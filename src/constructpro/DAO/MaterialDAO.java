package constructpro.DAO;

import java.sql.*;
import java.time.LocalDate;

public class MaterialDAO {
    private Connection connection;
    
    public MaterialDAO(Connection connection){
        this.connection = connection;
    }
    
    public void insertMaterial(String materailName, double quantity, double unitPrice, int siteId, LocalDate purchaseDate) throws SQLException{
        String sql ="INSERT INTO materials (material_name,quantity, unit_price, assigned_site_id, date_acquired)) VALUES(?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, materailName);
            stmt.setDouble(2, quantity);
            stmt.setDouble(3, unitPrice);
            stmt.setInt(4, siteId);
            stmt.setDate(5, Date.valueOf(purchaseDate));
            stmt.executeUpdate();
        }
    }
    
    public ResultSet getToolsInfo() throws SQLException {
        String sql = "SELECT m.material_id, m.material_name, m.quantity, m.unit_price, c.name, m.date_acquired " +
                     "FROM materials m " +
                     "INNER JOIN constructionSite c ON m.assigned_site_id = c.id";
        
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }
    
    public ResultSet searchMaterialByName(String searchTerm) throws SQLException{
        String sql = "SELECT m.material_id, m.material_name, m.quantity, m.unit_price, c.name, m.date_acquired " +
                     "FROM materials m " +
                     "INNER JOIN constructionSite c ON m.assigned_site_id = c.id "+
                     "WHERE m.material_name LIKE ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        String likeTerm = "%" + searchTerm + "%";
        ps.setString(1, likeTerm);
        return ps.executeQuery();
    }
}
