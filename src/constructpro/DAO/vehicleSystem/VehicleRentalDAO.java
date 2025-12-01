package constructpro.DAO.vehicleSystem;

import constructpro.DTO.vehicleSystem.VehicleRental;
import java.sql.*;

public class VehicleRentalDAO {
    private Connection connection;

    public VehicleRentalDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertNewRentedVehicle(VehicleRental rentedVehicle) throws SQLException {
        String sql = "INSERT INTO vehicle_Rental (vehicle_id, owner_company, owner_phone, daily_rate, start_date, end_date, days_worked, deposite_amount, transfer_fee) VALUES(?,?,?,?,?,?,?,?,?) ";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, rentedVehicle.getVehicle_id());
            stmt.setString(2, rentedVehicle.getOwnerName());
            stmt.setString(3, rentedVehicle.getOwnerPhone());
            stmt.setDouble(4, rentedVehicle.getDailyRate());
            stmt.setDate(5, Date.valueOf(rentedVehicle.getStartDate()));
            stmt.setDate(6, rentedVehicle.getEndDate() != null ? Date.valueOf(rentedVehicle.getEndDate()) : null);
            stmt.setInt(7, rentedVehicle.getDaysWorked());
            stmt.setDouble(8, rentedVehicle.getDepositAmount());
            stmt.setDouble(9, rentedVehicle.getTransferFee());
            stmt.executeUpdate();
        }
    }

    /**
     * Get all rental records for a specific vehicle
     */
    public java.util.List<VehicleRental> getAllRentalRecords(int vehicleId) throws SQLException {
        java.util.List<VehicleRental> records = new java.util.ArrayList<>();
        String sql = "SELECT * FROM vehicle_Rental WHERE vehicle_id = ? ORDER BY start_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                VehicleRental rental = new VehicleRental();
                rental.setId(rs.getInt("id"));
                rental.setVehicle_id(rs.getInt("vehicle_id"));
                rental.setOwnerName(rs.getString("owner_company"));
                rental.setOwnerPhone(rs.getString("owner_phone"));
                rental.setDailyRate(rs.getDouble("daily_rate"));
                rental.setStartDate(rs.getDate("start_date").toLocalDate());

                Date endDate = rs.getDate("end_date");
                if (endDate != null) {
                    rental.setEndDate(endDate.toLocalDate());
                }

                rental.setDaysWorked(rs.getInt("days_worked"));
                rental.setDepositAmount(rs.getDouble("deposite_amount"));
                rental.setTransferFee(rs.getDouble("transfer_fee"));
                records.add(rental);
            }
        }
        return records;
    }

    /**
     * Update an existing rental record
     */
    public void updateRentalRecord(VehicleRental rental) throws SQLException {
        String sql = "UPDATE vehicle_Rental SET start_date = ?, end_date = ?, days_worked = ?, " +
                "transfer_fee = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(rental.getStartDate()));
            stmt.setDate(2, rental.getEndDate() != null ? Date.valueOf(rental.getEndDate()) : null);
            stmt.setInt(3, rental.getDaysWorked());
            stmt.setDouble(4, rental.getTransferFee());
            stmt.setInt(5, rental.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Calculate total rental cost for a vehicle
     * Cost = (daily_rate * days_worked) + transfer_fee
     */
    public double getTotalRentalCost(int vehicleId) throws SQLException {
        String sql = "SELECT SUM((daily_rate * days_worked) + transfer_fee) as total FROM vehicle_Rental WHERE vehicle_id = ?";

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
        String sql = "SELECT * FROM vehicle_Rental WHERE vehicle_id = ? ORDER BY start_date DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                VehicleRental rental = new VehicleRental();
                rental.setId(rs.getInt("id"));
                rental.setVehicle_id(rs.getInt("vehicle_id"));
                rental.setOwnerName(rs.getString("owner_company"));
                rental.setOwnerPhone(rs.getString("owner_phone"));
                rental.setDailyRate(rs.getDouble("daily_rate"));
                rental.setStartDate(rs.getDate("start_date").toLocalDate());

                Date endDate = rs.getDate("end_date");
                if (endDate != null) {
                    rental.setEndDate(endDate.toLocalDate());
                }

                rental.setDaysWorked(rs.getInt("days_worked"));
                rental.setDepositAmount(rs.getDouble("deposite_amount"));
                rental.setTransferFee(rs.getDouble("transfer_fee"));
                return rental;
            }
        }
        return null;
    }
}
