package constructpro.DAO;

import constructpro.DTO.Salary;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaryDAO {
    private Connection conn;

    public SalaryDAO(Connection conn) {
        this.conn = conn;
    }

    // Insert new salary record
    public boolean addSalary(Salary salary) throws SQLException {
        String sql = "INSERT INTO salary (worker_id, payroll_period_id, payment_date, days_worked, daily_rate, total_earned, amount_paid, retained_amount, payment_percentage, notes, is_paid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, salary.getWorkerId());
            stmt.setInt(2, salary.getPayrollPeriodId());
            stmt.setDate(3, Date.valueOf(salary.getPaymentDate()));
            stmt.setInt(4, salary.getDaysWorked());
            stmt.setDouble(5, salary.getDailyRate());
            stmt.setDouble(6, salary.getTotalEarned());
            stmt.setDouble(7, salary.getAmountPaid());
            stmt.setDouble(8, salary.getRetainedAmount());
            stmt.setDouble(9, salary.getPaymentPercentage());
            stmt.setString(10, salary.getNotes());
            stmt.setBoolean(11, salary.isPaid());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    salary.setId(rs.getInt(1));
                }
            }
            return rows > 0;
        }
    }

    // Get salary by ID
    public Salary getSalaryById(int id) throws SQLException {
        String sql = "SELECT * FROM salary WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSalary(rs);
            }
        }
        return null;
    }

    // Get all salaries
    public List<Salary> getAllSalaries() throws SQLException {
        List<Salary> list = new ArrayList<>();
        String sql = "SELECT * FROM salary";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapResultSetToSalary(rs));
            }
        }
        return list;
    }

    // Update salary record
    public boolean updateSalary(Salary salary) throws SQLException {
        String sql = "UPDATE salary SET worker_id=?, payroll_period_id=?, payment_date=?, days_worked=?, daily_rate=?, total_earned=?, amount_paid=?, retained_amount=?, payment_percentage=?, notes=?, is_paid=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salary.getWorkerId());
            stmt.setInt(2, salary.getPayrollPeriodId());
            stmt.setDate(3, Date.valueOf(salary.getPaymentDate()));
            stmt.setInt(4, salary.getDaysWorked());
            stmt.setDouble(5, salary.getDailyRate());
            stmt.setDouble(6, salary.getTotalEarned());
            stmt.setDouble(7, salary.getAmountPaid());
            stmt.setDouble(8, salary.getRetainedAmount());
            stmt.setDouble(9, salary.getPaymentPercentage());
            stmt.setString(10, salary.getNotes());
            stmt.setBoolean(11, salary.isPaid());
            stmt.setInt(12, salary.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete salary
    public boolean deleteSalary(int id) throws SQLException {
        String sql = "DELETE FROM salary WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Map helper
    private Salary mapResultSetToSalary(ResultSet rs) throws SQLException {
        Salary salary = new Salary();
        salary.setId(rs.getInt("id"));
        salary.setWorkerId(rs.getInt("worker_id"));
        salary.setPayrollPeriodId(rs.getInt("payroll_period_id"));
        salary.setPaymentDate(rs.getDate("payment_date").toLocalDate());
        salary.setDaysWorked(rs.getInt("days_worked"));
        salary.setDailyRate(rs.getDouble("daily_rate"));
        salary.setTotalEarned(rs.getDouble("total_earned"));
        salary.setAmountPaid(rs.getDouble("amount_paid"));
        salary.setRetainedAmount(rs.getDouble("retained_amount"));
        salary.setPaymentPercentage(rs.getDouble("payment_percentage"));
        salary.setNotes(rs.getString("notes"));
        salary.setPaid(rs.getBoolean("is_paid"));
        return salary;
    }
}
