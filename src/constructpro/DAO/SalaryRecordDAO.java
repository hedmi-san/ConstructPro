package constructpro.DAO;

import constructpro.DTO.SalaryRecord;
import java.sql.*;

public class SalaryRecordDAO {

    private Connection connection;

    public SalaryRecordDAO(Connection connection) {
        this.connection = connection;
    }

    public SalaryRecord insertSalaryRecord(SalaryRecord record) throws SQLException {
        String sql = "INSERT INTO salary_record (worker_id, total_earned, total_paid) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, record.getWorkerId());
            stmt.setDouble(2, record.getTotalEarned());
            stmt.setDouble(3, record.getAmountPaid());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    record.setId(rs.getInt(1));
                }
            }
        }
        return record;
    }

    public void updateSalaryRecord(SalaryRecord record) throws SQLException {
        String sql = "UPDATE salary_record SET total_earned = ?, total_paid = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, record.getTotalEarned());
            stmt.setDouble(2, record.getAmountPaid());
            stmt.setInt(3, record.getId());
            stmt.executeUpdate();
        }
    }

    public SalaryRecord getSalaryRecordByWorkerId(int workerId) throws SQLException {
        String sql = "SELECT id, worker_id, total_earned, total_paid FROM salary_record WHERE worker_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, workerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SalaryRecord record = new SalaryRecord();
                    record.setId(rs.getInt("id"));
                    record.setWorkerId(rs.getInt("worker_id"));
                    record.setTotalEarned(rs.getDouble("total_earned"));
                    record.setAmountPaid(rs.getDouble("total_paid"));
                    return record;
                }
            }
        }
        return null;
    }

    /** 
     * If the record does not exist for this worker, create one 
     */
    public SalaryRecord getOrCreateSalaryRecord(int workerId) throws SQLException {
        SalaryRecord record = getSalaryRecordByWorkerId(workerId);
        if (record == null) {
            record = new SalaryRecord();
            record.setWorkerId(workerId);
            record.setTotalEarned(0);
            record.setAmountPaid(0);
            insertSalaryRecord(record);
        }
        return record;
    }
}
