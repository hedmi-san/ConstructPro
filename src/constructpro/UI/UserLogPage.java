package constructpro.UI;

import javax.swing.*;
import java.sql.SQLException;
import constructpro.DAO.UserDAO;
import java.awt.*;
import java.awt.event.*;

public class UserLogPage extends JPanel{
    
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JScrollPane jScrollPane1;
    private JSeparator jSeparator1;
    private JTable logTable;
    private JButton refreshButton;
    private JTextField searchText;
    
    public UserLogPage() {
        initComponents();
        loadDataSet();
    }
    
    @SuppressWarnings("unchecked")
    private void initComponents(){
        jLabel1 = new JLabel();
        jSeparator1 = new JSeparator();
        jScrollPane1 = new JScrollPane();
        logTable = new JTable();
        refreshButton = new JButton();
        searchText = new JTextField();
        jLabel2 = new JLabel();
        
        jLabel1.setFont(new Font("SansSerif", Font.BOLD, 22));
        jLabel1.setText("Logs");

        logTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(logTable);

        refreshButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); 
        refreshButton.setText("REFRESH");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        searchText.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                searchTextKeyReleased(evt);
            }
        });

        jLabel2.setText("Search:");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1,GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1,GroupLayout.PREFERRED_SIZE, 129,GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2,GroupLayout.PREFERRED_SIZE, 48,GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchText,GroupLayout.PREFERRED_SIZE, 153,GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(refreshButton,GroupLayout.PREFERRED_SIZE, 89,GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(refreshButton)
                        .addComponent(searchText,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(jLabel1,GroupLayout.PREFERRED_SIZE, 40,GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1,GroupLayout.PREFERRED_SIZE, 10,GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1,GroupLayout.PREFERRED_SIZE, 407,GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
    }
    
    private void searchTextKeyReleased(KeyEvent evt) {
    }
    
    private void refreshButtonActionPerformed(ActionEvent evt) {
        loadDataSet();
    }
    
    public void loadDataSet(){
        try {
            UserDAO userDAO = new UserDAO();
            logTable.setModel(userDAO.buildTableModel(userDAO.getUserLogsDAO()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
