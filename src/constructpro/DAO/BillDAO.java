package constructpro.DAO;

import constructpro.DTO.Bill;
import java.sql.*;

public class BillDAO {
    private Connection connection;

    public BillDAO(Connection connection) {
        this.connection = connection;
    }
    
    public void insertBill(Bill bill) throws SQLException{
        String sql = "INSERT INTO bills(bill_date,facture_number,supplier_id,assigned_site_id,transfer_fee,total_cost,paid_amount) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(bill.getBillDate()));
            stmt.setString(2, bill.getFactureNumber());
            stmt.setInt(3,bill.getSupplierID() );
            stmt.setInt(4, bill.getSiteID());
            stmt.setDouble(5,bill.getTransferFee());
            stmt.setDouble(6, bill.getPaidAmount());
            
            stmt.executeUpdate();
        }
    }
}
