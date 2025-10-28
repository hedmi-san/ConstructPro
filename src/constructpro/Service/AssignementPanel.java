package constructpro.Service;

import constructpro.DAO.ConstructionSiteDAO;
import java.sql.*;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.ConstructionSite;
import constructpro.DTO.Worker;
import javax.swing.*;
        
public class AssignementPanel extends JDialog {
    private ConstructionSite site;
    private Worker worker;
    private ConstructionSiteDAO siteDAO;
    private WorkerDAO workerDAO;
    private Connection conn;
    
    public AssignementPanel(JFrame parent, ConstructionSite site,Connection connection) throws SQLException{
        super(parent, "Assignement List", true);
        this.site = site;
        this.conn = connection;
        this.siteDAO =  new ConstructionSiteDAO(connection);
        this.workerDAO = new WorkerDAO(connection);
        initializeComponents();
        setupLayout();
        populateData();
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents(){
        
    }
    private void setupLayout(){
        
    }
    private void populateData(){    
        
    }
    
}
