package constructpro.UI;

import constructpro.DAO.UserDAO;
import constructpro.DTO.User;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class UsersPage extends JPanel {

    private JTable userTable;
    private JTextField searchField;
    private JTextField nameField, locationField, phoneField, usernameField;
    private JPasswordField passField;
    private JComboBox<String> typeCombo;
    private JButton addButton, updateButton, deleteButton, clearButton;

    public UsersPage(Connection connection) {
        initComponents();
        loadDataSet();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(25, 25, 25, 25));
        setOpaque(false);

        // --- Left Section: Table & Search ---
        JPanel tableContainer = new JPanel(new BorderLayout(0, 15));
        tableContainer.setOpaque(false);

        // Header & Search
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Gestion des Utilisateurs");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Rechercher des utilisateurs par nom ou nom d'utilisateur...");
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchUsers();
            }
        });

        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchWrapper.setOpaque(false);
        searchWrapper.add(searchField);
        topPanel.add(searchWrapper, BorderLayout.EAST);

        tableContainer.add(topPanel, BorderLayout.NORTH);

        // Table
        userTable = new JTable();
        userTable.setRowHeight(35);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setDefaultEditor(Object.class, null);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTable.setShowGrid(false);
        userTable.setIntercellSpacing(new Dimension(0, 0));
        

        // Selection handling
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableSelection();
            }
        });

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        scrollPane.putClientProperty("FlatLaf.style", "arc: 12");
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);

        // --- Right Section: Management Form ---
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(new Color(45, 45, 45));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
                new EmptyBorder(25, 25, 25, 25)));
        formCard.setPreferredSize(new Dimension(350, 0));
        formCard.putClientProperty("FlatLaf.style", "arc: 15");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.weightx = 1;
        gbc.gridx = 0;

        JLabel formTitle = new JLabel("Détails de l'utilisateur");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(new Color(200, 200, 200));
        gbc.gridy = 0;
        formCard.add(formTitle, gbc);

        // Fields
        nameField = createStyledField("Nom complet");
        gbc.gridy = 1;
        formCard.add(createLabeledPanel("NOM COMPLET", nameField), gbc);

        locationField = createStyledField("Location");
        gbc.gridy = 2;
        formCard.add(createLabeledPanel("LOCATION", locationField), gbc);

        phoneField = createStyledField("Numéro de téléphone");
        gbc.gridy = 3;
        formCard.add(createLabeledPanel("NUMÉRO DE TÉLÉPHONE", phoneField), gbc);

        usernameField = createStyledField("Username");
        gbc.gridy = 4;
        formCard.add(createLabeledPanel("USERNAME", usernameField), gbc);

        passField = new JPasswordField();
        passField.setPreferredSize(new Dimension(0, 35));
        passField.putClientProperty("FlatLaf.style", "arc: 8");
        gbc.gridy = 5;
        formCard.add(createLabeledPanel("MOT DE PASSE", passField), gbc);

        typeCombo = new JComboBox<>(new String[] { "Admin", "Employee" });
        typeCombo.setPreferredSize(new Dimension(0, 35));
        typeCombo.putClientProperty("FlatLaf.style", "arc: 8");
        gbc.gridy = 6;
        formCard.add(createLabeledPanel("USER TYPE", typeCombo), gbc);

        // Buttons
        JPanel buttonGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonGrid.setOpaque(false);

        addButton = new JButton("Ajouter un Utilisateur");
        addButton.putClientProperty("FlatLaf.style", "background: #27ae60; foreground: #ffffff; arc: 8");
        addButton.addActionListener(e -> addUser());

        updateButton = new JButton("Mettre à jour");
        updateButton.putClientProperty("FlatLaf.style", "background: #2980b9; foreground: #ffffff; arc: 8");
        updateButton.addActionListener(e -> updateUser());

        deleteButton = new JButton("Supprimer");
        deleteButton.putClientProperty("FlatLaf.style", "background: #c0392b; foreground: #ffffff; arc: 8");
        deleteButton.addActionListener(e -> deleteUser());

        clearButton = new JButton("Clair");
        clearButton.addActionListener(e -> clearForm());

        buttonGrid.add(addButton);
        buttonGrid.add(updateButton);
        buttonGrid.add(deleteButton);
        buttonGrid.add(clearButton);

        gbc.gridy = 7;
        gbc.insets = new Insets(10, 0, 0, 0);
        formCard.add(buttonGrid, gbc);

        // Spacer
        gbc.gridy = 8;
        gbc.weighty = 1;
        formCard.add(Box.createVerticalGlue(), gbc);

        add(formCard, BorderLayout.EAST);
    }

    private JTextField createStyledField(String placeholder) {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 35));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("FlatLaf.style", "arc: 8");
        return field;
    }

    private JPanel createLabeledPanel(String labelText, JComponent component) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(new Color(120, 120, 120));
        p.add(l, BorderLayout.NORTH);
        p.add(component, BorderLayout.CENTER);
        return p;
    }

    public void loadDataSet() {
        try {
            UserDAO dao = new UserDAO();
            userTable.setModel(dao.buildTableModel(dao.getQueryResult()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchUsers() {
        String term = searchField.getText();
        try {
            UserDAO dao = new UserDAO();
            userTable.setModel(dao.buildTableModel(dao.searchUsers(term)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleTableSelection() {
        int row = userTable.getSelectedRow();
        if (row >= 0) {
            nameField.setText(userTable.getValueAt(row, 1).toString());
            locationField.setText(userTable.getValueAt(row, 2).toString());
            phoneField.setText(userTable.getValueAt(row, 3).toString());
            usernameField.setText(userTable.getValueAt(row, 4).toString());
            usernameField.setEditable(false);
            typeCombo.setSelectedItem(userTable.getValueAt(row, 6).toString());
        }
    }

    private void addUser() {
        if (validateFields()) {
            User u = new User();
            u.setFullName(nameField.getText());
            u.setLocation(locationField.getText());
            u.setPhone(phoneField.getText());
            u.setUserName(usernameField.getText());
            u.setPassword(new String(passField.getPassword()));
            u.setUserType((String) typeCombo.getSelectedItem());

            new UserDAO().addUserDAO(u, u.getUserType());
            loadDataSet();
            clearForm();
        }
    }

    private void updateUser() {
        if (userTable.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to update.");
            return;
        }
        if (validateFields()) {
            User u = new User();
            u.setUserName(usernameField.getText());
            u.setFullName(nameField.getText());
            u.setLocation(locationField.getText());
            u.setPhone(phoneField.getText());
            u.setUserType((String) typeCombo.getSelectedItem());

            new UserDAO().editUserDAO(u);
            loadDataSet();
        }
    }

    private void deleteUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur à supprimer.");
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this, "Supprimer l'utilisateur " + userTable.getValueAt(row, 4) + "?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            new UserDAO().deleteUserDAO(userTable.getValueAt(row, 4).toString());
            loadDataSet();
            clearForm();
        }
    }

    private void clearForm() {
        nameField.setText("");
        locationField.setText("");
        phoneField.setText("");
        usernameField.setText("");
        usernameField.setEditable(true);
        passField.setText("");
        typeCombo.setSelectedIndex(0);
        userTable.clearSelection();
    }

    private boolean validateFields() {
        if (nameField.getText().isEmpty() || usernameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom et le nom d'utilisateur sont obligatoires.");
            return false;
        }
        return true;
    }
}
