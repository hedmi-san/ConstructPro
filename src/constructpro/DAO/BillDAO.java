package constructpro.DAO;

import constructpro.DTO.Bill;
import java.sql.*;
import java.time.LocalDate;

public class BillDAO {
    private Connection connection;

    public BillDAO(Connection connection) {
        this.connection = connection;
    }

    public int insertBill(Bill bill) throws SQLException {
        String sql = "INSERT INTO bills(billDate,factureNumber,supplierId,assignedSiteId,transferFee,totalCost,paidAmount,imagePath) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, Date.valueOf(bill.getBillDate()));
            stmt.setString(2, bill.getFactureNumber());
            stmt.setInt(3, bill.getSupplierID());
            stmt.setInt(4, bill.getSiteID());
            stmt.setDouble(5, bill.getTransferFee());
            stmt.setDouble(6, bill.getCost());
            stmt.setDouble(7, bill.getPaidAmount());
            stmt.setString(8, bill.getImagePath());

            stmt.executeUpdate();

            // Get the generated bill ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1; // Return -1 if insertion failed
    }

    // Sync method: Calculate totalCost from billItems + transferFee and update
    // bills table
    private void updateAllBillTotals() throws SQLException {
        String sql = """
                    UPDATE bills b
                    LEFT JOIN (
                        SELECT billId, SUM(quantity * unitPrice) as itemsTotal
                        FROM billItems
                        GROUP BY billId
                    ) bi ON b.id = bi.billId
                    SET b.totalCost = COALESCE(bi.itemsTotal, 0) + COALESCE(b.transferFee, 0)
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    private void updateBillTotal(int billId) throws SQLException {
        String sql = """
                    UPDATE bills b
                    LEFT JOIN (
                        SELECT billId, SUM(quantity * unitPrice) as itemsTotal
                        FROM billItems
                        WHERE billId = ?
                        GROUP BY billId
                    ) bi ON b.id = bi.billId
                    SET b.totalCost = COALESCE(bi.itemsTotal, 0) + COALESCE(b.transferFee, 0)
                    WHERE b.id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ps.setInt(2, billId);
            ps.executeUpdate();
        }
    }

    public ResultSet getBillsInfo() throws SQLException {
        updateAllBillTotals(); // Sync before fetch
        String sql = """
                SELECT
                    b.id,
                    b.factureNumber,
                    s.supplierName,
                    cs.name AS site_name,
                    b.billDate,
                    b.totalCost,
                    b.paidAmount,
                    b.transferFee
                FROM bills b
                JOIN suppliers s ON b.supplierId = s.id
                JOIN constructionSite cs ON b.assignedSiteId = cs.id
                ORDER BY b.billDate DESC
                """;
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }

    public Bill getBillById(int id) throws SQLException {
        updateBillTotal(id); // Sync specific bill before fetch
        String sql = "SELECT * FROM bills WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setFactureNumber(rs.getString("factureNumber"));
                bill.setSupplierID(rs.getInt("supplierId"));
                bill.setSiteID(rs.getInt("assignedSiteId"));
                java.sql.Date bDate = rs.getDate("billDate");
                if (bDate != null)
                    bill.setBillDate(bDate.toLocalDate());
                bill.setTransferFee(rs.getDouble("transferFee"));
                bill.setCost(rs.getDouble("totalCost"));
                bill.setPaidAmount(rs.getDouble("paidAmount"));
                bill.setImagePath(rs.getString("imagePath"));
                return bill;
            }
        }
        return null;
    }

    public void updateBill(Bill bill) throws SQLException {
        String sql = "UPDATE bills SET factureNumber=?, supplierId=?, assignedSiteId=?, billDate=?, transferFee=?, totalCost=?, paidAmount=?, imagePath=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, bill.getFactureNumber());
            ps.setInt(2, bill.getSupplierID());
            ps.setInt(3, bill.getSiteID());
            ps.setDate(4, Date.valueOf(bill.getBillDate()));
            ps.setDouble(5, bill.getTransferFee());
            ps.setDouble(6, bill.getCost());
            ps.setDouble(7, bill.getPaidAmount());
            ps.setString(8, bill.getImagePath());
            ps.setInt(9, bill.getId());
            ps.executeUpdate();
        }
    }

    public ResultSet searchBillsByFactureNumber(String searchTerm) throws SQLException {
        updateAllBillTotals(); // Sync before search
        String sql = """
                SELECT
                    b.id,
                    b.factureNumber,
                    s.supplierName,
                    cs.name AS site_name,
                    b.billDate,
                    b.totalCost,
                    b.paidAmount,
                    b.transferFee
                FROM bills b
                JOIN suppliers s ON b.supplierId = s.id
                JOIN constructionSite cs ON b.assignedSiteId = cs.id
                WHERE b.factureNumber LIKE ?
                ORDER BY b.billDate DESC
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, "%" + searchTerm + "%");
        return ps.executeQuery();
    }

    public void deleteBill(int id) throws SQLException {
        String sql = "DELETE FROM bills WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public ResultSet getBillsBySupplierId(int supplierId) throws SQLException {
        updateAllBillTotals(); // Sync before fetch (could be optimized to filter by supplier)
        String sql = """
                SELECT
                    b.id,
                    b.factureNumber,
                    s.supplierName,
                    cs.name AS site_name,
                    b.billDate,
                    b.totalCost,
                    b.paidAmount,
                    b.transferFee,
                    b.imagePath
                FROM bills b
                JOIN suppliers s ON b.supplierId = s.id
                JOIN constructionSite cs ON b.assignedSiteId = cs.id
                WHERE b.supplierId = ?
                ORDER BY b.billDate DESC
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, supplierId);
        return ps.executeQuery();
    }

    public ResultSet getBillsBySiteId(int siteId) throws SQLException {
        updateAllBillTotals();
        String sql = """
                SELECT
                    b.id,
                    b.factureNumber,
                    s.supplierName,
                    b.billDate,
                    b.totalCost
                FROM bills b
                JOIN suppliers s ON b.supplierId = s.id
                WHERE b.assignedSiteId = ?
                ORDER BY b.billDate DESC
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, siteId);
        return ps.executeQuery();
    }

    public ResultSet getBillsReport(int siteId, LocalDate start, LocalDate end) throws SQLException {
        String sql = """
                SELECT
                    b.billDate,
                    s.supplierName,
                    b.totalCost
                FROM bills b
                INNER JOIN suppliers s ON b.supplierId = s.id
                WHERE b.assignedSiteId = ? AND b.billDate BETWEEN ? AND ?
                ORDER BY b.billDate ASC
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, siteId);
        ps.setDate(2, Date.valueOf(start));
        ps.setDate(3, Date.valueOf(end));
        return ps.executeQuery();
    }

    public ResultSet getBillsBySiteAndSupplierType(int siteId, String supplierType) throws SQLException {
        updateAllBillTotals();
        String sql = """
                SELECT
                    b.id,
                    b.factureNumber,
                    s.supplierName,
                    b.billDate,
                    b.totalCost
                FROM bills b
                JOIN suppliers s ON b.supplierId = s.id
                WHERE b.assignedSiteId = ? AND s.supplierType = ?
                ORDER BY b.billDate DESC
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, siteId);
        ps.setString(2, supplierType);
        return ps.executeQuery();

    }

    public ResultSet getBillDetailsBySupplierTypeReport(int siteId, String supplierType, LocalDate start, LocalDate end)
            throws SQLException {
        String sql = """
                SELECT
                    b.factureNumber,
                    b.billDate,
                    b.totalCost,
                    bi.itemName,
                    bi.quantity,
                    bi.unitPrice,
                    (bi.quantity * bi.unitPrice) as itemTotal
                FROM bills b
                JOIN suppliers s ON b.supplierId = s.id
                LEFT JOIN billItems bi ON b.id = bi.billId
                WHERE b.assignedSiteId = ?
                  AND s.supplierType = ?
                  AND b.billDate BETWEEN ? AND ?
                ORDER BY b.billDate, b.factureNumber
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, siteId);
        ps.setString(2, supplierType);
        ps.setDate(3, Date.valueOf(start));
        ps.setDate(4, Date.valueOf(end));
        return ps.executeQuery();
    }
}
