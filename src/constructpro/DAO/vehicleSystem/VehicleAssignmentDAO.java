package constructpro.DAO.vehicleSystem;

import constructpro.DTO.vehicleSystem.VehicleAssignment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleAssignmentDAO {
    private Connection connection;

    public VehicleAssignmentDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Get current assignment for a vehicle (where unassignment_date is NULL)
     */
    public VehicleAssignment getCurrentAssignment(int vehicleId) throws SQLException {
        String sql = "SELECT * FROM vehicleAssignment WHERE vehicleId = ? AND unassignmentDate IS NULL";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                VehicleAssignment assignment = new VehicleAssignment();
                assignment.setId(rs.getInt("id"));
                assignment.setVehicle_id(rs.getInt("vehicleId"));
                assignment.setAssignedSiteId(rs.getInt("assignedSiteId"));
                java.sql.Date aDate = rs.getDate("assignmentDate");
                if (aDate != null)
                    assignment.setAssignmentDate(aDate.toLocalDate());

                java.sql.Date uDate = rs.getDate("unassignmentDate");
                if (uDate != null) {
                    assignment.setUnAssignmentDate(uDate.toLocalDate());
                }

                return assignment;
            }
        }
        return null;
    }

    /**
     * Get all vehicle assignments for history tracking
     */
    public List<VehicleAssignment> getAllVehicleAssignments(int vehicleId) throws SQLException {
        List<VehicleAssignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM vehicleAssignment WHERE vehicleId = ? ORDER BY assignmentDate DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                VehicleAssignment assignment = new VehicleAssignment();
                assignment.setId(rs.getInt("id"));
                assignment.setVehicle_id(rs.getInt("vehicleId"));
                assignment.setAssignedSiteId(rs.getInt("assignedSiteId"));
                java.sql.Date aDate = rs.getDate("assignmentDate");
                if (aDate != null)
                    assignment.setAssignmentDate(aDate.toLocalDate());

                java.sql.Date uDate = rs.getDate("unassignmentDate");
                if (uDate != null) {
                    assignment.setUnAssignmentDate(uDate.toLocalDate());
                }

                assignments.add(assignment);
            }
        }
        return assignments;
    }
}
