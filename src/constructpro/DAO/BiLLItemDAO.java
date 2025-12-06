package constructpro.DAO;

import constructpro.DTO.BiLLItem;
import java.sql.*;

public class BiLLItemDAO {
    private Connection connection;

    public BiLLItemDAO(Connection connection) {
        this.connection = connection;
    }
    
}
