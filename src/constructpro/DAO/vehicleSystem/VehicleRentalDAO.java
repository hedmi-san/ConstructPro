package constructpro.DAO.vehicleSystem;

import java.sql.*;

public class VehicleRentalDAO {
    private Connection connection;

    public VehicleRentalDAO(Connection connection) {
        this.connection = connection;
    }
    
}
