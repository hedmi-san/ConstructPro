package constructpro.DAO;

import constructpro.DTO.WorkerSalaryConfig;
import constructpro.Database.ConnectionEstablish;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkerSalaryConfigDAO {
    
    Connection connection;
    Statement st;
    
    public WorkerSalaryConfigDAO(){
        try {
                connection = new ConnectionEstablish().getConn();
                st = connection.createStatement();
            } catch (SQLException ex) {
                ex.printStackTrace();
        }
    }
    
    // CREATE - Add new salary configuration
    public boolean addSalaryConfig(WorkerSalaryConfig config) throws SQLException {
        // First, deactivate any existing active config for this worker
        deactivateCurrentConfig(config.getWorkerId());
        
        String sql = """
            INSERT INTO worker_salary_config (worker_id, daily_rate, payment_percentage, effective_date, is_active)
            VALUES (?, ?, ?, ?, TRUE)
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, config.getWorkerId());
            pstmt.setDouble(2, config.getDailyRate());
            pstmt.setDouble(3, config.getPaymentPercentage());
            pstmt.setDate(4, Date.valueOf(config.getEffectiveDate()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        config.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    // READ - Get current active configuration for worker
    public WorkerSalaryConfig getCurrentConfigForWorker(int workerId) throws SQLException {
        String sql = """
            SELECT id, worker_id, daily_rate, payment_percentage, effective_date, is_active
            FROM worker_salary_config
            WHERE worker_id = ? AND is_active = TRUE
            ORDER BY effective_date DESC
            LIMIT 1
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, workerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConfig(rs);
                }
            }
        }
        return null;
    }
    
    // READ - Get configuration effective at specific date
    public WorkerSalaryConfig getConfigForWorkerAtDate(int workerId, LocalDate date) throws SQLException {
        String sql = """
            SELECT id, worker_id, daily_rate, payment_percentage, effective_date, is_active
            FROM worker_salary_config
            WHERE worker_id = ? AND effective_date <= ?
            ORDER BY effective_date DESC
            LIMIT 1
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, workerId);
            pstmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConfig(rs);
                }
            }
        }
        return null;
    }
    
    // READ - Get all configurations for a worker (history)
    public List<WorkerSalaryConfig> getWorkerConfigHistory(int workerId) throws SQLException {
        String sql = """
            SELECT id, worker_id, daily_rate, payment_percentage, effective_date, is_active
            FROM worker_salary_config
            WHERE worker_id = ?
            ORDER BY effective_date DESC
            """;
        
        List<WorkerSalaryConfig> configs = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, workerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    configs.add(mapResultSetToConfig(rs));
                }
            }
        }
        
        return configs;
    }
    
    // READ - Get all active configurations
    public List<WorkerSalaryConfig> getAllActiveConfigs() throws SQLException {
        String sql = """
            SELECT wsc.id, wsc.worker_id, wsc.daily_rate, wsc.payment_percentage, 
                   wsc.effective_date, wsc.is_active
            FROM worker_salary_config wsc
            INNER JOIN worker w ON wsc.worker_id = w.id
            WHERE wsc.is_active = TRUE AND w.is_active = TRUE
            ORDER BY w.first_name, w.last_name
            """;
        
        List<WorkerSalaryConfig> configs = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                configs.add(mapResultSetToConfig(rs));
            }
        }
        
        return configs;
    }
    
    // READ - Get configuration by ID
    public WorkerSalaryConfig getConfigById(int configId) throws SQLException {
        String sql = """
            SELECT id, worker_id, daily_rate, payment_percentage, effective_date, is_active
            FROM worker_salary_config
            WHERE id = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, configId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConfig(rs);
                }
            }
        }
        return null;
    }
    
    // READ - Get workers without salary configuration
    public List<Integer> getWorkersWithoutConfig() throws SQLException {
        String sql = """
            SELECT w.id
            FROM worker w
            LEFT JOIN worker_salary_config wsc ON w.id = wsc.worker_id AND wsc.is_active = TRUE
            WHERE wsc.worker_id IS NULL AND w.is_active = TRUE
            """;
        
        List<Integer> workerIds = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                workerIds.add(rs.getInt("id"));
            }
        }
        
        return workerIds;
    }
    
    // UPDATE - Update salary configuration
    public boolean updateSalaryConfig(WorkerSalaryConfig config) throws SQLException {
        String sql = """
            UPDATE worker_salary_config 
            SET daily_rate = ?, payment_percentage = ?, effective_date = ?
            WHERE id = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setDouble(1, config.getDailyRate());
            pstmt.setDouble(2, config.getPaymentPercentage());
            pstmt.setDate(3, Date.valueOf(config.getEffectiveDate()));
            pstmt.setInt(4, config.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // UPDATE - Deactivate current configuration for worker
    public boolean deactivateCurrentConfig(int workerId) throws SQLException {
        String sql = """
            UPDATE worker_salary_config 
            SET is_active = FALSE 
            WHERE worker_id = ? AND is_active = TRUE
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, workerId);
            return pstmt.executeUpdate() >= 0; // >= 0 because there might be no active config
        }
    }
    
    // UPDATE - Change worker's daily rate (creates new config)
    public boolean changeWorkerDailyRate(int workerId, double newDailyRate, LocalDate effectiveDate) throws SQLException {
        WorkerSalaryConfig currentConfig = getCurrentConfigForWorker(workerId);
        if (currentConfig == null) {
            // Create new config with default payment percentage
            WorkerSalaryConfig newConfig = new WorkerSalaryConfig(workerId, newDailyRate, 70.0);
            newConfig.setEffectiveDate(effectiveDate);
            return addSalaryConfig(newConfig);
        } else {
            // Create new config with same payment percentage
            WorkerSalaryConfig newConfig = new WorkerSalaryConfig(workerId, newDailyRate, currentConfig.getPaymentPercentage());
            newConfig.setEffectiveDate(effectiveDate);
            return addSalaryConfig(newConfig);
        }
    }
    
    // UPDATE - Change payment percentage for worker
    public boolean changePaymentPercentage(int workerId, double newPercentage, LocalDate effectiveDate) throws SQLException {
        WorkerSalaryConfig currentConfig = getCurrentConfigForWorker(workerId);
        if (currentConfig == null) {
            // Create new config with default daily rate
            WorkerSalaryConfig newConfig = new WorkerSalaryConfig(workerId, 2000.0, newPercentage);
            newConfig.setEffectiveDate(effectiveDate);
            return addSalaryConfig(newConfig);
        } else {
            // Create new config with same daily rate
            WorkerSalaryConfig newConfig = new WorkerSalaryConfig(workerId, currentConfig.getDailyRate(), newPercentage);
            newConfig.setEffectiveDate(effectiveDate);
            return addSalaryConfig(newConfig);
        }
    }
    
    // DELETE - Delete configuration (soft delete by deactivation is preferred)
    public boolean deleteConfig(int configId) throws SQLException {
        String sql = "DELETE FROM worker_salary_config WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, configId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // UTILITY - Check if worker has active configuration
    public boolean workerHasActiveConfig(int workerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM worker_salary_config WHERE worker_id = ? AND is_active = TRUE";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, workerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    // UTILITY - Get daily rate for worker at specific date
    public double getDailyRateForWorkerAtDate(int workerId, LocalDate date) throws SQLException {
        WorkerSalaryConfig config = getConfigForWorkerAtDate(workerId, date);
        return config != null ? config.getDailyRate() : 0.0;
    }
    
    // UTILITY - Get payment percentage for worker at specific date
    public double getPaymentPercentageForWorkerAtDate(int workerId, LocalDate date) throws SQLException {
        WorkerSalaryConfig config = getConfigForWorkerAtDate(workerId, date);
        return config != null ? config.getPaymentPercentage() : 70.0; // Default 70%
    }
    
    // UTILITY - Create default configuration for new worker
    public boolean createDefaultConfig(int workerId, double dailyRate) throws SQLException {
        WorkerSalaryConfig defaultConfig = new WorkerSalaryConfig(workerId, dailyRate, 70.0);
        defaultConfig.setEffectiveDate(LocalDate.now());
        return addSalaryConfig(defaultConfig);
    }
    
    // HELPER - Map ResultSet to WorkerSalaryConfig object
    private WorkerSalaryConfig mapResultSetToConfig(ResultSet rs) throws SQLException {
        WorkerSalaryConfig config = new WorkerSalaryConfig();
        
        config.setId(rs.getInt("id"));
        config.setWorkerId(rs.getInt("worker_id"));
        config.setDailyRate(rs.getDouble("daily_rate"));
        config.setPaymentPercentage(rs.getDouble("payment_percentage"));
        config.setEffectiveDate(rs.getDate("effective_date").toLocalDate());
        config.setActive(rs.getBoolean("is_active"));
        
        return config;
    }
}