package constructpro.DAO;

import constructpro.DTO.PaymentCheck;
import java.sql.*;
import constructpro.Database.SQLiteDateUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentCheckDAO {

    private Connection connection;

    public PaymentCheckDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertPaymentCheck(int salary_record_id, int siteId, LocalDate payment_date, double base_salary,
            double paid_amount) throws SQLException {
        String sql = "INSERT INTO paymentCheck (salaryRecordId, siteId, paymentDate, baseSalary, paidAmount) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, salary_record_id);
            stmt.setInt(2, siteId);
            stmt.setDate(3, Date.valueOf(payment_date));
            stmt.setDouble(4, base_salary);
            stmt.setDouble(5, paid_amount);
            stmt.executeUpdate();
        }
    }

    public List<PaymentCheck> getAllWorkerPaymentChecks(int salaryRecordId) throws SQLException {
        List<PaymentCheck> checks = new ArrayList<>();
        String sql = "SELECT id, paymentDate, baseSalary, paidAmount FROM paymentCheck WHERE salaryRecordId = ? ORDER BY paymentDate";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, salaryRecordId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PaymentCheck check = new PaymentCheck();
                    check.setId(rs.getInt("id"));
                    check.setPaymentDay(SQLiteDateUtils.getDate(rs, "paymentDate"));
                    check.setBaseSalary(rs.getDouble("baseSalary"));
                    check.setPaidAmount(rs.getDouble("paidAmount"));
                    checks.add(check);
                }
            }
        }
        return checks;
    }

    public void deletePaymentCheck(int id) throws SQLException {
        String sql = "DELETE FROM paymentCheck WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Get all payment checks for a specific site and date with worker information
     * Used for generating payment check PDF reports
     */
    public ResultSet getPaymentChecksBySiteAndDate(int siteId, LocalDate paymentDate) throws SQLException {
        String sql = """
                SELECT
                    pc.id,
                    pc.paymentDate,
                    pc.baseSalary,
                    pc.paidAmount,
                    w.id as worker_id,
                    w.firstName,
                    w.lastName,
                    w.job,
                    sr.totalEarned,
                    sr.totalPaid
                FROM paymentCheck pc
                INNER JOIN salaryRecord sr ON pc.salaryRecordId = sr.id
                INNER JOIN worker w ON sr.workerId = w.id
                WHERE pc.siteId = ? AND pc.paymentDate = ?
                ORDER BY w.lastName, w.firstName
                """;

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, siteId);
        stmt.setDate(2, Date.valueOf(paymentDate));
        return stmt.executeQuery();
    }
}
