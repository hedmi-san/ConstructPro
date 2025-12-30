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
        String sql = "INSERT INTO suppliers (supplierName, phone, address,totalSpent,totalPaid) VALUES (?,?,?,?,?)";
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
        String sql = "UPDATE suppliers SET supplierName = ?, phone = ?, address = ?,totalSpent = ?,totalPaid = ? WHERE  id = ?";
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
        String sql = "DELETE FROM suppliers WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public ResultSet getSuppliersInfo() throws SQLException {
        String sql = """
                    SELECT
                        s.id,
                        s.supplierName,
                        s.phone,
                        s.address,
                        COALESCE(SUM(b.totalCost), 0) as totalSpent,
                        COALESCE(SUM(b.paidAmount), 0) as totalPaid
                    FROM suppliers s
                    LEFT JOIN bills b ON s.id = b.supplierId
                    GROUP BY s.id, s.supplierName, s.phone, s.address
                """;
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }

    public Supplier getSupplierById(int id) throws SQLException {
        String sql = """
                    SELECT
                        s.id,
                        s.supplierName,
                        s.phone,
                        s.address,
                        COALESCE(SUM(b.totalCost), 0) as totalSpent,
                        COALESCE(SUM(b.paidAmount), 0) as totalPaid
                    FROM suppliers s
                    LEFT JOIN bills b ON s.id = b.supplierId
                    WHERE s.id = ?
                    GROUP BY s.id, s.supplierName, s.phone, s.address
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setId(rs.getInt("id"));
                supplier.setName(rs.getString("supplierName"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setAddress(rs.getString("address"));
                supplier.setTotalSpent(rs.getDouble("totalSpent"));
                supplier.setTotalPaid(rs.getDouble("totalPaid"));
                return supplier;
            }
        }
        return null;
    }

    public ResultSet searchSupplierByName(String searchTerm) {
        try {
            String query = """
                        SELECT
                            s.id,
                            s.supplierName,
                            s.phone,
                            s.address,
                            COALESCE(SUM(b.totalCost), 0) as totalSpent,
                            COALESCE(SUM(b.paidAmount), 0) as totalPaid
                        FROM suppliers s
                        LEFT JOIN bills b ON s.id = b.supplierId
                        WHERE s.supplierName LIKE ?
                        GROUP BY s.id, s.supplierName, s.phone, s.address
                    """;
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
        String sql = "SELECT supplierName FROM suppliers ORDER BY supplierName";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getString("supplierName"));
            }
        }
        return list;
    }

    // Get supplier ID by name
    public int getSupplierIdByName(String supplierName) throws SQLException {
        if (supplierName == null || supplierName.equals("Sélectionner un fournisseure")) {
            return 0;
        }

        String sql = "SELECT id FROM suppliers WHERE supplierName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, supplierName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return 0;
    }
}
