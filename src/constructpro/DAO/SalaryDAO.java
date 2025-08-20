package constructpro.DAO;

import constructpro.DTO.Salary;
import constructpro.Database.ConnectionEstablish;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SalaryDAO {
    
    Connection connection;
    Statement st;
    
    public SalaryDAO(){
        try {
            connection = new ConnectionEstablish().getConn();
            st = connection.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    // CREATE - Add salary payment record
    public boolean addSalary(Salary salary) throws SQLException {
        String sql = """
            INSERT INTO salary (worker_id, payroll_period_id, payment_date, days_worked, daily_rate, 
                              total_earned, amount_paid, retained_amount, payment_percentage, notes, is_paid)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, salary.getWorkerId());
            pstmt.setInt(2, salary.getPayrollPeriodId());
            pstmt.setDate(3, salary.getPaymentDate() != null ? Date.valueOf(salary.getPaymentDate()) : null);
            pstmt.setInt(4, salary.getDaysWorked());
            pstmt.setDouble(5, salary.getDailyRate());
            pstmt.setDouble(6, salary.getTotalEarned());
            pstmt.setDouble(7, salary.getAmountPaid());
            pstmt.setDouble(8, salary.getRetainedAmount());
            pstmt.setDouble(9, salary.getPaymentPercentage());
            pstmt.setString(10, salary.getNotes());
            pstmt.setBoolean(11, salary.isPaid());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        salary.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    // CREATE - Process payroll for a period (bulk creation)
    public boolean processPayrollForPeriod(int payrollPeriodId) throws SQLException {
        // This method calculates and creates salary records for all active workers in a payroll period
        String sql = """
            INSERT INTO salary (worker_id, payroll_period_id, days_worked, daily_rate, 
                              total_earned, amount_paid, retained_amount, payment_percentage, is_paid)
            SELECT 
                w.id as worker_id,
                ? as payroll_period_id,
                COALESCE(att_summary.present_days, 0) as days_worked,
                COALESCE(wsc.daily_rate, 0) as daily_rate,
                COALESCE(att_summary.present_days, 0) * COALESCE(wsc.daily_rate, 0) as total_earned,
                (COALESCE(att_summary.present_days, 0) * COALESCE(wsc.daily_rate, 0)) * (COALESCE(wsc.payment_percentage, 70) / 100) as amount_paid,
                (COALESCE(att_summary.present_days, 0) * COALESCE(wsc.daily_rate, 0)) * (1 - COALESCE(wsc.payment_percentage, 70) / 100) as retained_amount,
                COALESCE(wsc.payment_percentage, 70) as payment_percentage,
                FALSE as is_paid
            FROM worker w
            JOIN payroll_period pp ON pp.id = ?
            LEFT JOIN worker_salary_config wsc ON w.id = wsc.worker_id AND wsc.is_active = TRUE
            LEFT JOIN (
                SELECT 
                    worker_id,
                    SUM(CASE WHEN is_present THEN 1 ELSE 0 END) as present_days
                FROM attendance a
                JOIN payroll_period pp2 ON pp2.id = ? AND a.attendance_date BETWEEN pp2.start_date AND pp2.end_date
                GROUP BY worker_id
            ) att_summary ON w.id = att_summary.worker_id
            WHERE w.is_active = TRUE
            AND NOT EXISTS (
                SELECT 1 FROM salary s WHERE s.worker_id = w.id AND s.payroll_period_id = ?
            )
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, payrollPeriodId);
            pstmt.setInt(2, payrollPeriodId);
            pstmt.setInt(3, payrollPeriodId);
            pstmt.setInt(4, payrollPeriodId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // READ - Get salary by ID
    public Salary getSalaryById(int id) throws SQLException {
        String sql = """
            SELECT id, worker_id, payroll_period_id, payment_date, days_worked, daily_rate,
                   total_earned, amount_paid, retained_amount, payment_percentage, notes, is_paid
            FROM salary
            WHERE id = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSalary(rs);
                }
            }
        }
        return null;
    }
    
    // READ - Get salary for worker in specific payroll period
    public Salary getSalaryForWorkerInPeriod(int workerId, int payrollPeriodId) throws SQLException {
        String sql = """
            SELECT id, worker_id, payroll_period_id, payment_date, days_worked, daily_rate,
                   total_earned, amount_paid, retained_amount, payment_percentage, notes, is_paid
            FROM salary
            WHERE worker_id = ? AND payroll_period_id = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, workerId);
            pstmt.setInt(2, payrollPeriodId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSalary(rs);
                }
            }
        }
        return null;
    }
    
    // READ - Get all salaries for a worker
    public List<Salary> getSalariesForWorker(int workerId) throws SQLException {
        String sql = """
            SELECT s.id, s.worker_id, s.payroll_period_id, s.payment_date, s.days_worked, s.daily_rate,
                   s.total_earned, s.amount_paid, s.retained_amount, s.payment_percentage, s.notes, s.is_paid
            FROM salary s
            JOIN payroll_period pp ON s.payroll_period_id = pp.id
            WHERE s.worker_id = ?
            ORDER BY pp.start_date DESC
            """;
        
        List<Salary> salaries = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, workerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    salaries.add(mapResultSetToSalary(rs));
                }
            }
        }
        
        return salaries;
    }
    
    // READ - Get recent salaries for worker (last N payments)
    public List<Salary> getRecentSalariesForWorker(int workerId, int limit) throws SQLException {
        String sql = """
            SELECT s.id, s.worker_id, s.payroll_period_id, s.payment_date, s.days_worked, s.daily_rate,
                   s.total_earned, s.amount_paid, s.retained_amount, s.payment_percentage, s.notes, s.is_paid
            FROM salary s
            JOIN payroll_period pp ON s.payroll_period_id = pp.id
            WHERE s.worker_id = ?
            ORDER BY pp.start_date DESC
            LIMIT ?
            """;
        
        List<Salary> salaries = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, workerId);
            pstmt.setInt(2, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    salaries.add(mapResultSetToSalary(rs));
                }
            }
        }
        
        return salaries;
    }
    
    // READ - Get all salaries for a payroll period
    public List<Salary> getSalariesForPeriod(int payrollPeriodId) throws SQLException {
        String sql = """
            SELECT s.id, s.worker_id, s.payroll_period_id, s.payment_date, s.days_worked, s.daily_rate,
                   s.total_earned, s.amount_paid, s.retained_amount, s.payment_percentage, s.notes, s.is_paid
            FROM salary s
            JOIN worker w ON s.worker_id = w.id
            WHERE s.payroll_period_id = ?
            ORDER BY w.first_name, w.last_name
            """;
        
        List<Salary> salaries = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, payrollPeriodId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    salaries.add(mapResultSetToSalary(rs));
                }
            }
        }
        
        return salaries;
    }
    
    // READ - Get unpaid salaries for a period
    public List<Salary> getUnpaidSalariesForPeriod(int payrollPeriodId) throws SQLException {
        String sql = """
            SELECT s.id, s.worker_id, s.payroll_period_id, s.payment_date, s.days_worked, s.daily_rate,
                   s.total_earned, s.amount_paid, s.retained_amount, s.payment_percentage, s.notes, s.is_paid
            FROM salary s
            JOIN worker w ON s.worker_id = w.id
            WHERE s.payroll_period_id = ? AND s.is_paid = FALSE
            ORDER BY w.first_name, w.last_name
            """;
        
        List<Salary> salaries = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, payrollPeriodId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    salaries.add(mapResultSetToSalary(rs));
                }
            }
        }
        
        return salaries;
    }
    
    // READ - Get salary statistics for a period
    public Map<String, Object> getPeriodSalaryStatistics(int payrollPeriodId) throws SQLException {
        String sql = """
            SELECT 
                COUNT(*) as total_workers,
                SUM(days_worked) as total_days_worked,
                SUM(total_earned) as total_earned,
                SUM(amount_paid) as total_paid,
                SUM(retained_amount) as total_retained,
                AVG(days_worked) as avg_days_worked,
                AVG(daily_rate) as avg_daily_rate,
                COUNT(CASE WHEN is_paid THEN 1 END) as workers_paid,
                COUNT(CASE WHEN NOT is_paid THEN 1 END) as workers_unpaid
            FROM salary
            WHERE payroll_period_id = ?
            """;
        
        Map<String, Object> statistics = new HashMap<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, payrollPeriodId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    statistics.put("totalWorkers", rs.getInt("total_workers"));
                    statistics.put("totalDaysWorked", rs.getInt("total_days_worked"));
                    statistics.put("totalEarned", rs.getDouble("total_earned"));
                    statistics.put("totalPaid", rs.getDouble("total_paid"));
                    statistics.put("totalRetained", rs.getDouble("total_retained"));
                    statistics.put("avgDaysWorked", rs.getDouble("avg_days_worked"));
                    statistics.put("avgDailyRate", rs.getDouble("avg_daily_rate"));
                    statistics.put("workersPaid", rs.getInt("workers_paid"));
                    statistics.put("workersUnpaid", rs.getInt("workers_unpaid"));
                }
            }
        }
        
        return statistics;
    }
    
    // UPDATE - Update salary record
    public boolean updateSalary(Salary salary) throws SQLException {
        String sql = """
            UPDATE salary 
            SET payment_date = ?, days_worked = ?, daily_rate = ?, total_earned = ?,
                amount_paid = ?, retained_amount = ?, payment_percentage = ?, notes = ?, is_paid = ?
            WHERE id = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setDate(1, salary.getPaymentDate() != null ? Date.valueOf(salary.getPaymentDate()) : null);
            pstmt.setInt(2, salary.getDaysWorked());
            pstmt.setDouble(3, salary.getDailyRate());
            pstmt.setDouble(4, salary.getTotalEarned());
            pstmt.setDouble(5, salary.getAmountPaid());
            pstmt.setDouble(6, salary.getRetainedAmount());
            pstmt.setDouble(7, salary.getPaymentPercentage());
            pstmt.setString(8, salary.getNotes());
            pstmt.setBoolean(9, salary.isPaid());
            pstmt.setInt(10, salary.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // UPDATE - Mark salary as paid
    public boolean markSalaryAsPaid(int salaryId, LocalDate paymentDate) throws SQLException {
        String sql = "UPDATE salary SET is_paid = TRUE, payment_date = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(paymentDate));
            pstmt.setInt(2, salaryId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // UPDATE - Mark all salaries in period as paid
    
}