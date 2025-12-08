package constructpro.DAO;

import java.sql.*;
import constructpro.DTO.Supplier;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    private Connection connection;

    public SupplierDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertSupplier(Supplier supplier) throws SQLException {
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

    public void updateSupplier(Supplier supplier) throws SQLException {
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

    public void deleteWorker(int id) throws SQLException {
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

    public Supplier getSupplierById(int id) throws SQLException {
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

    public ResultSet searchSupplierByName(String searchTerm) {
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

    public List<String> getAllSuppliersNames() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT supplier_name FROM suppliers ORDER BY supplier_name";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getString("supplier_name"));
            }
        }
        return list;
    }

    // Get supplier ID by name
    public int getSupplierIdByName(String supplierName) throws SQLException {
        if (supplierName == null || supplierName.equals("Sélectionner un fournisseure")) {
            return 0;
        }

        String sql = "SELECT supplier_id FROM suppliers WHERE supplier_name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, supplierName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("supplier_id");
            }
        }
        return 0;
    }
}
