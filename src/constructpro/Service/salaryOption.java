package constructpro.Service;

import javax.swing.*;
import java.sql.*;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.Worker;

public class salaryOption extends JDialog{
    private Connection conn;
    private WorkerDAO workerDAO;
    private Worker worker;
    public salaryOption(JFrame parent, Worker worker,Connection connection) throws SQLException {
        super(parent, "Choisissez un Option", true);
        this.workerDAO = new WorkerDAO(connection);
        this.worker= worker;
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
