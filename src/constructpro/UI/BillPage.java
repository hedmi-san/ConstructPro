package constructpro.UI;

import javax.swing.*;
import java.sql.*;
import constructpro.DAO.BillDAO;
import constructpro.DTO.Bill;
import constructpro.DAO.ConstructionSiteDAO;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;


public class BillPage extends JPanel{
    private JButton refreshButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton addButton;
    private JTextField searchText;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTable billsTable;
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
