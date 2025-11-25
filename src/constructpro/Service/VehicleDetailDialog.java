package constructpro.Service;

import constructpro.DTO.Vehicle;
import constructpro.DTO.vehicleSystem.VehicleAssignment;
import constructpro.DAO.vehicleSystem.VehicleAssignmentDAO;
import constructpro.DTO.vehicleSystem.Maintainance;
import constructpro.DAO.vehicleSystem.MaintainanceDAO;
import constructpro.DTO.vehicleSystem.VehicleRental;
import constructpro.DAO.vehicleSystem.VehicleRentalDAO;
import java.sql.Connection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class VehicleDetailDialog extends JDialog{
    
    private Vehicle curentVehicle;
    private VehicleAssignment vehicleAssignment;
    private Maintainance maintainance;
    private VehicleRental vehicleRental;
    private VehicleAssignmentDAO vehicleAssignmentDAO;
    private MaintainanceDAO maintainanceDAO;
    private VehicleRentalDAO vehicleRentalDAO;
    
    // Color scheme for dark theme
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color DARKER_BACKGROUND = new Color(35, 35, 35);
    private static final Color ACCENT_COLOR = new Color(0, 120, 215);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color LABEL_COLOR = new Color(180, 180, 180);
    
    //Maintainance Panel
    private JPanel maintainancePanel,rentPanle;
    private JTable maintainacetable;
    private DefaultTableModel tableModel;
    private JPanel totalsPanel;
    private Connection conn;
    
    public VehicleDetailDialog(JFrame parent, Vehicle vehicle,Connection connection) {
        super(parent, "Vehicle Details", true);
        this.curentVehicle = vehicle;
        this.conn = connection;
        this.maintainanceDAO = new MaintainanceDAO(connection);
        
    }

}
