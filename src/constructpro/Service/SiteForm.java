package constructpro.Service;

import constructpro.DTO.ConstructionSite;
import constructpro.DAO.ConstructionSiteDAO;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class SiteForm extends JDialog {

    private JTextField nameField, locationField, totalCostField;
    private JComboBox<String> statusComboBox;
    private JDateChooser startDateChooser, endDateChooser;
    private JButton saveButton, cancelButton;
    private boolean confirmed = false;

    private ConstructionSite existingSite;
    private ConstructionSiteDAO siteDAO;

    public SiteForm(JFrame parent, String title, ConstructionSite site, Connection connection) throws SQLException {
        super(parent, title, true);
        this.existingSite = site;
        this.siteDAO = new ConstructionSiteDAO(connection);

        initComponents();
        setupLayout();
        setupActions();

        if (site != null) {
            populateFields(site);
        }

        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        nameField = new JTextField(20);
        locationField = new JTextField(20);
        totalCostField = new JTextField(20);

        // Status dropdown
        statusComboBox = new JComboBox<>(new String[] {
            "Active","Terminé", "Non Spécifié"
        });

        // Date choosers
        startDateChooser = new JDateChooser();
        endDateChooser = new JDateChooser();

        startDateChooser.setDateFormatString("dd/MM/yyyy");
        endDateChooser.setDateFormatString("dd/MM/yyyy");

        Dimension dateChooserSize = new Dimension(200, 25);
        startDateChooser.setPreferredSize(dateChooserSize);
        endDateChooser.setPreferredSize(dateChooserSize);

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Site Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(nameField, gbc);
        row++;

        // Location
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(locationField, gbc);
        row++;

        // Status
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(statusComboBox, gbc);
        row++;

        // Start Date
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(startDateChooser, gbc);
        row++;

        // End Date
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(endDateChooser, gbc);
        row++;

        // Total Cost
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Total Cost (DA):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(totalCostField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupActions() {
        saveButton.addActionListener(e -> {
            if (validateFields()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
    }

    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Site name is required!");
            return false;
        }

        if (locationField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Location is required!");
            return false;
        }

        if (startDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Start date is required!");
            return false;
        }

        if (endDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "End date is required!");
            return false;
        }

        if (endDateChooser.getDate().before(startDateChooser.getDate())) {
            JOptionPane.showMessageDialog(this, "End date cannot be before start date!");
            return false;
        }

        try {
            if (!totalCostField.getText().trim().isEmpty()) {
                Double.valueOf(totalCostField.getText().trim());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Total cost must be a valid number!");
            return false;
        }

        return true;
    }

    private void populateFields(ConstructionSite site) {
        nameField.setText(site.getName());
        locationField.setText(site.getLocation());
        statusComboBox.setSelectedItem(site.getStatus() != null ? site.getStatus() : "Not Started");

        if (site.getStartDate() != null)
            startDateChooser.setDate(Date.from(site.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        if (site.getEndDate() != null)
            endDateChooser.setDate(Date.from(site.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        totalCostField.setText(String.valueOf(site.getTotalCost()));
    }

    private LocalDate convertToLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public ConstructionSite getSiteFromForm() {
        ConstructionSite site = new ConstructionSite();
        site.setName(nameField.getText().trim());
        site.setLocation(locationField.getText().trim());
        site.setStatus((String) statusComboBox.getSelectedItem());
        site.setStartDate(convertToLocalDate(startDateChooser.getDate()));
        site.setEndDate(convertToLocalDate(endDateChooser.getDate()));

        String totalCostStr = totalCostField.getText().trim();
        if (!totalCostStr.isEmpty()) {
            try {
                site.setTotalCost(Double.parseDouble(totalCostStr));
            } catch (NumberFormatException ex) {
                site.setTotalCost(0);
            }
        } else {
            site.setTotalCost(0);
        }
        return site;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
