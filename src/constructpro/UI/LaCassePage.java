package constructpro.UI;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class LaCassePage extends JPanel{
    
    public Connection conn;
    
    public LaCassePage(Connection connection) {
        this.conn = connection;
        initDAO();
        initComponents();
        loadDataSet();
    }
    
    private void initDAO(){}
    
    private void initComponents(){}
    
    private void loadDataSet(){}
}
