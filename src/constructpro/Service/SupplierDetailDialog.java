package constructpro.Service;

import java.awt.Color;
import constructpro.DTO.Supplier;
import constructpro.DAO.SupplierDAO;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class SupplierDetailDialog extends JDialog {
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color DARKER_BACKGROUND = new Color(35, 35, 35);
    private static final Color ACCENT_COLOR = new Color(0, 120, 215);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color LABEL_COLOR = new Color(180, 180, 180);
    
    private Supplier currentSupplier;
    private SupplierDAO supplierDAO;
    private JTabbedPane tabbedPane;
    private JPanel infoPanel;
    private JPanel financeOperationPanel;
    private JTable billsTable,financeOperationTable;
    private DefaultTableModel tableModel1,tableModel2;
    private JButton addButton,deleteButton;
    
    private Connection conn;
    
    public SupplierDetailDialog(JFrame parent,Supplier supplier, Connection connection){
        super(parent, "Détails de Fournisseure", true);
        this.conn = connection;
        
        initializeComponents();
        setupLayout();
        setupStyling();
        populateData();

        setSize(900, 650);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents(){
        
    }
    private void setupLayout(){
        
    }
    private void setupStyling(){
        
    }
    private void populateData(){
        
    }
}
