package constructpro.Service;

import constructpro.DTO.Bill;
import constructpro.DAO.BillDAO;
import constructpro.DAO.BiLLItemDAO;
import constructpro.DAO.SupplierDAO;
import constructpro.DAO.ConstructionSiteDAO;
import javax.swing.*;
import java.sql.*;

public class BillForm extends JDialog{
    private JTextField factureNumberTextField,transferFeeTextField;
    private JComboBox supplierComboBox,siteComboBox;
    private JButton addItemButton,deleteItemButton,saveButton,cancelButton;
    private JPanel billItemPanel;
    private SupplierDAO supplierDAO;
    private ConstructionSiteDAO siteDAO;
    private BillDAO billDAO;
    private Connection conn;
    private Bill existingBill;
    private boolean confirmed = false;

    public BillForm(JFrame parent, String title, Bill bill, Connection connection) throws SQLException {
        super(parent, title, true);
        this.conn = conn;
        this.siteDAO = new ConstructionSiteDAO(connection);
        this.supplierDAO = new SupplierDAO(connection);
        this.billDAO= new BillDAO(connection);
        initComponents();
        setupLayout();
        setupActions();

        if (bill != null) {
            populateFields(bill);
        }

        pack();
        setLocationRelativeTo(parent);
        
    }
    
    private void initComponents(){
    
    }
            
    private void setupLayout(){
    
    }
            
    private void setupActions(){
    
    }
            
    private void populateFields(Bill bill){
    
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    //public Bill getBillFromForm(){}
}
