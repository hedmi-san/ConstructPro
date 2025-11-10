package constructpro.DAO;

import java.sql.*;

public class VehicleDAO {
    private Connection connection;

    public VehicleDAO(Connection connection) {
        this.connection = connection;
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
