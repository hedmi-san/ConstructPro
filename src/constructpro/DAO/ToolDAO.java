package constructpro.DAO;

import java.sql.*;
import java.time.LocalDate;

public class ToolDAO {
    private Connection connection;
    
    public ToolDAO(Connection connection){
        this.connection = connection;
    }
    
    public void insertTool(String name, double quantity, double unitPrice, int siteId, LocalDate purchaseDate) throws SQLException{
        String sql = "INSERT INTO tools(tool_name, quantity, unit_price, assigned_site_id, date_acquired) VALUES (?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setDouble(2, quantity);
            stmt.setDouble(3, unitPrice);
            stmt.setInt(4, siteId);
            stmt.setDate(5, Date.valueOf(purchaseDate));
            stmt.executeUpdate();
        }
    }
    
    public ResultSet getToolsInfo() throws SQLException {
        String sql = "SELECT t.tool_id, t.tool_name, t.quantity, t.unit_price, c.name, t.date_acquired " +
                     "FROM tools t " +
                     "INNER JOIN constructionSite c ON t.assigned_site_id = c.id";
        
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }
    
    public ResultSet searchToolByName(String searchTerm) throws SQLException{
        String sql = "SELECT t.tool_id, t.tool_name, t.quantity, t.unit_price, c.name, t.date_acquired " +
                     "FROM tools t " +
                     "INNER JOIN constructionSite c ON t.assigned_site_id = c.id "+
                     "WHERE t.tool_name LIKE ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        String likeTerm = "%" + searchTerm + "%";
        ps.setString(1, likeTerm);
        return ps.executeQuery();
    }
}