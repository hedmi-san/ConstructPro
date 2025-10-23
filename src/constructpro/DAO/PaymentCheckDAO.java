package constructpro.DAO;

import java.sql.*;
import java.time.LocalDate;

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
    
    public void getAllWorkerPaymentChecks(int salary_record_id) throws SQLException{
        String sql ="SELECT payment_date,base_salary,paid_amount FROM payment_check WHERE salary_record_id = ? ";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1,salary_record_id);
            stmt.executeQuery();
        }
    }
    
    public void deletePaymentCheck(int id)throws SQLException {
        String sql = "DELETE FROM payment_check WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
