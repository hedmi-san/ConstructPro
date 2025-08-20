package constructpro.DAO;

import constructpro.Database.ConnectionEstablish;
import java.sql.*;

public class WorkerBalanceDAO {
    
    Connection connection;
    Statement st;
    
    public WorkerBalanceDAO(){
        try {
            connection = new ConnectionEstablish().getConn();
            st = connection.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
