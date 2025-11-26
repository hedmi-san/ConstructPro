package constructpro.DAO.vehicleSystem;

import constructpro.DTO.vehicleSystem.VehicleRental;
import java.sql.*;

public class VehicleRentalDAO {
    private Connection connection;

    public VehicleRentalDAO(Connection connection) {
        this.connection = connection;
    }
    
    public void insertNewRentedVehicle(VehicleRental rentedVehicle) throws SQLException{
        String sql = "INSERT INTO vehicle_Rental (vehicle_id, owner_company, owner_phone, daily_rate, start_date, end_date, deposite_amount,transfer_fee) VALUES(?,?,?,?,?,?,?,?) ";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, rentedVehicle.getVehicle_id());
            stmt.setString(2, rentedVehicle.getOwnerName());
            stmt.setString(3, rentedVehicle.getOwnerPhone());
            stmt.setDouble(4, rentedVehicle.getDailyRate());
            stmt.setDate(5,Date.valueOf(rentedVehicle.getStartDate()));
            stmt.setDate(6,Date.valueOf(rentedVehicle.getEndDate()) != null ?  Date.valueOf(rentedVehicle.getEndDate()): null);
            stmt.setDouble(7,rentedVehicle.getDepositAmount());
            stmt.setDouble(8,rentedVehicle.getTransferFee());
            stmt.executeUpdate();
        }
    }
    
}
