package constructpro.UI;
import constructpro.DAO.BiLLItemDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class AttachmentPage  extends JPanel{
    
    public Connection conn;
    public AttachmentPage(Connection connection){
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
    
    private void loadSearchResults(String searchTerm) {
        
    }
}
