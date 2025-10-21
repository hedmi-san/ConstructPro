package constructpro.DAO;

import java.sql.*;
import java.time.LocalDate;

public class PaymentCheckDAO {

    private Connection connection;

    public PaymentCheckDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertPaymentCheck(int salary_record_id,LocalDate payment_date,double base_salary,double paid_amount ) throws SQLException {
        String sql = "INSERT INTO payment_check (salary_record_id, payment_date, payment_amount, paid_amount) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, salary_record_id);
            stmt.setDate(2, Date.valueOf(payment_date));
            stmt.setDouble(3, base_salary);
            stmt.setDouble(4, paid_amount);
            stmt.executeUpdate();
        }
    }
}
