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

    // Sync method to update all salary record totals from paymentCheck
    private void updateAllSalaryRecordTotals() throws SQLException {
        String sql = """
                    UPDATE salaryRecord sr
                    LEFT JOIN (
                        SELECT salaryRecordId, SUM(baseSalary) as te, SUM(paidAmount) as tp
                        FROM paymentCheck
                        GROUP BY salaryRecordId
                    ) pc ON sr.id = pc.salaryRecordId
                    SET sr.totalEarned = COALESCE(pc.te, 0),
                        sr.totalPaid = COALESCE(pc.tp, 0)
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    // Sync method for a specific salary record (optimization)
    public void updateSalaryRecordTotals(int salaryRecordId) throws SQLException {
        String sql = """
                    UPDATE salaryRecord sr
                    LEFT JOIN (
                        SELECT salaryRecordId, SUM(baseSalary) as te, SUM(paidAmount) as tp
                        FROM paymentCheck
                        WHERE salaryRecordId = ?
                        GROUP BY salaryRecordId
                    ) pc ON sr.id = pc.salaryRecordId
                    SET sr.totalEarned = COALESCE(pc.te, 0),
                        sr.totalPaid = COALESCE(pc.tp, 0)
                    WHERE sr.id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, salaryRecordId);
            ps.setInt(2, salaryRecordId);
            ps.executeUpdate();
        }
    }

    public SalaryRecord getSalaryRecordByWorkerId(int workerId) throws SQLException {
        // First sync the totals for this worker's salary record
        SalaryRecord existingRecord = getSalaryRecordByWorkerIdWithoutSync(workerId);
        if (existingRecord != null) {
            updateSalaryRecordTotals(existingRecord.getId());
        }

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

    // Helper method to get record without syncing (used internally)
    private SalaryRecord getSalaryRecordByWorkerIdWithoutSync(int workerId) throws SQLException {
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

    public SalaryRecord getSalaryRecordById(int id) throws SQLException {
        updateSalaryRecordTotals(id); // Sync totals before fetching
        String sql = "SELECT id, workerId, totalEarned, totalPaid FROM salaryRecord WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
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
}
