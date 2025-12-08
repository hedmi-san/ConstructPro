package constructpro.DAO;

import constructpro.DTO.BiLLItem;
import java.sql.*;

public class BiLLItemDAO {
    private Connection connection;

    public BiLLItemDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertBillItem(int billId, String itemType, String itemName, double quantity, double unitPrice)
            throws SQLException {
        String sql = "INSERT INTO bill_items (bill_id, item_type, item_name, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, billId);
            stmt.setString(2, itemType);
            stmt.setString(3, itemName);
            stmt.setDouble(4, quantity);
            stmt.setDouble(5, unitPrice);
            stmt.executeUpdate();
        }
    }

    public ResultSet getToolsInfo() throws SQLException {
        String sql = """
                SELECT
                    bi.item_id,
                    bi.item_name AS name,
                    bi.quantity,
                    bi.unit_price,
                    cs.name AS site_name,
                    b.bill_date
                FROM bill_items bi
                JOIN bills b       ON bi.bill_id = b.bill_id
                JOIN constructionsite cs ON b.assigned_site_id = cs.id
                WHERE bi.item_type = 'Outil';
                """;
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }

    public ResultSet searchToolByName(String searchTerm) throws SQLException {
        String sql = """
                    SELECT
                        bi.item_id,
                        bi.item_name AS name,
                        bi.quantity,
                        bi.unit_price,
                        cs.name AS site_name,
                        b.bill_date
                    FROM bill_items bi
                    JOIN bills b ON bi.bill_id = b.bill_id
                    JOIN constructionsite cs ON b.assigned_site_id = cs.id
                    WHERE bi.item_type = 'Outil'
                      AND bi.item_name LIKE ?
                """;

        PreparedStatement ps = connection.prepareStatement(sql);
        String likeTerm = "%" + searchTerm + "%";
        ps.setString(1, likeTerm);

        return ps.executeQuery();
    }

    public ResultSet getMaterialInfo() throws SQLException {
        String sql = """
                SELECT
                    bi.item_id,
                    bi.item_name AS name,
                    bi.quantity,
                    bi.unit_price,
                    cs.name AS site_name,
                    b.bill_date
                FROM bill_items bi
                JOIN bills b       ON bi.bill_id = b.bill_id
                JOIN constructionsite cs ON b.assigned_site_id = cs.id
                WHERE bi.item_type = 'Matériel';
                """;
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }

    public ResultSet searchMaterialByName(String searchTerm) throws SQLException {
        String sql = """
                    SELECT
                        bi.item_id,
                        bi.item_name AS name,
                        bi.quantity,
                        bi.unit_price,
                        cs.name AS site_name,
                        b.bill_date
                    FROM bill_items bi
                    JOIN bills b ON bi.bill_id = b.bill_id
                    JOIN constructionsite cs ON b.assigned_site_id = cs.id
                    WHERE bi.item_type = 'Matériel'
                      AND bi.item_name LIKE ?
                """;

        PreparedStatement ps = connection.prepareStatement(sql);
        String likeTerm = "%" + searchTerm + "%";
        ps.setString(1, likeTerm);

        return ps.executeQuery();
    }
}
