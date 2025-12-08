package constructpro.DAO;

import constructpro.DTO.Bill;
import java.sql.*;

public class BillDAO {
    private Connection connection;

    public BillDAO(Connection connection) {
        this.connection = connection;
    }

    public int insertBill(Bill bill) throws SQLException {
        String sql = "INSERT INTO bills(bill_date,facture_number,supplier_id,assigned_site_id,transfer_fee,total_cost,paid_amount) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, Date.valueOf(bill.getBillDate()));
            stmt.setString(2, bill.getFactureNumber());
            stmt.setInt(3, bill.getSupplierID());
            stmt.setInt(4, bill.getSiteID());
            stmt.setDouble(5, bill.getTransferFee());
            stmt.setDouble(6, bill.getCost());
            stmt.setDouble(7, bill.getPaidAmount());

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
                    b.bill_id,
                    b.facture_number,
                    s.supplier_name,
                    cs.name AS site_name,
                    b.bill_date,
                    b.total_cost,
                    b.paid_amount,
                    b.transfer_fee
                FROM bills b
                JOIN suppliers s ON b.supplier_id = s.supplier_id
                JOIN ConstructionSite cs ON b.assigned_site_id = cs.id
                ORDER BY b.bill_date DESC
                """;
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }

    public ResultSet searchBillsByFactureNumber(String searchTerm) throws SQLException {
        String sql = """
                SELECT
                    b.bill_id,
                    b.facture_number,
                    s.supplier_name,
                    cs.name AS site_name,
                    b.bill_date,
                    b.total_cost,
                    b.paid_amount,
                    b.transfer_fee
                FROM bills b
                JOIN suppliers s ON b.supplier_id = s.supplier_id
                JOIN ConstructionSite cs ON b.assigned_site_id = cs.id
                WHERE b.facture_number LIKE ?
                ORDER BY b.bill_date DESC
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, "%" + searchTerm + "%");
        return ps.executeQuery();
    }
}
