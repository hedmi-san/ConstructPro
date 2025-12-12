package constructpro.DAO;

import constructpro.Database.SQLiteDateUtils;
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
        String sql = "INSERT INTO workerAssignment(workerId,siteId,assignmentDate) VALUES(?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, workerId);
            stmt.setInt(2, siteId);
            stmt.setDate(3, Date.valueOf(assignmentDate));
            stmt.executeUpdate();
        }
    }

    public void updateWorkerAssignment(int workerId, int siteId, LocalDate unAssignmentDate) throws SQLException {
        String sql = "UPDATE workerAssignment SET unassignmentDate = ? WHERE workerId = ? AND siteId = ?  AND assignmentDate is not null";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(unAssignmentDate));
            stmt.setInt(2, workerId);
            stmt.setInt(3, siteId);
            stmt.executeUpdate();
        }
    }

    public List<WorkerAssignment> getAllWorkerAssignments(int workerId) throws SQLException {
        List<WorkerAssignment> assignments = new ArrayList<>();
        String sql = "SELECT id, workerId, siteId, assignmentDate, unassignmentDate " +
                "FROM workerAssignment WHERE workerId = ? ORDER BY assignmentDate DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, workerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                WorkerAssignment assignment = new WorkerAssignment();
                assignment.setId(rs.getInt("id"));
                assignment.setWorkerId(rs.getInt("workerId"));
                assignment.setSiteId(rs.getInt("siteId"));

                java.time.LocalDate assignmentDate = SQLiteDateUtils.getDate(rs, "assignmentDate");
                if (assignmentDate != null) {
                    assignment.setAssignmentDate(assignmentDate);
                }

                java.time.LocalDate unassignmentDate = SQLiteDateUtils.getDate(rs, "unassignmentDate");
                if (unassignmentDate != null) {
                    assignment.setUnAssignmentDate(unassignmentDate);
                }

                assignments.add(assignment);
            }
        }

        return assignments;
    }
}
