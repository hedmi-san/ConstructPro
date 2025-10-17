package constructpro.Service;
import javax.swing.*;
import java.sql.*;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DTO.ConstructionSite;
import constructpro.DAO.WorkerDAO;

public class WorkerList extends JDialog{
    private Connection conn;
    private final ConstructionSiteDAO siteDAO;
    public WorkerList(JFrame parent, ConstructionSite site,Connection connection) throws SQLException {
        super(parent, "Workers List", true);
        this.siteDAO = new ConstructionSiteDAO(connection);
        this.conn = connection;
        initializeComponents();
        setupLayout();
        setupStyling();
        populateData();
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        
    }

    private void setupLayout() {
        
    }

    private void setupStyling() {
        
    }

    private void populateData() {
        
    }
}
