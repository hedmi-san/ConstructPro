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

    public ResultSet getItemsInfo() throws SQLException {
        String sql = """
                SELECT
                    bi.itemId,
                    bi.itemName AS name,
                    bi.quantity,
                    bi.itemType,
                    bi.unitPrice,
                    cs.name AS site_name,
                    b.billDate
                FROM billItems bi
                JOIN bills b       ON bi.billId = b.id
                JOIN constructionSite cs ON b.assignedSiteId = cs.id;
                """;
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }

    public ResultSet searchItemByName(String searchTerm) throws SQLException {
        String sql = """
                    SELECT
                        bi.itemId,
                        bi.itemName AS name,
                        bi.quantity,
                        bi.unitPrice,
                        bi.itemType,
                        cs.name AS site_name,
                        b.billDate
                    FROM billItems bi
                    JOIN bills b ON bi.billId = b.id
                    JOIN constructionSite cs ON b.assignedSiteId = cs.id
                    WHERE bi.itemName LIKE ?
                """;

        PreparedStatement ps = connection.prepareStatement(sql);
        String likeTerm = "%" + searchTerm + "%";
        ps.setString(1, likeTerm);

        return ps.executeQuery();
    }

    public ResultSet getBillItemsBySiteId(int siteId) throws SQLException {
        String sql = """
                SELECT
                    bi.itemId,
                    bi.itemName AS name,
                    bi.quantity,
                    bi.itemType,
                    bi.unitPrice,
                    cs.name AS site_name,
                    b.billDate
                FROM billItems bi
                JOIN bills b       ON bi.billId = b.id
                JOIN constructionSite cs ON b.assignedSiteId = cs.id
                WHERE b.assignedSiteId = ?
                ORDER BY b.billDate DESC;
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, siteId);
        return ps.executeQuery();
    }

    public ResultSet searchBillItemsBySiteId(int siteId, String searchTerm) throws SQLException {
        String sql = """
                    SELECT
                        bi.itemId,
                        bi.itemName AS name,
                        bi.quantity,
                        bi.itemType,
                        bi.unitPrice,
                        cs.name AS site_name,
                        b.billDate
                    FROM billItems bi
                    JOIN bills b ON bi.billId = b.id
                    JOIN constructionSite cs ON b.assignedSiteId = cs.id
                    WHERE b.assignedSiteId = ? AND bi.itemName LIKE ?
                    ORDER BY b.billDate DESC
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, siteId);
        ps.setString(2, "%" + searchTerm + "%");
        return ps.executeQuery();
    }

    public void deleteBillItem(int itemId) throws SQLException {
        String sql = "DELETE FROM billItems WHERE itemId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.executeUpdate();
        }
    }
}
