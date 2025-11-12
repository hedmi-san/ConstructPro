package constructpro.DAO;

import constructpro.DTO.Vehicle;
import java.sql.*;

public class VehicleDAO {
    private Connection connection;

    public VehicleDAO(Connection connection) {
        this.connection = connection;
    }
    
    public void insertVehicle(Vehicle vehicle) throws SQLException{
        String sql ="INSERT INTO vehicle(name,plateNumber,status,assignedSiteId,driverId) VALUES(?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getName());
            stmt.setString(2, vehicle.getPlateNumber());
            stmt.setString(3, vehicle.getStatus());
            stmt.setInt(4, vehicle.getSiteID());
            stmt.setInt(5, vehicle.getDriverID());
            stmt.executeUpdate();
        }
    }
    
    public void updateVehicle(Vehicle vehicle) throws SQLException{
        String sql = "UPDATE vehicle SET name = ?, plateNumber = ?,status = ?, assignedSiteId = ?, driverId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getName());
            stmt.setString(2, vehicle.getPlateNumber());
            stmt.setString(3, vehicle.getStatus());
            stmt.setInt(4, vehicle.getSiteID());
            stmt.setInt(5, vehicle.getDriverID());
            stmt.executeUpdate();
        }
    }
    
    public void deleteVehicle(int id) throws SQLException{
        String sql = "DELETE FROM vehicle WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    public Vehicle  getVehicleById(int id) throws SQLException{
        String sql = "SELECT * FROM vehicle WHERE supplier_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setId(rs.getInt("id"));
                vehicle.setName(rs.getString("name"));
                vehicle.setPlateNumber(rs.getString("plateNumber"));
                vehicle.setStatus(rs.getString("status"));
                vehicle.setSiteID(rs.getInt("assignedSiteId"));
                vehicle.setDriverID(rs.getInt("driverId"));
                return vehicle;
            }
        }
        return null;
    
    }
    
    public ResultSet getVehiclesInfo() throws SQLException {
        String sql = """
                     SELECT 
                         v.id AS vehicle_id,
                         v.name AS vehicle_name,
                         v.plateNumber,
                         v.status,
                         s.name AS site_name,
                         CONCAT(w.first_name, ' ', w.last_name) AS driver_name
                     FROM 
                         vehicle v
                     LEFT JOIN 
                         ConstructionSite s ON v.assignedSiteId = s.id
                     LEFT JOIN 
                         worker w ON v.driverId = w.id;
                     """;
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }
    
    public ResultSet searchVehicle(String searchTerm){
        try {
            String sql="""
                   SELECT 
                        v.id AS vehicle_id,
                        v.name AS vehicle_name,
                        v.plateNumber,
                            v.status,
                            s.name AS site_name,
                            CONCAT(w.first_name, ' ', w.last_name) AS driver_name
                        FROM 
                            vehicle v
                        LEFT JOIN 
                            ConstructionSite s ON v.assignedSiteId = s.id
                        LEFT JOIN 
                            worker w ON v.driverId = w.id
                        WHERE 
                            vehicle_name LIKE ? OR plateNumber LIKE ?;
                    """;
            PreparedStatement ps = connection.prepareStatement(sql);
            String likeTerm = "%" + searchTerm + "%";
            ps.setString(1, likeTerm);
        return ps.executeQuery();
            }catch (SQLException e) {e.printStackTrace();}
        return null;
    }
}
