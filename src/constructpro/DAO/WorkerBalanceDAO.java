package constructpro.DAO;

import java.sql.*;

public class WorkerBalanceDAO {
    
    final Connection connection;
    public WorkerBalanceDAO(Connection conn) throws SQLException {
        this.connection = conn;
    }
}
