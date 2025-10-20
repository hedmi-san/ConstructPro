package constructpro.DAO;

import constructpro.DTO.PaymentCheck;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentCheckDAO {
    private Connection connection;

    public PaymentCheckDAO(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * Get all payment checks for a specific salary record
     */
    public List<PaymentCheck> getBySalaryRecordId(int salaryRecordId) throws SQLException {
        List<PaymentCheck> checks = new ArrayList<>();
        String sql = "SELECT id, salary_record_id, payment_date, base_salary, paid_amount " +
                     "FROM payment_check WHERE salary_record_id = ? ORDER BY payment_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, salaryRecordId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    checks.add(mapResultSetToPaymentCheck(rs));
                }
            }
        }
        return checks;
    }
    
    /**
     * Get a payment check by ID
     */
    public PaymentCheck getById(int id) throws SQLException {
        String sql = "SELECT id, salary_record_id, payment_date, base_salary, paid_amount " +
                     "FROM payment_check WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPaymentCheck(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get all payment checks
     */
    public List<PaymentCheck> getAllPaymentChacks() throws SQLException {
        List<PaymentCheck> checks = new ArrayList<>();
        String sql = "SELECT id, salary_record_id, payment_date, base_salary, paid_amount " +
                     "FROM payment_check ORDER BY payment_date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                checks.add(mapResultSetToPaymentCheck(rs));
            }
        }
        return checks;
    }
    
    /**
     * Insert a new payment check
     */
    public PaymentCheck insert(PaymentCheck check) throws SQLException {
        String sql = "INSERT INTO payment_check (salary_record_id, payment_date, base_salary, paid_amount) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, check.getSalaryrecordId());
            stmt.setDate(2, Date.valueOf(check.getPaymentDay()));
            stmt.setDouble(3, check.getBaseSalary());
            stmt.setDouble(4, check.getPaidAmount());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating payment check failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    check.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating payment check failed, no ID obtained.");
                }
            }
        }
        return check;
    }
    
    /**
     * Update an existing payment check
     */
    public void update(PaymentCheck check) throws SQLException {
        String sql = "UPDATE payment_check SET payment_date = ?, base_salary = ?, paid_amount = ? " +
                     "WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(check.getPaymentDay()));
            stmt.setDouble(2, check.getBaseSalary());
            stmt.setDouble(3, check.getPaidAmount());
            stmt.setInt(4, check.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating payment check failed, no rows affected.");
            }
        }
    }
    
    /**
     * Delete a payment check
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM payment_check WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Get total base salary for a salary record
     */
    public double getTotalBaseSalary(int salaryRecordId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(base_salary), 0) as total FROM payment_check " +
                     "WHERE salary_record_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, salaryRecordId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }
    
    /**
     * Get total paid amount for a salary record
     */
    public double getTotalPaidAmount(int salaryRecordId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(paid_amount), 0) as total FROM payment_check " +
                     "WHERE salary_record_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, salaryRecordId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }
    
    /**
     * Helper method to map ResultSet to PaymentCheck object
     */
    private PaymentCheck mapResultSetToPaymentCheck(ResultSet rs) throws SQLException {
        PaymentCheck check = new PaymentCheck();
        check.setId(rs.getInt("id"));
        check.setSalaryrecordId(rs.getInt("salary_record_id"));
        check.setPaymentDay(rs.getDate("payment_date").toLocalDate());
        check.setBaseSalary(rs.getDouble("base_salary"));
        check.setPaidAmount(rs.getDouble("paid_amount"));
        return check;
    }
}
