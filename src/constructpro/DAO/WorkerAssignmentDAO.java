package constructpro.DAO;

import java.time.LocalDate;

import java.sql.*;

public class WorkerAssignmentDAO {
    private Connection connection;
    
    public WorkerAssignmentDAO(Connection connection){
        this.connection = connection;
    }
    
    public void insertAssignment(int workerId,int siteId,LocalDate assignmentDate) throws SQLException{
        String sql = "INSERT INTO  worker_assignment(worker_id,site_id,assignment_date) VALUES(?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, workerId);
            stmt.setInt(2, siteId);
            stmt.setDate(3, Date.valueOf(assignmentDate));
            stmt.executeUpdate();
        }
    }
    public void updateWorkerAssignment(int workerId,int siteId,LocalDate unAssignmentDate) throws SQLException{
        String sql = "UPDATE worker_assignment SET unassignment_date = ? WHERE worker_id = ? AND site_id = ?  AND assignment_date is not null";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1,  Date.valueOf(unAssignmentDate));
            stmt.setInt(2, workerId);
            stmt.setInt(3, siteId);
            stmt.executeUpdate();
        }
    }
}
