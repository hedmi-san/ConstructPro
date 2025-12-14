package constructpro.UI;

import javax.swing.*;
import constructpro.DAO.UserDAO;
import constructpro.DTO.User;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.*;

public class UsersPage extends JPanel{
    private JButton addButton;
    private JButton clearButton;
    private JButton deleteButton;
    private JPanel entryPanel;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JScrollPane jScrollPane1;
    private JSeparator jSeparator1;
    private JTextField locationText;
    private JTextField nameText;
    private JPasswordField passText;
    private JTextField phoneText;
    private JTable userTable;
    private JComboBox<String> userTypeCombo;
    private JTextField usernameText;
    String userType;;


    
    public UsersPage(Connection connection) {
        initComponents();
        loadDataSet();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        // --- Components ---
        jLabel1 = new JLabel("Users");
        jSeparator1 = new JSeparator();
        entryPanel = new JPanel(new GridBagLayout());
        jLabel2 = new JLabel("Full Name:");
        jLabel3 = new JLabel("Location:");
        jLabel4 = new JLabel("Contact:");
        jLabel5 = new JLabel("Username:");
        jLabel6 = new JLabel("Password:");
        nameText = new JTextField(15);
        locationText = new JTextField(15);
        phoneText = new JTextField(15);
        usernameText = new JTextField(15);
        passText = new JPasswordField(15);
        userTypeCombo = new JComboBox<>(new String[] { "Admin", "Employee" });
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");
        jScrollPane1 = new JScrollPane();
        userTable = new JTable();

        // --- Header label ---
        jLabel1.setFont(new Font("SansSerif", Font.BOLD, 22));

        // --- Entry Panel (form) ---
        entryPanel.setBorder(BorderFactory.createTitledBorder("Enter User Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0 - Full Name
        gbc.gridx = 0; gbc.gridy = 0;
        entryPanel.add(jLabel2, gbc);
        gbc.gridx = 1;
        entryPanel.add(nameText, gbc);

        // Row 1 - Location
        gbc.gridx = 0; gbc.gridy = 1;
        entryPanel.add(jLabel3, gbc);
        gbc.gridx = 1;
        entryPanel.add(locationText, gbc);

        // Row 2 - Contact
        gbc.gridx = 0; gbc.gridy = 2;
        entryPanel.add(jLabel4, gbc);
        gbc.gridx = 1;
        entryPanel.add(phoneText, gbc);

        // Row 3 - Username
        gbc.gridx = 0; gbc.gridy = 3;
        entryPanel.add(jLabel5, gbc);
        gbc.gridx = 1;
        entryPanel.add(usernameText, gbc);

        // Row 4 - Password
        gbc.gridx = 0; gbc.gridy = 4;
        entryPanel.add(jLabel6, gbc);
        gbc.gridx = 1;
        entryPanel.add(passText, gbc);

        // Row 5 - Combo
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        entryPanel.add(userTypeCombo, gbc);

        // Row 6 - Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        entryPanel.add(buttonPanel, gbc);

        // --- Table ---
        userTable.setBorder(BorderFactory.createEtchedBorder());
        userTable.setForeground(new Color(102, 102, 102));
        userTable.setDefaultEditor(Object.class, null);
        userTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] { "Title 1", "Title 2", "Title 3", "Title 4" }
        ) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        userTable.setShowGrid(true);
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(userTable);

        // --- Main Layout (GridBag for whole panel) ---
        this.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1; gbc.weighty = 0;

        // Row 0 - Header
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        this.add(jLabel1, gbc);

        // Row 1 - Separator
        gbc.gridy = 1;
        this.add(jSeparator1, gbc);

        // Row 2 - Table + EntryPanel side by side
        gbc.gridy = 2; gbc.gridwidth = 1; gbc.weighty = 1;

        gbc.gridx = 0; gbc.weightx = 0.6;
        this.add(jScrollPane1, gbc);

        gbc.gridx = 1; gbc.weightx = 0.4;
        this.add(entryPanel, gbc);
    }


    public void loadDataSet(){
        try {
            UserDAO userDAO = new UserDAO();
            userTable.setModel(userDAO.buildTableModel(userDAO.getQueryResult()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void userTableMouseClicked(MouseEvent evt) {
        int row = userTable.getSelectedRow();
        int col = userTable.getColumnCount();
        Object[] val = new Object[col];

        for(int i=0; i<col; i++) {
            val[i] = userTable.getValueAt(row, i);
        }
        nameText.setText(val[1].toString());
        locationText.setText(val[2].toString());
        phoneText.setText(val[3].toString());
        usernameText.setText(val[4].toString());
        userTypeCombo.setSelectedItem(val[6].toString());
    }

    private void deleteButtonActionPerformed(ActionEvent evt) {
        if (userTable.getSelectedRow()<0)
            JOptionPane.showMessageDialog(null, "Please select an entry from the table");
        else{
            int opt = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to delete this user?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if(opt==JOptionPane.YES_OPTION) {
                new UserDAO().deleteUserDAO(
                        String.valueOf(
                                userTable.getValueAt
                                        (userTable.getSelectedRow(), 4)));
                loadDataSet();
            }
        }
    }


    private void addButtonActionPerformed(ActionEvent evt) {
        User userDTO = new User();

        if (nameText.getText().equals("") || locationText.getText().equals("") || phoneText.getText().equals(""))
            JOptionPane.showMessageDialog(null, "Please fill all the required fields.");
        else {
            userType = (String) userTypeCombo.getSelectedItem();
            userDTO.setFullName(nameText.getText());
            userDTO.setLocation(locationText.getText());
            userDTO.setPhone(phoneText.getText());
            userDTO.setUserName(usernameText.getText());
            userDTO.setPassword(passText.getText());
            userDTO.setUserType(userType);
            new UserDAO().addUserDAO(userDTO, userType);
            loadDataSet();
        }
    }

    private void clearButtonActionPerformed(ActionEvent evt) {
        nameText.setText("");
        locationText.setText("");
        phoneText.setText("");
        usernameText.setText("");
        passText.setText("");
    }
}