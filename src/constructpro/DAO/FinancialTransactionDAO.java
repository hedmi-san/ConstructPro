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
        String insertSql = "INSERT INTO financialTransaction (supplierId, paymentDate, amount, method, imagePath) VALUES (?, ?, ?, ?, ?)";
        String selectBillsSql = "SELECT id, totalCost, paidAmount FROM bills WHERE supplierId = ? AND paidAmount < totalCost ORDER BY billDate ASC";
        String updateBillSql = "UPDATE bills SET paidAmount = ? WHERE id = ?";

        try {
            connection.setAutoCommit(false); // Start transaction

            // 1. Insert the Financial Transaction
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                ps.setInt(1, ft.getSupplierId());
                ps.setDate(2, Date.valueOf(ft.getPaymentDate()));
                ps.setDouble(3, ft.getAmount());
                ps.setString(4, ft.getMethod());
                ps.setString(5, ft.getImagePath());
                ps.executeUpdate();
            }

            // 2. Distribute amount to unpaid bills
            double remainingAmount = ft.getAmount();

            try (PreparedStatement psSelect = connection.prepareStatement(selectBillsSql)) {
                psSelect.setInt(1, ft.getSupplierId());
                ResultSet rs = psSelect.executeQuery();

                while (rs.next() && remainingAmount > 0) {
                    int billId = rs.getInt("id");
                    double totalCost = rs.getDouble("totalCost");
                    double paidAmount = rs.getDouble("paidAmount");

                    double debtOnBill = totalCost - paidAmount;
                    double paymentForThisBill = 0;

                    if (remainingAmount >= debtOnBill) {
                        paymentForThisBill = debtOnBill;
                        remainingAmount -= debtOnBill;
                    } else {
                        paymentForThisBill = remainingAmount;
                        remainingAmount = 0;
                    }

                    double newPaidAmount = paidAmount + paymentForThisBill;

                    try (PreparedStatement psUpdate = connection.prepareStatement(updateBillSql)) {
                        psUpdate.setDouble(1, newPaidAmount);
                        psUpdate.setInt(2, billId);
                        psUpdate.executeUpdate();
                    }
                }
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw e; // Re-throw exception to be handled by caller
        } finally {
            try {
                connection.setAutoCommit(true); // Restore auto-commit
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
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
                ft.setImagePath(rs.getString("imagePath"));
                list.add(ft);
            }
        }
        return list;
    }
}
