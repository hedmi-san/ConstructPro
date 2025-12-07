package constructpro.DAO;

import java.sql.*;
import constructpro.DTO.Supplier;

public class SupplierDAO {
    private Connection connection;
    
    public SupplierDAO(Connection connection){
        this.connection = connection;
    }
    
    public void insertSupplier(Supplier supplier) throws SQLException{
        String sql = "INSERT INTO suppliers (supplier_name, phone, address,total_spent,total_paid) VALUES (?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getPhone());
            stmt.setString(3, supplier.getAddress());
            stmt.setDouble(4, supplier.getTotalSpent());
            stmt.setDouble(5, supplier.getTotalPaid());
            stmt.executeUpdate();
        }
    }
    
    public void updateSupplier(Supplier supplier) throws SQLException{
        String sql = "UPDATE suppliers SET supplier_name = ?, phone = ?, address = ?,total_spent = ?,total_paid = ? WHERE  supplier_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getPhone());
            stmt.setString(3, supplier.getAddress());
            stmt.setDouble(4, supplier.getTotalSpent());
            stmt.setDouble(5, supplier.getTotalPaid());
            stmt.setInt(6, supplier.getId());
            stmt.executeUpdate();
        }
    }
    
    public void deleteWorker(int id) throws SQLException{
        String sql = "DELETE FROM suppliers WHERE supplier_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    public ResultSet getSuppliersInfo() throws SQLException {
        String sql = "SELECT * FROM suppliers";
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }
    
    public Supplier getSupplierById(int id) throws SQLException{
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setId(rs.getInt("supplier_id"));
                supplier.setName(rs.getString("supplier_name"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setAddress(rs.getString("address"));
                supplier.setTotalSpent(rs.getDouble("total_spent"));
                supplier.setTotalPaid(rs.getDouble("total_paid"));
                return supplier;
            }
        }
        return null;
    }
    
    public ResultSet searchSupplierByName(String searchTerm){
        try {
        String query = "SELECT * FROM suppliers WHERE supplier_name LIKE ?";
        PreparedStatement ps = connection.prepareStatement(query);
        String likeTerm = "%" + searchTerm + "%";
        ps.setString(1, likeTerm);
        return ps.executeQuery();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
    }
}
