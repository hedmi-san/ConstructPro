package constructpro.UI;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DTO.ConstructionSite;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;


public class ConstructionSitePage extends JPanel{
    
    private JButton deleteButton;
    private JButton editButton;
    private JButton addButton;
    private JButton refreshButton;
    private JTextField searchText;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTable workerstTable;
    private JScrollPane jScrollPane1;
    private ConstructionSiteDAO SiteDAO;
    private JFrame parentFrame;
    public Connection conn;
    public ConstructionSitePage(Connection connection) {
        this.conn = connection;
        initDAO();
        initComponents();
        loadDataSet();
    }
    
    public ConstructionSitePage(JFrame parent) {
        this.parentFrame = parent;
        initDAO();
        initComponents();
        loadDataSet();
    }
    
    private void initDAO() {
        try {
            SiteDAO = new ConstructionSiteDAO(conn);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données: " + e.getMessage());
        }
    }
    
    private void initComponents() {}
    
    private void loadDataSet() {}
    
}
