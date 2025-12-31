package constructpro.DAO.vehicleSystem;

import constructpro.DTO.vehicleSystem.VehicleRental;

import java.util.List;
import java.sql.*;

public class VehicleRentalDAO {
    private Connection connection;

    public VehicleRentalDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertNewRentedVehicle(VehicleRental rentedVehicle) throws SQLException {
        String sql = "INSERT INTO vehicleRental (vehicleId, assignedSiteId, ownerCompany, ownerPhone, dailyRate, startDate, endDate, daysWorked, depositeAmount, transferFee) VALUES(?,?,?,?,?,?,?,?,?,?) ";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, rentedVehicle.getVehicle_id());
            stmt.setInt(2, rentedVehicle.getAssignedSiteId());
            stmt.setString(3, rentedVehicle.getOwnerName());
            stmt.setString(4, rentedVehicle.getOwnerPhone());
            stmt.setDouble(5, rentedVehicle.getDailyRate());
            stmt.setDate(6, Date.valueOf(rentedVehicle.getStartDate()));
            stmt.setDate(7, rentedVehicle.getEndDate() != null ? Date.valueOf(rentedVehicle.getEndDate()) : null);
            stmt.setInt(8, rentedVehicle.getDaysWorked());
            stmt.setDouble(9, rentedVehicle.getDepositAmount());
            stmt.setDouble(10, rentedVehicle.getTransferFee());
            stmt.executeUpdate();
        }
    }

    /**
     * Get all rental records for a specific vehicle
     */
    public List<VehicleRental> getAllRentalRecords(int vehicleId) throws SQLException {
        java.util.List<VehicleRental> records = new java.util.ArrayList<>();
        String sql = "SELECT * FROM vehicleRental WHERE vehicleId = ? ORDER BY startDate DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                VehicleRental rental = new VehicleRental();
                rental.setId(rs.getInt("id"));
                rental.setVehicle_id(rs.getInt("vehicleId"));
                rental.setAssignedSiteId(rs.getInt("assignedSiteId"));
                rental.setOwnerName(rs.getString("ownerCompany"));
                rental.setOwnerPhone(rs.getString("ownerPhone"));
                rental.setDailyRate(rs.getDouble("dailyRate"));
                java.sql.Date sDate = rs.getDate("startDate");
                if (sDate != null)
                    rental.setStartDate(sDate.toLocalDate());

                java.sql.Date eDate = rs.getDate("endDate");
                if (eDate != null) {
                    rental.setEndDate(eDate.toLocalDate());
                }

                rental.setDaysWorked(rs.getInt("daysWorked"));
                rental.setDepositAmount(rs.getDouble("depositeAmount"));
                rental.setTransferFee(rs.getDouble("transferFee"));
                records.add(rental);
            }
        }
        return records;
    }

    /**
     * Update an existing rental record
     */
    public void updateRentalRecord(VehicleRental rental) throws SQLException {
        String sql = "UPDATE vehicleRental SET startDate = ?, endDate = ?, daysWorked = ?, " +
                "transferFee = ?, assignedSiteId = ?, dailyRate = ?, depositeAmount = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(rental.getStartDate()));
            stmt.setDate(2, rental.getEndDate() != null ? Date.valueOf(rental.getEndDate()) : null);
            stmt.setInt(3, rental.getDaysWorked());
            stmt.setDouble(4, rental.getTransferFee());
            stmt.setInt(5, rental.getAssignedSiteId());
            stmt.setDouble(6, rental.getDailyRate());
            stmt.setDouble(7, rental.getDepositAmount());
            stmt.setInt(8, rental.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Add a new rental record for an existing vehicle
     */
    public void addRentalRecord(VehicleRental rental) throws SQLException {
        String sql = "INSERT INTO vehicleRental (vehicleId, assignedSiteId, ownerCompany, ownerPhone, " +
                "dailyRate, startDate, endDate, daysWorked, depositeAmount, transferFee) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, rental.getVehicle_id());
            stmt.setInt(2, rental.getAssignedSiteId());
            stmt.setString(3, rental.getOwnerName());
            stmt.setString(4, rental.getOwnerPhone());
            stmt.setDouble(5, rental.getDailyRate());
            stmt.setDate(6, Date.valueOf(rental.getStartDate()));
            stmt.setDate(7, rental.getEndDate() != null ? Date.valueOf(rental.getEndDate()) : null);
            stmt.setInt(8, rental.getDaysWorked());
            stmt.setDouble(9, rental.getDepositAmount());
            stmt.setDouble(10, rental.getTransferFee());
            stmt.executeUpdate();
        }
    }

    /**
     * Calculate total rental cost for a vehicle
     * Cost = (daily_rate * days_worked) + transfer_fee
     */
    public double getTotalRentalCost(int vehicleId) throws SQLException {
        String sql = "SELECT SUM((dailyRate * daysWorked) + transferFee) as total FROM vehicleRental WHERE vehicleId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    /**
     * Get current rental information for a vehicle
     * Returns the most recent rental record (by start_date)
     */
    public VehicleRental getCurrentRentalInfo(int vehicleId) throws SQLException {
        String sql = "SELECT * FROM vehicleRental WHERE vehicleId = ? ORDER BY startDate DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                VehicleRental rental = new VehicleRental();
                rental.setId(rs.getInt("id"));
                rental.setVehicle_id(rs.getInt("vehicleId"));
                rental.setAssignedSiteId(rs.getInt("assignedSiteId"));
                rental.setOwnerName(rs.getString("ownerCompany"));
                rental.setOwnerPhone(rs.getString("ownerPhone"));
                rental.setDailyRate(rs.getDouble("dailyRate"));
                java.sql.Date sDate = rs.getDate("startDate");
                if (sDate != null)
                    rental.setStartDate(sDate.toLocalDate());

                java.sql.Date eDate = rs.getDate("endDate");
                if (eDate != null) {
                    rental.setEndDate(eDate.toLocalDate());
                }

                rental.setDaysWorked(rs.getInt("daysWorked"));
                rental.setDepositAmount(rs.getDouble("depositeAmount"));
                rental.setTransferFee(rs.getDouble("transferFee"));
                return rental;
            }
        }
        return null;
    }
}
