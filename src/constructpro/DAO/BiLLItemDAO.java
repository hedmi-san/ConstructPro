package constructpro.DAO;

import constructpro.DTO.BiLLItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BiLLItemDAO {
    private Connection connection;

    public BiLLItemDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertBillItem(int billId, String itemType, String itemName, double quantity, double unitPrice)
            throws SQLException {
        String sql = "INSERT INTO billItems (billId, itemType, itemName, quantity, unitPrice) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, billId);
            stmt.setString(2, itemType);
            stmt.setString(3, itemName);
            stmt.setDouble(4, quantity);
            stmt.setDouble(5, unitPrice);
            stmt.executeUpdate();
        }
    }

    public void deleteBillItems(int billId) throws SQLException {
        String sql = "DELETE FROM billItems WHERE billId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, billId);
            stmt.executeUpdate();
        }
    }

    public List<BiLLItem> getBillItems(int billId) throws SQLException {
        List<BiLLItem> items = new ArrayList<>();
        String sql = "SELECT * FROM billItems WHERE billId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, billId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                BiLLItem item = new BiLLItem();
                item.setId(rs.getInt("itemId"));
                item.setBillID(rs.getInt("billId"));
                item.setBillType(rs.getString("itemType"));
                item.setItemName(rs.getString("itemName"));
                item.setQuantity(rs.getDouble("quantity"));
                item.setUnitPrice(rs.getDouble("unitPrice"));
                item.setTotalPrice(item.getQuantity() * item.getUnitPrice());
                items.add(item);
            }
        }
        return items;
    }

    public ResultSet getToolsInfo() throws SQLException {
        String sql = """
                SELECT
                    bi.itemId,
                    bi.itemName AS name,
                    bi.quantity,
                    bi.unitPrice,
                    cs.name AS site_name,
                    b.billDate
                FROM billItems bi
                JOIN bills b       ON bi.billId = b.id
                JOIN constructionSite cs ON b.assignedSiteId = cs.id
                WHERE bi.itemType = 'Outil';
                """;
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }

    public ResultSet searchToolByName(String searchTerm) throws SQLException {
        String sql = """
                    SELECT
                        bi.itemId,
                        bi.itemName AS name,
                        bi.quantity,
                        bi.unitPrice,
                        cs.name AS site_name,
                        b.billDate
                    FROM billItems bi
                    JOIN bills b ON bi.billId = b.id
                    JOIN constructionSite cs ON b.assignedSiteId = cs.id
                    WHERE bi.itemType = 'Outil'
                      AND bi.itemName LIKE ?
                """;

        PreparedStatement ps = connection.prepareStatement(sql);
        String likeTerm = "%" + searchTerm + "%";
        ps.setString(1, likeTerm);

        return ps.executeQuery();
    }

    public ResultSet getMaterialInfo() throws SQLException {
        String sql = """
                SELECT
                    bi.itemId,
                    bi.itemName AS name,
                    bi.quantity,
                    bi.unitPrice,
                    cs.name AS site_name,
                    b.billDate
                FROM billItems bi
                JOIN bills b       ON bi.billId = b.id
                JOIN constructionSite cs ON b.assignedSiteId = cs.id
                WHERE bi.itemType = 'Matériel';
                """;
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }

    public ResultSet searchMaterialByName(String searchTerm) throws SQLException {
        String sql = """
                    SELECT
                        bi.itemId,
                        bi.itemName AS name,
                        bi.quantity,
                        bi.unitPrice,
                        cs.name AS site_name,
                        b.billDate
                    FROM billItems bi
                    JOIN bills b ON bi.billId = b.id
                    JOIN constructionSite cs ON b.assignedSiteId = cs.id
                    WHERE bi.itemType = 'Matériel'
                      AND bi.itemName LIKE ?
                """;

        PreparedStatement ps = connection.prepareStatement(sql);
        String likeTerm = "%" + searchTerm + "%";
        ps.setString(1, likeTerm);

        return ps.executeQuery();
    }
}
