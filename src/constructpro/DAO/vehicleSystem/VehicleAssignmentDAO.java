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
        String sql = "SELECT * FROM vehical_Assignment WHERE vehicle_id = ? AND unassignment_date IS NULL";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                VehicleAssignment assignment = new VehicleAssignment();
                assignment.setId(rs.getInt("id"));
                assignment.setVehicle_id(rs.getInt("vehicle_id"));
                assignment.setAssignedSiteId(rs.getInt("assignedSiteId"));
                assignment.setAssignmentDate(rs.getDate("assignment_date").toLocalDate());

                Date unassignDate = rs.getDate("unassignment_date");
                if (unassignDate != null) {
                    assignment.setUnAssignmentDate(unassignDate.toLocalDate());
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
        String sql = "SELECT * FROM vehical_Assignment WHERE vehicle_id = ? ORDER BY assignment_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                VehicleAssignment assignment = new VehicleAssignment();
                assignment.setId(rs.getInt("id"));
                assignment.setVehicle_id(rs.getInt("vehicle_id"));
                assignment.setAssignedSiteId(rs.getInt("assignedSiteId"));
                assignment.setAssignmentDate(rs.getDate("assignment_date").toLocalDate());

                Date unassignDate = rs.getDate("unassignment_date");
                if (unassignDate != null) {
                    assignment.setUnAssignmentDate(unassignDate.toLocalDate());
                }

                assignments.add(assignment);
            }
        }
        return assignments;
    }
}
