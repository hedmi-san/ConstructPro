package constructpro.Utils;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.util.Locale;
import javax.swing.JButton;

/**
 * Utility class to configure JDateChooser components globally.
 */
public class DateChooserConfigurator {

    /**
     * Configures a JDateChooser with French locale and a white background for the
     * calendar.
     * 
     * @param dateChooser The JDateChooser to configure.
     */
    public static void configure(JDateChooser dateChooser) {
        if (dateChooser == null)
            return;

        // Set French locale
        dateChooser.setLocale(Locale.FRENCH);

        // Customize the calendar popup colors
        dateChooser.getJCalendar().getDayChooser().getDayPanel().setBackground(Color.WHITE);
        dateChooser.getJCalendar().setWeekdayForeground(Color.BLACK);
        dateChooser.getJCalendar().setDecorationBackgroundColor(Color.WHITE);

        // Ensure the editor also follows a readable style
        if (dateChooser.getDateEditor() instanceof JTextFieldDateEditor) {
            JTextFieldDateEditor editor = (JTextFieldDateEditor) dateChooser.getDateEditor();
            editor.setForeground(Color.WHITE);
            editor.setCaretColor(Color.WHITE);
            editor.setDisabledTextColor(Color.WHITE);
            // Ensure background is dark enough for white text
            editor.setBackground(new Color(182, 182, 182));
        }

        // Fix Calendar header components (Month and Year choosers)
        com.toedter.calendar.JCalendar calendar = dateChooser.getJCalendar();

        // Style the year chooser (usually a spinner)
        calendar.getYearChooser().getComponent(0).setForeground(Color.WHITE);
        if (calendar.getYearChooser().getComponent(0) instanceof javax.swing.JSpinner) {
            javax.swing.JSpinner spinner = (javax.swing.JSpinner) calendar.getYearChooser().getComponent(0);
            javax.swing.JComponent editor = spinner.getEditor();
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                ((javax.swing.JSpinner.DefaultEditor) editor).getTextField().setForeground(Color.WHITE);
                ((javax.swing.JSpinner.DefaultEditor) editor).getTextField().setBackground(new Color(182, 182, 182));
            }
        }

        // Style the month chooser (usually a combo box)
        calendar.getMonthChooser().getComboBox().setForeground(Color.WHITE);
        calendar.getMonthChooser().getComboBox().setBackground(new Color(45, 45, 45));

        // Make the button icon visible if it's dark
        for (java.awt.Component comp : dateChooser.getComponents()) {
            if (comp instanceof JButton) {
                comp.setBackground(new Color(60, 60, 60));
                comp.setForeground(Color.WHITE);
            }
        }
    }
}
