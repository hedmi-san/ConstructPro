package constructpro.DAO;

import constructpro.DTO.PayrollPeriod;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollPeriodDAO {
    private Connection conn;

    public PayrollPeriodDAO(Connection conn) {
        this.conn = conn;
    }

    // Insert new payroll period
    public boolean addPayrollPeriod(PayrollPeriod period) throws SQLException {
        String sql = "INSERT INTO payroll_period (start_date, end_date, payment_date, period_type, is_processed, is_paid) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, Date.valueOf(period.getStartDate()));
            stmt.setDate(2, Date.valueOf(period.getEndDate()));
            stmt.setDate(3, Date.valueOf(period.getPaymentDate()));
            stmt.setString(4, period.getPeriodType().name());
            stmt.setBoolean(5, period.isProcessed());
            stmt.setBoolean(6, period.isPaid());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    period.setId(rs.getInt(1));
                }
            }
            return rows > 0;
        }
    }

    // Get payroll period by ID
    public PayrollPeriod getPayrollPeriodById(int id) throws SQLException {
        String sql = "SELECT * FROM payroll_period WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PayrollPeriod period = new PayrollPeriod();
                period.setId(rs.getInt("id"));
                period.setStartDate(rs.getDate("start_date").toLocalDate());
                period.setEndDate(rs.getDate("end_date").toLocalDate());
                period.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                period.setPeriodType(PayrollPeriod.PeriodType.valueOf(rs.getString("period_type")));
                period.setProcessed(rs.getBoolean("is_processed"));
                period.setPaid(rs.getBoolean("is_paid"));
                return period;
            }
        }
        return null;
    }

    // Get all payroll periods
    public List<PayrollPeriod> getAllPayrollPeriods() throws SQLException {
        List<PayrollPeriod> list = new ArrayList<>();
        String sql = "SELECT * FROM payroll_period";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                PayrollPeriod period = new PayrollPeriod();
                period.setId(rs.getInt("id"));
                period.setStartDate(rs.getDate("start_date").toLocalDate());
                period.setEndDate(rs.getDate("end_date").toLocalDate());
                period.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                period.setPeriodType(PayrollPeriod.PeriodType.valueOf(rs.getString("period_type")));
                period.setProcessed(rs.getBoolean("is_processed"));
                period.setPaid(rs.getBoolean("is_paid"));
                list.add(period);
            }
        }
        return list;
    }

    // Update payroll period
    public boolean updatePayrollPeriod(PayrollPeriod period) throws SQLException {
        String sql = "UPDATE payroll_period SET start_date=?, end_date=?, payment_date=?, period_type=?, is_processed=?, is_paid=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(period.getStartDate()));
            stmt.setDate(2, Date.valueOf(period.getEndDate()));
            stmt.setDate(3, Date.valueOf(period.getPaymentDate()));
            stmt.setString(4, period.getPeriodType().name());
            stmt.setBoolean(5, period.isProcessed());
            stmt.setBoolean(6, period.isPaid());
            stmt.setInt(7, period.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Delete payroll period
    public boolean deletePayrollPeriod(int id) throws SQLException {
        String sql = "DELETE FROM payroll_period WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
