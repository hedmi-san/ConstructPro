package constructpro.UI;

import constructpro.Service.AttachmentFileForm;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AttachmentPage extends JPanel {

    private JButton attachmentFileButton;
    private JFrame parentFrame;
    public Connection conn;

    public AttachmentPage(Connection connection) {
        this.conn = connection;
        initDAO();
        initComponents();
        setupButtonActions();
        loadDataSet();
    }

    private void initDAO() {
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 50, 20)); // Added bottom padding to lift button up
        attachmentFileButton = new JButton("Fiche Attachement");
        bottomPanel.add(attachmentFileButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadDataSet() {

    }

    private void setupButtonActions() {
        attachmentFileButton.addActionListener(e -> {
            try {
                // Determine the parent frame
                JComponent comp = this;
                while (comp.getParent() != null && !(comp.getParent() instanceof JFrame)) {
                    comp = (JComponent) comp.getParent();
                }
                JFrame parent = (JFrame) (comp.getParent() instanceof JFrame ? comp.getParent() : null);

                AttachmentFileForm dialog = new AttachmentFileForm(parent, "Ajouter une fiche d'attachement", conn);
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    // Logic after confirmation if needed
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

}
