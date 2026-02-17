package constructpro.DAO;

import constructpro.DTO.PaymentCheck;
import java.sql.*;

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
                    java.sql.Date pDate = rs.getDate("paymentDate");
                    if (pDate != null)
                        check.setPaymentDay(pDate.toLocalDate());
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

    public PaymentCheck getPaymentCheckById(int id) throws SQLException {
        PaymentCheck paymentCheck = null;
        String sql = "SELECT id, salaryRecordId, siteId, paymentDate, baseSalary, paidAmount FROM paymentCheck WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    paymentCheck = new PaymentCheck();
                    paymentCheck.setId(rs.getInt("id"));
                    paymentCheck.setSalaryrecordId(rs.getInt("salaryRecordId"));
                    paymentCheck.setSiteId(rs.getInt("siteId"));
                    java.sql.Date pDate = rs.getDate("paymentDate");
                    if (pDate != null) {
                        paymentCheck.setPaymentDay(pDate.toLocalDate());
                    }
                    paymentCheck.setBaseSalary(rs.getDouble("baseSalary"));
                    paymentCheck.setPaidAmount(rs.getDouble("paidAmount"));
                }
            }
        }

        return paymentCheck;
    }

    public void updatePaymentCheck(PaymentCheck paymentCheck) throws SQLException {
        String sql = "UPDATE paymentCheck SET paymentDate = ?, baseSalary = ?, paidAmount = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(paymentCheck.getPaymentDay()));
            stmt.setDouble(2, paymentCheck.getBaseSalary());
            stmt.setDouble(3, paymentCheck.getPaidAmount());
            stmt.setInt(4, paymentCheck.getId());
            stmt.executeUpdate();
        }
    }

    public double getTotalPaidForWorkerOnSite(int workerId, int siteId) throws SQLException {
        String sql = "SELECT SUM(pc.paidAmount) FROM paymentCheck pc " +
                "JOIN salaryRecord sr ON pc.salaryRecordId = sr.id " +
                "WHERE sr.workerId = ? AND pc.siteId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, workerId);
            stmt.setInt(2, siteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    public ResultSet getPaymentChecksSummaryReport(int siteId, LocalDate start, LocalDate end) throws SQLException {
        String sql = """
                SELECT
                    CONCAT(w.firstName, ' ', w.lastName) as workerName,
                    COUNT(pc.id) as checkCount,
                    SUM(pc.paidAmount) as totalPaid
                FROM paymentCheck pc
                INNER JOIN salaryRecord sr ON pc.salaryRecordId = sr.id
                INNER JOIN worker w ON sr.workerId = w.id
                WHERE pc.siteId = ? AND pc.paymentDate BETWEEN ? AND ?
                GROUP BY w.id
                ORDER BY workerName
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, siteId);
        ps.setDate(2, Date.valueOf(start));
        ps.setDate(3, Date.valueOf(end));
        return ps.executeQuery();
    }

    public ResultSet getDetailedPaymentCheckReport(int siteId, LocalDate start, LocalDate end) throws SQLException {
        String sql = """
                SELECT
                    CONCAT(w.firstName, ' ', w.lastName) as workerName,
                    pc.paymentDate,
                    pc.paidAmount
                FROM paymentCheck pc
                INNER JOIN salaryRecord sr ON pc.salaryRecordId = sr.id
                INNER JOIN worker w ON sr.workerId = w.id
                WHERE pc.siteId = ? AND pc.paymentDate BETWEEN ? AND ?
                ORDER BY workerName, pc.paymentDate
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, siteId);
        ps.setDate(2, Date.valueOf(start));
        ps.setDate(3, Date.valueOf(end));
        return ps.executeQuery();
    }
}
