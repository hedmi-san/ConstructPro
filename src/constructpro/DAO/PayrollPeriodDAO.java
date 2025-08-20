package constructpro.DAO;

import constructpro.DTO.PayrollPeriod;
import constructpro.Database.ConnectionEstablish;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class PayrollPeriodDAO {
    
    Connection connection;
    Statement st;
    
    public PayrollPeriodDAO(){
        try {
            connection = new ConnectionEstablish().getConn();
            st = connection.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    // CREATE - Add new payroll period
    public boolean addPayrollPeriod(PayrollPeriod period) throws SQLException {
        String sql = """
            INSERT INTO payroll_period (start_date, end_date, payment_date, period_type, is_processed, is_paid)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setDate(1, Date.valueOf(period.getStartDate()));
            pstmt.setDate(2, Date.valueOf(period.getEndDate()));
            pstmt.setDate(3, Date.valueOf(period.getPaymentDate()));
            pstmt.setString(4, period.getPeriodType());
            pstmt.setBoolean(5, period.isProcessed());
            pstmt.setBoolean(6, period.isPaid());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        period.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    // CREATE - Generate payroll periods for a year
    public boolean generatePayrollPeriodsForYear(int year) throws SQLException {
        List<PayrollPeriod> periods = new ArrayList<>();
        
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            
            // First half (1st to 15th)
            LocalDate firstHalfStart = LocalDate.of(year, month, 1);
            LocalDate firstHalfEnd = LocalDate.of(year, month, 15);
            PayrollPeriod firstHalf = new PayrollPeriod(firstHalfStart, firstHalfEnd, "FIRST_HALF");
            periods.add(firstHalf);
            
            // Second half (16th to end of month)
            LocalDate secondHalfStart = LocalDate.of(year, month, 16);
            LocalDate secondHalfEnd = yearMonth.atEndOfMonth();
            PayrollPeriod secondHalf = new PayrollPeriod(secondHalfStart, secondHalfEnd, "SECOND_HALF");
            periods.add(secondHalf);
        }
        
        // Batch insert all periods
        return addMultiplePayrollPeriods(periods);
    }
    
    // CREATE - Add multiple payroll periods (batch)
    public boolean addMultiplePayrollPeriods(List<PayrollPeriod> periods) throws SQLException {
        String sql = """
            INSERT IGNORE INTO payroll_period (start_date, end_date, payment_date, period_type, is_processed, is_paid)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            connection.setAutoCommit(false);
            
            for (PayrollPeriod period : periods) {
                pstmt.setDate(1, Date.valueOf(period.getStartDate()));
                pstmt.setDate(2, Date.valueOf(period.getEndDate()));
                pstmt.setDate(3, Date.valueOf(period.getPaymentDate()));
                pstmt.setString(4, period.getPeriodType());
                pstmt.setBoolean(5, period.isProcessed());
                pstmt.setBoolean(6, period.isPaid());
                pstmt.addBatch();
            }
            
            int[] results = pstmt.executeBatch();
            connection.commit();
            
            return results.length > 0;
        } catch (SQLException e) {
            throw e;
        }
    }
    
    // READ - Get payroll period by ID
    public PayrollPeriod getPayrollPeriodById(int id) throws SQLException {
        String sql = """
            SELECT id, start_date, end_date, payment_date, period_type, is_processed, is_paid
            FROM payroll_period
            WHERE id = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayrollPeriod(rs);
                }
            }
        }
        return null;
    }
    
    // READ - Get current payroll period (containing today's date)
    public PayrollPeriod getCurrentPayrollPeriod() throws SQLException {
        return getPayrollPeriodForDate(LocalDate.now());
    }
    
    // READ - Get payroll period for specific date
    public PayrollPeriod getPayrollPeriodForDate(LocalDate date) throws SQLException {
        String sql = """
            SELECT id, start_date, end_date, payment_date, period_type, is_processed, is_paid
            FROM payroll_period
            WHERE ? BETWEEN start_date AND end_date
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayrollPeriod(rs);
                }
            }
        }
        return null;
    }
    
    // READ - Get previous payroll period
    public PayrollPeriod getPreviousPayrollPeriod() throws SQLException {
        String sql = """
            SELECT id, start_date, end_date, payment_date, period_type, is_processed, is_paid
            FROM payroll_period
            WHERE end_date < CURDATE()
            ORDER BY end_date DESC
            LIMIT 1
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return mapResultSetToPayrollPeriod(rs);
            }
        }
        return null;
    }
    
    // READ - Get next payroll period
    public PayrollPeriod getNextPayrollPeriod() throws SQLException {
        String sql = """
            SELECT id, start_date, end_date, payment_date, period_type, is_processed, is_paid
            FROM payroll_period
            WHERE start_date > CURDATE()
            ORDER BY start_date ASC
            LIMIT 1
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return mapResultSetToPayrollPeriod(rs);
            }
        }
        return null;
    }
    
    // READ - Get all payroll periods for a year
    public List<PayrollPeriod> getPayrollPeriodsForYear(int year) throws SQLException {
        String sql = """
            SELECT id, start_date, end_date, payment_date, period_type, is_processed, is_paid
            FROM payroll_period
            WHERE YEAR(start_date) = ?
            ORDER BY start_date
            """;
        
        List<PayrollPeriod> periods = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    periods.add(mapResultSetToPayrollPeriod(rs));
                }
            }
        }
        
        return periods;
    }
    
    // READ - Get all payroll periods for a month
    public List<PayrollPeriod> getPayrollPeriodsForMonth(int year, int month) throws SQLException {
        String sql = """
            SELECT id, start_date, end_date, payment_date, period_type, is_processed, is_paid
            FROM payroll_period
            WHERE YEAR(start_date) = ? AND MONTH(start_date) = ?
            ORDER BY start_date
            """;
        
        List<PayrollPeriod> periods = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    periods.add(mapResultSetToPayrollPeriod(rs));
                }
            }
        }
        
        return periods;
    }
    
    // READ - Get unprocessed payroll periods
    public List<PayrollPeriod> getUnprocessedPeriods() throws SQLException {
        String sql = """
            SELECT id, start_date, end_date, payment_date, period_type, is_processed, is_paid
            FROM payroll_period
            WHERE is_processed = FALSE AND end_date <= CURDATE()
            ORDER BY end_date
            """;
        
        List<PayrollPeriod> periods = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                periods.add(mapResultSetToPayrollPeriod(rs));
            }
        }
        
        return periods;
    }
    
    // READ - Get unpaid payroll periods
    public List<PayrollPeriod> getUnpaidPeriods() throws SQLException {
        String sql = """
            SELECT id, start_date, end_date, payment_date, period_type, is_processed, is_paid
            FROM payroll_period
            WHERE is_processed = TRUE AND is_paid = FALSE
            ORDER BY payment_date
            """;
        
        List<PayrollPeriod> periods = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                periods.add(mapResultSetToPayrollPeriod(rs));
            }
        }
        
        return periods;
    }
    
    // READ - Get periods due for payment (payment date <= today)
    public List<PayrollPeriod> getPeriodsDueForPayment() throws SQLException {
        String sql = """
            SELECT id, start_date, end_date, payment_date, period_type, is_processed, is_paid
            FROM payroll_period
            WHERE is_processed = TRUE AND is_paid = FALSE AND payment_date <= CURDATE()
            ORDER BY payment_date
            """;
        
        List<PayrollPeriod> periods = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                periods.add(mapResultSetToPayrollPeriod(rs));
            }
        }
        
        return periods;
    }
    
    // UPDATE - Mark period as processed
    public boolean markPeriodAsProcessed(int periodId) throws SQLException {
        String sql = "UPDATE payroll_period SET is_processed = TRUE WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, periodId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // UPDATE - Mark period as paid
    public boolean markPeriodAsPaid(int periodId) throws SQLException {
        String sql = "UPDATE payroll_period SET is_paid = TRUE WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, periodId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // UPDATE - Update payment date
    public boolean updatePaymentDate(int periodId, LocalDate newPaymentDate) throws SQLException {
        String sql = "UPDATE payroll_period SET payment_date = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(newPaymentDate));
            pstmt.setInt(2, periodId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // UPDATE - Update payroll period
    public boolean updatePayrollPeriod(PayrollPeriod period) throws SQLException {
        String sql = """
            UPDATE payroll_period 
            SET start_date = ?, end_date = ?, payment_date = ?, period_type = ?, is_processed = ?, is_paid = ?
            WHERE id = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(period.getStartDate()));
            pstmt.setDate(2, Date.valueOf(period.getEndDate()));
            pstmt.setDate(3, Date.valueOf(period.getPaymentDate()));
            pstmt.setString(4, period.getPeriodType());
            pstmt.setBoolean(5, period.isProcessed());
            pstmt.setBoolean(6, period.isPaid());
            pstmt.setInt(7, period.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // DELETE - Delete payroll period (not recommended - use with caution)
    public boolean deletePayrollPeriod(int periodId) throws SQLException {
        String sql = "DELETE FROM payroll_period WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, periodId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // UTILITY - Check if period exists for date range
    public boolean periodExistsForDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM payroll_period 
            WHERE start_date = ? AND end_date = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    // UTILITY - Get total periods for year
    public int getTotalPeriodsForYear(int year) throws SQLException {
        String sql = "SELECT COUNT(*) FROM payroll_period WHERE YEAR(start_date) = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    // UTILITY - Auto-generate missing periods up to current date
    public boolean generateMissingPeriods() throws SQLException {
        int currentYear = LocalDate.now().getYear();
        
        // Generate periods for current year if they don't exist
        if (getTotalPeriodsForYear(currentYear) == 0) {
            return generatePayrollPeriodsForYear(currentYear);
        }
        
        return true; // Periods already exist
    }
    
    // HELPER - Map ResultSet to PayrollPeriod object
    private PayrollPeriod mapResultSetToPayrollPeriod(ResultSet rs) throws SQLException {
        PayrollPeriod period = new PayrollPeriod();
        
        period.setId(rs.getInt("id"));
        period.setStartDate(rs.getDate("start_date").toLocalDate());
        period.setEndDate(rs.getDate("end_date").toLocalDate());
        period.setPaymentDate(rs.getDate("payment_date").toLocalDate());
        period.setPeriodType(rs.getString("period_type"));
        period.setProcessed(rs.getBoolean("is_processed"));
        period.setPaid(rs.getBoolean("is_paid"));
        
        return period;
    }
}