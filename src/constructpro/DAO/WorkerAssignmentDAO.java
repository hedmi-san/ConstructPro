package constructpro.DAO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import constructpro.DTO.WorkerAssignment;

import java.sql.*;

public class WorkerAssignmentDAO {
    private Connection connection;

    public WorkerAssignmentDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertAssignment(int workerId, int siteId, LocalDate assignmentDate) throws SQLException {
        String sql = "INSERT INTO  worker_assignment(worker_id,site_id,assignment_date) VALUES(?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, workerId);
            stmt.setInt(2, siteId);
            stmt.setDate(3, Date.valueOf(assignmentDate));
            stmt.executeUpdate();
        }
    }

    public void updateWorkerAssignment(int workerId, int siteId, LocalDate unAssignmentDate) throws SQLException {
        String sql = "UPDATE worker_assignment SET unassignment_date = ? WHERE worker_id = ? AND site_id = ?  AND assignment_date is not null";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(unAssignmentDate));
            stmt.setInt(2, workerId);
            stmt.setInt(3, siteId);
            stmt.executeUpdate();
        }
    }

    public List<WorkerAssignment> getAllWorkerAssignments(int workerId) throws SQLException {
        List<WorkerAssignment> assignments = new ArrayList<>();
        String sql = "SELECT id, worker_id, site_id, assignment_date, unassignment_date " +
                "FROM worker_assignment WHERE worker_id = ? ORDER BY assignment_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, workerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                WorkerAssignment assignment = new WorkerAssignment();
                assignment.setId(rs.getInt("id"));
                assignment.setWorkerId(rs.getInt("worker_id"));
                assignment.setSiteId(rs.getInt("site_id"));

                Date assignmentDate = rs.getDate("assignment_date");
                if (assignmentDate != null) {
                    assignment.setAssignmentDate(assignmentDate.toLocalDate());
                }

                Date unassignmentDate = rs.getDate("unassignment_date");
                if (unassignmentDate != null) {
                    assignment.setUnAssignmentDate(unassignmentDate.toLocalDate());
                }

                assignments.add(assignment);
            }
        }

        return assignments;
    }
}
