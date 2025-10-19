package constructpro.DAO;

import java.sql.*;

public class PaymentCheckDAO {
    private Connection conn;

    public PaymentCheckDAO(Connection connection) {
        this.conn = connection;
    }
    
}
