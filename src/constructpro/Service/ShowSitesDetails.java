package constructpro.Service;

import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DTO.ConstructionSite;
import constructpro.DTO.Worker;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;

public class ShowSitesDetails extends JDialog {
    
    private Connection conn;
    private ConstructionSiteDAO siteDao;
    private ConstructionSite site;
    private Worker worker;
    private JLabel siteNameLabel;
    private JPanel infoPanel,workersPanel,costPanel,billPanel;
    public ShowSitesDetails(JFrame parent, ConstructionSite site,Connection connection) throws SQLException {
        super(parent, "Détails du chantier", true);
        this.site = site;
        this.siteDao = new ConstructionSiteDAO(connection);
        this.conn = connection;
        this.worker = new Worker();
        initializeComponents();
        setupLayout();
        setupStyling();
        populateData();//site information
        populateWorkersData();
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        
    }

    private void setupStyling() {
        
    }

    private void setupLayout() {
        
    }

    private void populateData() {
        
    }
    
    
    
}
