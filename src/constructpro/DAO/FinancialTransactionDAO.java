package constructpro.DAO;

import constructpro.DTO.FinancialTransaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FinancialTransactionDAO {
    private Connection connection;

    public FinancialTransactionDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertTransaction(FinancialTransaction ft) throws SQLException {
        String sql = "INSERT INTO financialTransaction (supplierId, paymentDate, amount, method, imagePath) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ft.getSupplierId());
            ps.setDate(2, Date.valueOf(ft.getPaymentDate()));
            ps.setDouble(3, ft.getAmount());
            ps.setString(4, ft.getMethod());
            ps.executeUpdate();
        }
    }

    public void deleteTransaction(int id) throws SQLException {
        // Note: Currently simple delete. Reverting payments from bills is complex and
        // not implemented yet.
        // It would require tracking which bills were paid by this specific transaction.
        String sql = "DELETE FROM financialTransaction WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<FinancialTransaction> getTransactionsBySupplierId(int supplierId) throws SQLException {
        List<FinancialTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM financialTransaction WHERE supplierId = ? ORDER BY paymentDate DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FinancialTransaction ft = new FinancialTransaction();
                ft.setId(rs.getInt("id"));
                ft.setSupplierId(rs.getInt("supplierId"));
                ft.setPaymentDate(rs.getDate("paymentDate").toLocalDate());
                ft.setAmount(rs.getDouble("amount"));
                ft.setMethod(rs.getString("method"));
                list.add(ft);
            }
        }
        return list;
    }
    
    public FinancialTransaction getTransactionById(int id) throws SQLException{
        String sql = "SELECT * FROM financialTransaction WHERE id = ?";
        FinancialTransaction ft = new FinancialTransaction();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ft.setId(rs.getInt("id"));
                ft.setSupplierId(rs.getInt("supplierId"));
                ft.setPaymentDate(rs.getDate("paymentDate").toLocalDate());
                ft.setAmount(rs.getDouble("amount"));
                ft.setMethod(rs.getString("method"));
                ft.setImagePath(rs.getString("imagePath"));
            }
        }
        return ft;
    }
}
