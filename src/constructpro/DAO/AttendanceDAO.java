package constructpro.DAO;

import constructpro.DTO.Attendance;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class AttendanceDAO {
    private Connection conn;

    public AttendanceDAO(Connection conn) {
        this.conn = conn;
    }

    public void insert(Attendance attendance) throws SQLException {
        String sql = "INSERT INTO attendance (worker_id, attendance_date, is_present, notes) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attendance.getWorkerId());
            ps.setDate(2, Date.valueOf(attendance.getAttendanceDate()));
            ps.setBoolean(3, attendance.isPresent());
            ps.setString(4, attendance.getNotes());
            ps.executeUpdate();
        }
    }

    public void update(Attendance attendance) throws SQLException {
        String sql = "UPDATE attendance SET worker_id=?, attendance_date=?, is_present=?, notes=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attendance.getWorkerId());
            ps.setDate(2, Date.valueOf(attendance.getAttendanceDate()));
            ps.setBoolean(3, attendance.isPresent());
            ps.setString(4, attendance.getNotes());
            ps.setInt(5, attendance.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM attendance WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Attendance getById(int id) throws SQLException {
        String sql = "SELECT * FROM attendance WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Attendance a = new Attendance();
                a.setId(rs.getInt("id"));
                a.setWorkerId(rs.getInt("worker_id"));
                a.setAttendanceDate(rs.getDate("attendance_date").toLocalDate());
                a.setPresent(rs.getBoolean("is_present"));
                a.setNotes(rs.getString("notes"));
                return a;
            }
        }
        return null;
    }

    public List<Attendance> getAttendanceByWorker(int workerId) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE worker_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Attendance a = new Attendance();
                a.setId(rs.getInt("id"));
                a.setWorkerId(rs.getInt("worker_id"));
                a.setAttendanceDate(rs.getDate("attendance_date").toLocalDate());
                a.setPresent(rs.getBoolean("is_present"));
                a.setNotes(rs.getString("notes"));
                list.add(a);
            }
        }
        return list;
    }
}
