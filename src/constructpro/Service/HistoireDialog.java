package constructpro.Service;

import constructpro.DAO.SalaryRecordDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.Worker;
import javax.swing.*;
import java.sql.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class HistoireDialog extends JDialog {
    private Connection conn;
    private WorkerDAO workerDAO;
    
    public HistoireDialog(JFrame parent,Worker worker,Connection connection) throws SQLException {
        super(parent, "Histoire", true);
        this.workerDAO = new WorkerDAO(connection);
        this.conn = connection;
        initializeComponents();
        setupLayout();
        populateData();
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    private void initializeComponents(){} 
    private void setupLayout(){}
    private void populateData(){}
}

