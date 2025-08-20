package constructpro.DAO;

import constructpro.DTO.WorkerSalaryConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkerSalaryConfigDAO {
    private Connection conn;

    public WorkerSalaryConfigDAO(Connection conn) {
        this.conn = conn;
    }

    public void insert(WorkerSalaryConfig config) throws SQLException {
        String sql = "INSERT INTO worker_salary_config (worker_id, daily_rate, payment_percentage, effective_date, is_active) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, config.getWorkerId());
            ps.setDouble(2, config.getDailyRate());
            ps.setDouble(3, config.getPaymentPercentage());
            ps.setDate(4, Date.valueOf(config.getEffectiveDate()));
            ps.setBoolean(5, config.isActive());
            ps.executeUpdate();
        }
    }

    public void update(WorkerSalaryConfig config) throws SQLException {
        String sql = "UPDATE worker_salary_config SET worker_id=?, daily_rate=?, payment_percentage=?, effective_date=?, is_active=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, config.getWorkerId());
            ps.setDouble(2, config.getDailyRate());
            ps.setDouble(3, config.getPaymentPercentage());
            ps.setDate(4, Date.valueOf(config.getEffectiveDate()));
            ps.setBoolean(5, config.isActive());
            ps.setInt(6, config.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM worker_salary_config WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public WorkerSalaryConfig getById(int id) throws SQLException {
        String sql = "SELECT * FROM worker_salary_config WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                WorkerSalaryConfig c = new WorkerSalaryConfig();
                c.setId(rs.getInt("id"));
                c.setWorkerId(rs.getInt("worker_id"));
                c.setDailyRate(rs.getDouble("daily_rate"));
                c.setPaymentPercentage(rs.getDouble("payment_percentage"));
                c.setEffectiveDate(rs.getDate("effective_date").toLocalDate());
                c.setActive(rs.getBoolean("is_active"));
                return c;
            }
        }
        return null;
    }

    public List<WorkerSalaryConfig> getByWorkerId(int workerId) throws SQLException {
        List<WorkerSalaryConfig> list = new ArrayList<>();
        String sql = "SELECT * FROM worker_salary_config WHERE worker_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                WorkerSalaryConfig c = new WorkerSalaryConfig();
                c.setId(rs.getInt("id"));
                c.setWorkerId(rs.getInt("worker_id"));
                c.setDailyRate(rs.getDouble("daily_rate"));
                c.setPaymentPercentage(rs.getDouble("payment_percentage"));
                c.setEffectiveDate(rs.getDate("effective_date").toLocalDate());
                c.setActive(rs.getBoolean("is_active"));
                list.add(c);
            }
        }
        return list;
    }
}
