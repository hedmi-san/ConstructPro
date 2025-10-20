package constructpro.DAO;

import constructpro.DTO.PaymentCheck;
import constructpro.DTO.SalaryRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaryRecordDAO {
    
    private Connection connection;
    
    public SalaryRecordDAO(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * Get salary record by worker ID
     */
    public ResultSet getSalaryRecordByWorkerId(int workerId) throws SQLException {
        String sql = "SELECT id, worker_id, total_earned, total_paid " +
                     "FROM salary_record WHERE worker_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, workerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs;
                }
            }
        }
        return null;
    }

    public List<PaymentCheck> getAllPaymentChecks() throws SQLException {
        List<PaymentCheck> records = new ArrayList<>();
        String sql = "SELECT id, worker_id, total_earned, total_paid FROM salary_record";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                records.add((PaymentCheck) rs);
            }
        }
        return records;
    }
    
    /**
     * Create a new salary record for a worker
     */
    public SalaryRecord insert(SalaryRecord record) throws SQLException {
        String sql = "INSERT INTO salary_record (worker_id, total_earned, total_paid) " +
                     "VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, record.getWorkerId());
            stmt.setDouble(2, record.getTotalEarned());
            stmt.setDouble(3, record.getAmountPaid());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating salary record failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    record.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating salary record failed, no ID obtained.");
                }
            }
        }
        return record;
    }
    
    /**
     * Update an existing salary record
     */
    public void update(SalaryRecord record) throws SQLException {
        String sql = "UPDATE salary_record SET total_earned = ?, total_paid = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, record.getTotalEarned());
            stmt.setDouble(2, record.getAmountPaid());
            stmt.setInt(3, record.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating salary record failed, no rows affected.");
            }
        }
    }
    
    /**
     * Delete a salary record (this will cascade delete payment checks)
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM salary_record WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Helper method to map ResultSet to SalaryRecord object
     */
}
