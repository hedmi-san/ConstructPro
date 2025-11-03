package constructpro.DAO;

import java.sql.*;

public class SupplierDAO {
    private Connection connection;
    
    public SupplierDAO(Connection connection){
        this.connection = connection;
    }
    
    public void insertSupplier(String name, String phoneNumber, String address, double TotalSpent, double totalPaid) throws SQLException{
        String sql = "INSERT INTO suppliers (supplier_name, phone, address,total_spent,total_paid) VALUES (?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, phoneNumber);
            stmt.setString(3, address);
            stmt.setDouble(4, TotalSpent);
            stmt.setDouble(5, totalPaid);
            stmt.executeUpdate();
        }
    }
    
    public ResultSet getSuppliersInfo() throws SQLException {
        String sql = "SELECT * FROM suppliers";
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }
}
