package constructpro.DAO;

import constructpro.DTO.Bill;
import java.sql.*;

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

    public ResultSet getBillsInfo() throws SQLException {
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
    
    public void deleteBill(int id) throws SQLException{
        String sql = "DELETE FROM bills WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
}
