package constructpro.DAO;

import constructpro.DTO.Attendance;
import constructpro.Database.ConnectionEstablish;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AttendanceDAO {
    
    Connection connection;
    Statement st;
    
    public AttendanceDAO(){
        try {
            connection = new ConnectionEstablish().getConn();
            st = connection.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    // CREATE - Mark attendance for a worker
    public boolean markAttendance(Attendance attendance) throws SQLException {
        String sql = """
            INSERT INTO attendance (worker_id, attendance_date, is_present, hours_worked, notes)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            is_present = VALUES(is_present),
            hours_worked = VALUES(hours_worked),
            notes = VALUES(notes)
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, attendance.getWorkerId());
            pstmt.setDate(2, Date.valueOf(attendance.getAttendanceDate()));
            pstmt.setBoolean(3, attendance.isPresent());
            pstmt.setDouble(4, attendance.getHoursWorked());
            pstmt.setString(5, attendance.getNotes());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0 && attendance.getId() == 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        attendance.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
            return affectedRows > 0;
        }
    }
    
    // CREATE - Bulk mark attendance for multiple workers
    public boolean markBulkAttendance(List<Attendance> attendanceList) throws SQLException {
        String sql = """
            INSERT INTO attendance (worker_id, attendance_date, is_present, hours_worked, notes)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            is_present = VALUES(is_present),
            hours_worked = VALUES(hours_worked),
            notes = VALUES(notes)
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            connection.setAutoCommit(false);
            
            for (Attendance attendance : attendanceList) {
                pstmt.setInt(1, attendance.getWorkerId());
                pstmt.setDate(2, Date.valueOf(attendance.getAttendanceDate()));
                pstmt.setBoolean(3, attendance.isPresent());
                pstmt.setDouble(4, attendance.getHoursWorked());
                pstmt.setString(5, attendance.getNotes());
                pstmt.addBatch();
            }
            
            int[] results = pstmt.executeBatch();
            connection.commit();
            
            return results.length == attendanceList.size();
        } catch (SQLException e) {
            throw e;
        }
    }
    
    // READ - Get attendance by ID
    public Attendance getAttendanceById(int id) throws SQLException {
        String sql = """
            SELECT id, worker_id, attendance_date, is_present, hours_worked, notes
            FROM attendance 
            WHERE id = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAttendance(rs);
                }
            }
        }
        return null;
    }
    
    // READ - Get attendance for specific worker and date
    public Attendance getAttendance(int workerId, LocalDate date) throws SQLException {
        String sql = """
            SELECT id, worker_id, attendance_date, is_present, hours_worked, notes
            FROM attendance 
            WHERE worker_id = ? AND attendance_date = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, workerId);
            pstmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAttendance(rs);
                }
            }
        }
        return null;
    }
    
    // READ - Get worker's attendance for a date range
    public List<Attendance> getWorkerAttendance(int workerId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = """
            SELECT id, worker_id, attendance_date, is_present, hours_worked, notes
            FROM attendance 
            WHERE worker_id = ? AND attendance_date BETWEEN ? AND ?
            ORDER BY attendance_date
            """;
        
        List<Attendance> attendanceList = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, workerId);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    attendanceList.add(mapResultSetToAttendance(rs));
                }
            }
        }
        
        return attendanceList;
    }
    
    // READ - Get all workers' attendance for a specific date
    public List<Attendance> getDateAttendance(LocalDate date) throws SQLException {
        String sql = """
            SELECT id, worker_id, attendance_date, is_present, hours_worked, notes
            FROM attendance 
            WHERE attendance_date = ?
            ORDER BY worker_id
            """;
        
        List<Attendance> attendanceList = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    attendanceList.add(mapResultSetToAttendance(rs));
                }
            }
        }
        
        return attendanceList;
    }
    
    // READ - Get attendance summary for date range
    public Map<Integer, Map<String, Object>> getAttendanceSummary(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = """
            SELECT 
                worker_id,
                COUNT(*) as total_days,
                SUM(CASE WHEN is_present THEN 1 ELSE 0 END) as present_days,
                SUM(CASE WHEN is_present THEN 0 ELSE 1 END) as absent_days,
                SUM(hours_worked) as total_hours,
                AVG(CASE WHEN is_present THEN hours_worked ELSE NULL END) as avg_hours_per_day
            FROM attendance 
            WHERE attendance_date BETWEEN ? AND ?
            GROUP BY worker_id
            """;
        
        Map<Integer, Map<String, Object>> summary = new HashMap<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> workerSummary = new HashMap<>();
                    workerSummary.put("totalDays", rs.getInt("total_days"));
                    workerSummary.put("presentDays", rs.getInt("present_days"));
                    workerSummary.put("absentDays", rs.getInt("absent_days"));
                    workerSummary.put("totalHours", rs.getDouble("total_hours"));
                    workerSummary.put("avgHoursPerDay", rs.getDouble("avg_hours_per_day"));
                    
                    summary.put(rs.getInt("worker_id"), workerSummary);
                }
            }
        }
        
        return summary;
    }
    
    // READ - Count present days for worker in date range
    public int getPresentDaysCount(int workerId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = """
            SELECT COUNT(*) as present_days
            FROM attendance 
            WHERE worker_id = ? AND attendance_date BETWEEN ? AND ? AND is_present = TRUE
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, workerId);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("present_days");
                }
            }
        }
        
        return 0;
    }
    
    // READ - Get workers who were absent on a specific date
    public List<Integer> getAbsentWorkers(LocalDate date) throws SQLException {
        String sql = """
            SELECT DISTINCT w.id
            FROM worker w
            LEFT JOIN attendance a ON w.id = a.worker_id AND a.attendance_date = ?
            WHERE (a.is_present IS NULL OR a.is_present = FALSE) AND w.is_active = TRUE
            """;
        
        List<Integer> absentWorkers = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    absentWorkers.add(rs.getInt("id"));
                }
            }
        }
        
        return absentWorkers;
    }
    
    // UPDATE - Update attendance record
    public boolean updateAttendance(Attendance attendance) throws SQLException {
        String sql = """
            UPDATE attendance 
            SET is_present = ?, hours_worked = ?, notes = ?
            WHERE id = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, attendance.isPresent());
            pstmt.setDouble(2, attendance.getHoursWorked());
            pstmt.setString(3, attendance.getNotes());
            pstmt.setInt(4, attendance.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // DELETE - Delete attendance record
    public boolean deleteAttendance(int id) throws SQLException {
        String sql = "DELETE FROM attendance WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // DELETE - Delete attendance for worker and date
    public boolean deleteAttendance(int workerId, LocalDate date) throws SQLException {
        String sql = "DELETE FROM attendance WHERE worker_id = ? AND attendance_date = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, workerId);
            pstmt.setDate(2, Date.valueOf(date));
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // UTILITY - Check if attendance exists for worker and date
    public boolean attendanceExists(int workerId, LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM attendance WHERE worker_id = ? AND attendance_date = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, workerId);
            pstmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    // UTILITY - Get attendance percentage for worker
    public double getAttendancePercentage(int workerId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = """
            SELECT 
                COUNT(*) as total_days,
                SUM(CASE WHEN is_present THEN 1 ELSE 0 END) as present_days
            FROM attendance 
            WHERE worker_id = ? AND attendance_date BETWEEN ? AND ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, workerId);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int totalDays = rs.getInt("total_days");
                    int presentDays = rs.getInt("present_days");
                    
                    if (totalDays == 0) return 0.0;
                    return (presentDays * 100.0) / totalDays;
                }
            }
        }
        
        return 0.0;
    }
    
    // HELPER - Map ResultSet to Attendance object
    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        
        attendance.setId(rs.getInt("id"));
        attendance.setWorkerId(rs.getInt("worker_id"));
        attendance.setAttendanceDate(rs.getDate("attendance_date").toLocalDate());
        attendance.setPresent(rs.getBoolean("is_present"));
        attendance.setHoursWorked(rs.getDouble("hours_worked"));
        attendance.setNotes(rs.getString("notes"));
        
        return attendance;
    }
}