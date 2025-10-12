package constructpro.Service;

import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DTO.ConstructionSite;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;

public class ShowSitesDetails extends JDialog {
    
    private Connection conn;
    private final ConstructionSiteDAO siteDAO = new ConstructionSiteDAO(conn);
    public ShowSitesDetails(JFrame parent, ConstructionSite site,Connection connection) throws SQLException {
        
    }
}
