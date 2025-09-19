package constructpro.DAO;

import java.sql.*;
import constructpro.DTO.WorkerBalance;
import java.util.ArrayList;
import java.util.List;

public class WorkerBalanceDAO {
    
    final Connection connection;
    Statement st;
    ResultSet rs;
    public WorkerBalanceDAO(Connection conn) throws SQLException {
        this.connection = conn;
    }
    
    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS WorkerBalance (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "worker_id INT NOT NULL," +
                "total_earned DECIMAL(10,2) DEFAULT 0," +
                "total_paid DECIMAL(10,2) DEFAULT 0," +
                "total_retained DECIMAL(10,2) DEFAULT 0," +
                "FOREIGN KEY (worker_id) REFERENCES Worker(id)" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    // Insert new worker balance (when worker is added to project)
    public void insert(WorkerBalance balance) throws SQLException {
        String sql = "INSERT INTO WorkerBalance (worker_id, total_earned, total_paid, total_retained) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, balance.getWorkerId());
            pstmt.setDouble(2, balance.getTotalEarned());
            pstmt.setDouble(3, balance.getTotalPaid());
            pstmt.setDouble(4, balance.getTotalRetained());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    balance.setId(rs.getInt(1));
                }
            }
        }
    }

    // Find by worker ID
    public WorkerBalance findByWorkerId(int workerId) throws SQLException {
        String sql = "SELECT * FROM WorkerBalance WHERE worker_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, workerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    // Get all balances
    public List<WorkerBalance> findAll() throws SQLException {
        List<WorkerBalance> balances = new ArrayList<>();
        String sql = "SELECT * FROM WorkerBalance";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                balances.add(mapRow(rs));
            }
        }
        return balances;
    }

    // Update worker balance (after salary calculation)
    public void update(WorkerBalance balance) throws SQLException {
        String sql = "UPDATE WorkerBalance SET total_earned=?, total_paid=?, total_retained=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, balance.getTotalEarned());
            pstmt.setDouble(2, balance.getTotalPaid());
            pstmt.setDouble(3, balance.getTotalRetained());
            pstmt.setInt(4, balance.getId());
            pstmt.executeUpdate();
        }
    }

    // Delete balance record (rare, but maybe if worker removed from system)
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM WorkerBalance WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // Helper method
    private WorkerBalance mapRow(ResultSet rs) throws SQLException {
        WorkerBalance balance = new WorkerBalance();
        balance.setId(rs.getInt("id"));
        balance.setWorkerId(rs.getInt("worker_id"));
        balance.setTotalEarned(rs.getDouble("total_earned"));
        balance.setTotalPaid(rs.getDouble("total_paid"));
        balance.setTotalRetained(rs.getDouble("total_retained"));
        return balance;
    }
    
    public WorkerBalance getBalanceByWorker(int workerId) {
        WorkerBalance balance = null;
        String sql = "SELECT * FROM worker_balance WHERE worker_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, workerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                balance = new WorkerBalance();
                balance.setWorkerId(rs.getInt("worker_id"));
                balance.setTotalEarned(rs.getDouble("total_earned"));
                balance.setTotalPaid(rs.getDouble("total_paid"));
                balance.setTotalRetained(rs.getDouble("total_retained"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }

    public void updateWorkerBalance(WorkerBalance balance) {
        String sql = "INSERT INTO worker_balance (worker_id, total_earned, total_paid, total_retained) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "total_earned = VALUES(total_earned), " +
                     "total_paid = VALUES(total_paid), " +
                     "total_retained = VALUES(total_retained)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, balance.getWorkerId());
            ps.setDouble(2, balance.getTotalEarned());
            ps.setDouble(3, balance.getTotalPaid());
            ps.setDouble(4, balance.getTotalRetained());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
