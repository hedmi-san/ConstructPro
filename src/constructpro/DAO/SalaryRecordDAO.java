package constructpro.DAO;

import constructpro.DTO.SalaryRecord;
import java.sql.*;

public class SalaryRecordDAO {

    private Connection connection;

    public SalaryRecordDAO(Connection connection) {
        this.connection = connection;
    }

    public SalaryRecord insertSalaryRecord(SalaryRecord record) throws SQLException {
        String sql = "INSERT INTO salaryRecord (workerId, totalEarned, totalPaid) VALUES (?, ?, ?)";

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
        String sql = "UPDATE salaryRecord SET totalEarned = ?, totalPaid = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, record.getTotalEarned());
            stmt.setDouble(2, record.getAmountPaid());
            stmt.setInt(3, record.getId());
            stmt.executeUpdate();
        }
    }

    public SalaryRecord getSalaryRecordByWorkerId(int workerId) throws SQLException {
        String sql = "SELECT id, workerId, totalEarned, totalPaid FROM salaryRecord WHERE workerId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, workerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SalaryRecord record = new SalaryRecord();
                    record.setId(rs.getInt("id"));
                    record.setWorkerId(rs.getInt("workerId"));
                    record.setTotalEarned(rs.getDouble("totalEarned"));
                    record.setAmountPaid(rs.getDouble("totalPaid"));
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
