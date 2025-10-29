package constructpro.UI;

import javax.swing.*;
import java.sql.*;


public class BillPage extends JPanel{
    private JButton refreshButton;
    private JTextField searchText;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTable activeSitesTable;
    private JScrollPane jScrollPane1;
    private JFrame parentFrame;
    private Connection conn;
    
    public BillPage(Connection connection) {
       this.conn = connection;
        initDAO();
        initComponents();
        loadDataSet(); 
    }
    
    private void initDAO(){
    
    }
    private void initComponents(){
        
    }
    private void loadDataSet(){
        
    }
}
