package constructpro.DAO;

import java.sql.*;

public class MaterialDAO {
    private Connection connection;
    
    public MaterialDAO(Connection connection){
        this.connection = connection;
    }
}
