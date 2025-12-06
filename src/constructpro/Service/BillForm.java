package constructpro.Service;

import constructpro.DTO.Bill;
import constructpro.DAO.BillDAO;
import constructpro.DAO.BiLLItemDAO;
import javax.swing.*;
import java.sql.*;

public class BillForm extends JDialog{
    private JTextField factureNumberTextField,transferFeeTextField;
    private JComboBox supplierComboBox,siteComboBox;
    private JButton addItemButton,deleteItemButton,saveButton,cancelButton;
    private JPanel billItemPanel;
    private Connection conn;
    private Bill existingBill;
    private boolean confirmed = false;

    public BillForm(JFrame parent, String title, Bill bill, Connection connection) {
        super(parent, title, true);
        this.conn = conn;
        
    }
    
    
    
}
