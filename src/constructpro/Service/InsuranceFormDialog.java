package constructpro.Service;

import constructpro.DTO.Insurance;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class InsuranceFormDialog extends JDialog {

    private JTextField insuranceNumberField;
    private JTextField agencyNameField;
    private JComboBox<String> statusComboBox;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private DefaultListModel<String> documentListModel;
    private JList<String> documentList;

    private Insurance insurance; // Will be null in "Add" mode
    private boolean saved = false;
    private int workerId;
    private static final String[] DOCUMENT_OPTIONS = {
        "Acte de Naissance",
        "Fiche familiale de l'état civil",
        "Photocopie de la carte identité",
        "Photocopie de chèque"
    };

     public InsuranceFormDialog(Window owner, Insurance insurance, int workerId) {
        super(owner, "Formulaire d'assurance", ModalityType.APPLICATION_MODAL);
        this.insurance = insurance;
        this.workerId = workerId;

        setTitle(insurance == null ? "Add Insurance" : "Edit Insurance");
        setSize(500, 500);
        setLocationRelativeTo(owner);

        initUI();
        loadDataIfEditMode();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // === Fields Panel ===
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        insuranceNumberField = new JTextField();
        agencyNameField = new JTextField();
        statusComboBox = new JComboBox<>(new String[]{"Active", "Non Active", "Bureau"});
        startDateChooser = new JDateChooser();
        endDateChooser = new JDateChooser();

        int row = 0;
        addField(fieldsPanel, gbc, row++, "Numéro d'assurance:", insuranceNumberField);
        addField(fieldsPanel, gbc, row++, "Nom de l'agence:", agencyNameField);
            addField(fieldsPanel, gbc, row++, "Statut:", statusComboBox);
        addField(fieldsPanel, gbc, row++, "Date de début:", startDateChooser);
        addField(fieldsPanel, gbc, row++, "Date de fin:", endDateChooser);

        // === Documents Panel ===
        documentListModel = new DefaultListModel<>();
        documentList = new JList<>(documentListModel);
        JScrollPane documentScroll = new JScrollPane(documentList);
        documentScroll.setPreferredSize(new Dimension(200, 100));

        // ComboBox for document selection
        JComboBox<String> documentComboBox = new JComboBox<>(DOCUMENT_OPTIONS);

        // Add / Remove buttons
        JButton addDocButton = new JButton("Ajouter Document");
        JButton removeDocButton = new JButton("Supprimer sélectionné");

        addDocButton.addActionListener(e -> {
            String selectedDoc = (String) documentComboBox.getSelectedItem();
            if (selectedDoc != null && !documentListModel.contains(selectedDoc)) {
                documentListModel.addElement(selectedDoc);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Document déjà ajouté ou non sélectionné",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        removeDocButton.addActionListener(e -> {
            int selectedIndex = documentList.getSelectedIndex();
            if (selectedIndex != -1) {
                documentListModel.remove(selectedIndex);
            }
        });

        // Top panel with combo + add button
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.add(documentComboBox);
        addPanel.add(addDocButton);

        // Bottom panel with remove button
        JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        removePanel.add(removeDocButton);

        // Final document panel
        JPanel docPanel = new JPanel(new BorderLayout(5, 5));
        docPanel.setBorder(BorderFactory.createTitledBorder("Documents d'assurance"));
        docPanel.add(documentScroll, BorderLayout.CENTER);
        docPanel.add(addPanel, BorderLayout.NORTH);
        docPanel.add(removePanel, BorderLayout.SOUTH);

        // === Bottom Buttons ===
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        saveButton.addActionListener(this::saveInsurance);
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // === Assemble ===
        panel.add(fieldsPanel, BorderLayout.NORTH);
        panel.add(docPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void loadDataIfEditMode() {
        if (insurance != null) {
            insuranceNumberField.setText(insurance.getInsuranceNumber());
            agencyNameField.setText(insurance.getAgencyName());
            statusComboBox.setSelectedItem(insurance.getStatus());

            if (insurance.getStartDate() != null) {
                startDateChooser.setDate(Date.from(insurance.getStartDate()
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            if (insurance.getEndDate() != null) {
                endDateChooser.setDate(Date.from(insurance.getEndDate()
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }

            if (insurance.getInsuranceDocuments() != null) {
                for (String doc : insurance.getInsuranceDocuments()) {
                    documentListModel.addElement(doc);
                }
            }
        }
    }

//    private void addDocument() {
//        String docName = JOptionPane.showInputDialog(this, "Enter document name:");
//        if (docName != null && !docName.trim().isEmpty()) {
//            documentListModel.addElement(docName.trim());
//        }
//    }
//
//    private void removeSelectedDocument() {
//        int selectedIndex = documentList.getSelectedIndex();
//        if (selectedIndex != -1) {
//            documentListModel.remove(selectedIndex);
//        }
//    }

    private void saveInsurance(ActionEvent e) {
        if (insurance == null) {
            insurance = new Insurance();
        }

        insurance.setInsuranceNumber(insuranceNumberField.getText().trim());
        insurance.setAgencyName(agencyNameField.getText().trim());
        insurance.setStatus((String) statusComboBox.getSelectedItem());
        insurance.setWorkerId(workerId);

        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();

        if (startDate != null) {
            insurance.setStartDate(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        if (endDate != null) {
            insurance.setEndDate(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        insurance.setInsuranceDocuments(new ArrayList<>());
        for (int i = 0; i < documentListModel.size(); i++) {
            insurance.getInsuranceDocuments().add(documentListModel.get(i));
        }

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Insurance getInsurance() {
        return insurance;
    }
}
