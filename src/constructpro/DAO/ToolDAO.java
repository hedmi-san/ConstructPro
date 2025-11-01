package constructpro.DAO;

import java.sql.*;
import java.time.LocalDate;



public class ToolDAO {
    private Connection connection;
    private Statement st;
    private ResultSet rs;
    public ToolDAO(Connection connection){
        this.connection= connection;
    }
    
    public void insertTool(String name,double quantity, double unitPrice, int siteId,LocalDate purshaceDate) throws SQLException{
        String sql = "INSERT into tools(tool_name, quantity, unit_price, assigned_site_id, date_acquired) VALUES (?,?,?,?,?) ";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setDouble(2, quantity);
            stmt.setDouble(3, unitPrice);
            stmt.setInt(4, siteId);
            stmt.setDate(5, Date.valueOf(purshaceDate));
            stmt.executeUpdate();
        }
    }
    
    public ResultSet getToolsInfo(){
        try {
            String sql = "SELECT t.tool_name, t.quantity, t.unit_price,c.name AS site_name, t.date_acquired FROM tools t INNER JOIN constructionSite c ON t.assigned_site_id = c.id";
            rs = st.executeQuery(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }
}
