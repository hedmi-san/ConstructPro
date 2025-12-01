package constructpro.DAO.vehicleSystem;

import constructpro.DTO.vehicleSystem.Maintainance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaintainanceDAO {
    private Connection connection;

    public MaintainanceDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Get all maintenance records for a specific vehicle
     */
    public List<Maintainance> getAllMaintainanceRecords(int vehicleId) throws SQLException {
        List<Maintainance> records = new ArrayList<>();
        String sql = "SELECT * FROM maitainance_Ticket WHERE vehicle_id = ? ORDER BY repaire_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Maintainance maintainance = new Maintainance();
                maintainance.setId(rs.getInt("id"));
                maintainance.setVehicle_id(rs.getInt("vehicle_id"));
                maintainance.setMaintainanceType(rs.getString("maitainance_type"));
                maintainance.setAssignedSiteId(rs.getInt("assignedSiteId"));
                maintainance.setRepair_date(rs.getDate("repaire_date").toLocalDate());
                maintainance.setRepairCost(rs.getDouble("cost"));
                records.add(maintainance);
            }
        }
        return records;
    }

    /**
     * Add a new maintenance record
     */
    public void addMaintainance(Maintainance maintainance) throws SQLException {
        String sql = "INSERT INTO maitainance_Ticket (maitainance_type, vehicle_id, assignedSiteId, repaire_date, cost) "
                +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, maintainance.getMaintainanceType());
            stmt.setInt(2, maintainance.getVehicle_id());
            stmt.setInt(3, maintainance.getAssignedSiteId());
            stmt.setDate(4, Date.valueOf(maintainance.getRepair_date()));
            stmt.setDouble(5, maintainance.getRepairCost());
            stmt.executeUpdate();
        }
    }

    /**
     * Update an existing maintenance record
     */
    public void updateMaintainance(Maintainance maintainance) throws SQLException {
        String sql = "UPDATE maitainance_Ticket SET maitainance_type = ?, assignedSiteId = ?, " +
                "repaire_date = ?, cost = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, maintainance.getMaintainanceType());
            stmt.setInt(2, maintainance.getAssignedSiteId());
            stmt.setDate(3, Date.valueOf(maintainance.getRepair_date()));
            stmt.setDouble(4, maintainance.getRepairCost());
            stmt.setInt(5, maintainance.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Delete a maintenance record
     */
    public void deleteMaintainance(int id) throws SQLException {
        String sql = "DELETE FROM maitainance_Ticket WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Calculate total maintenance cost for a vehicle
     */
    public double getTotalMaintainanceCost(int vehicleId) throws SQLException {
        String sql = "SELECT SUM(cost) as total FROM maitainance_Ticket WHERE vehicle_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }
}
