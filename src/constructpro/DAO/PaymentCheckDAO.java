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

    public void insertPaymentCheck(int salary_record_id,LocalDate payment_date,double base_salary,double paid_amount ) throws SQLException {
        String sql = "INSERT INTO payment_check (salary_record_id, payment_date, base_salary, paid_amount) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, salary_record_id);
            stmt.setDate(2, Date.valueOf(payment_date));
            stmt.setDouble(3, base_salary);
            stmt.setDouble(4, paid_amount);
            stmt.executeUpdate();
        }
    }
    
    public List<PaymentCheck> getAllWorkerPaymentChecks(int salaryRecordId) throws SQLException {
        List<PaymentCheck> checks = new ArrayList<>();
        String sql = "SELECT id, payment_date, base_salary, paid_amount FROM payment_check WHERE salary_record_id = ? ORDER BY payment_date";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, salaryRecordId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PaymentCheck check = new PaymentCheck();
                    check.setId(rs.getInt("id"));
                    check.setPaymentDay(rs.getDate("payment_date").toLocalDate());
                    check.setBaseSalary(rs.getDouble("base_salary"));
                    check.setPaidAmount(rs.getDouble("paid_amount"));
                    checks.add(check);
                }
            }
        }
        return checks;
    }
    
    public void deletePaymentCheck(int id)throws SQLException {
        String sql = "DELETE FROM payment_check WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
