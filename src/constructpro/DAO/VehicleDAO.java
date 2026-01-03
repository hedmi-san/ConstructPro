package constructpro.DAO;

import constructpro.DTO.Vehicle;
import java.sql.*;

public class VehicleDAO {
    private Connection connection;

    public VehicleDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicle(name,plateNumber,ownershipType,assignedSiteId,driverId) VALUES(?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getName());
            stmt.setString(2, vehicle.getPlateNumber());
            stmt.setString(3, vehicle.getOwnershipType());
            stmt.setInt(4, vehicle.getSiteID());
            if (vehicle.getDriverID() > 0) {
                stmt.setInt(5, vehicle.getDriverID());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.executeUpdate();
        }
    }

    public void updateVehicle(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicle SET name = ?, plateNumber = ?, assignedSiteId = ?, driverId = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getName());
            stmt.setString(2, vehicle.getPlateNumber());
            stmt.setInt(3, vehicle.getSiteID());
            if (vehicle.getDriverID() > 0) {
                stmt.setInt(4, vehicle.getDriverID());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.setInt(5, vehicle.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteVehicle(int id) throws SQLException {
        String sql = "DELETE FROM vehicle WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Vehicle getVehicleById(int id) throws SQLException {
        String sql = "SELECT * FROM vehicle WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setId(rs.getInt("id"));
                vehicle.setName(rs.getString("name"));
                vehicle.setPlateNumber(rs.getString("plateNumber"));
                vehicle.setOwnershipType(rs.getString("ownershipType"));
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
                    v.ownershipType,
                    s.name AS site_name,
                    CONCAT(w.firstName, ' ', w.lastName) AS driver_name
                FROM
                    vehicle v
                LEFT JOIN
                    constructionSite s ON v.assignedSiteId = s.id
                LEFT JOIN
                    worker w ON v.driverId = w.id;
                """;
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }

    public ResultSet searchVehicle(String searchTerm) {
        try {
            String sql = """
                    SELECT
                         v.id AS vehicle_id,
                         v.name AS vehicle_name,
                         v.plateNumber,
                         v.ownershipType,
                         s.name AS site_name,
                         CONCAT(w.firstName, ' ', w.lastName) AS driver_name
                         FROM
                             vehicle v
                         LEFT JOIN
                             constructionSite s ON v.assignedSiteId = s.id
                         LEFT JOIN
                             worker w ON v.driverId = w.id
                         WHERE
                             v.name LIKE ? OR v.plateNumber LIKE ?;
                     """;
            PreparedStatement ps = connection.prepareStatement(sql);
            String likeTerm = "%" + searchTerm + "%";
            ps.setString(1, likeTerm);
            ps.setString(2, likeTerm);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getVehicleId(Vehicle vehicle) throws SQLException {
        String sql = "SELECT id FROM vehicle WHERE plateNumber = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, vehicle.getPlateNumber());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    public ResultSet getVehiclesWithCostsBySiteId(int siteId) throws SQLException {
        String sql = """
                SELECT DISTINCT
                    v.id,
                    v.name,
                    v.plateNumber,
                    v.ownershipType,
                    (SELECT COALESCE(SUM(cost), 0) FROM maintenanceTicket WHERE vehicleId = v.id AND assignedSiteId = ?) as maintenanceCost,
                    (SELECT COALESCE(SUM((dailyRate * daysWorked) + transferFee), 0) FROM vehicleRental WHERE vehicleId = v.id AND assignedSiteId = ?) as rentCost
                FROM vehicle v
                LEFT JOIN maintenanceTicket mt ON v.id = mt.vehicleId
                LEFT JOIN vehicleRental vr ON v.id = vr.vehicleId
                WHERE v.assignedSiteId = ? OR mt.assignedSiteId = ? OR vr.assignedSiteId = ?
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, siteId);
        ps.setInt(2, siteId);
        ps.setInt(3, siteId);
        ps.setInt(4, siteId);
        ps.setInt(5, siteId);
        return ps.executeQuery();
    }
}
