/**
 * Parent class used by all pages in the system. Contains a mainPanel which fills the whole frame / window and the required
 * methods and variables used to generate the content on the mainPanel.
 */
package UserInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import Core.VaccineSystem;

public class Page implements ActionListener {
    protected JPanel mainPanel;
    protected VaccineSystem vaccineSystem;
    protected ArrayList<JButton> buttons;
    protected String buttonAction;

    public Page(VaccineSystem vaccineSystem) {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        buttons = new ArrayList<>();
        this.vaccineSystem = vaccineSystem;
    }

    public Page() {}

    /**
     * Adds the given button to the given panel and gives the button an action listener so the button being pressed will be
     * detected in actionPerformed()
     */
    protected void addButton(JButton button, JPanel panel) {
        button.addActionListener(this);
        panel.add(button);
    }

    /**
     * Adds the given label, followed by the given component to the given panel so the user can easily identify what the
     * component is for.
     * For example, if given a JTextField and the label "Name:", the panel would be given "Name: [text field]".
     * Requires a panel with a grid layout with 2 columns.
     * @param panel The panel the label and component are added to
     * @param label The component's label text
     * @param component the component to be added to the panel
     * @return the panel with the added label and component
     */
    public static JPanel addLabelledComponent(JPanel panel, String label, JComponent component) {
        panel.add(new JLabel(label));
        panel.add(component);
        return panel;
    }

    /**
     * Sets the component's width to the window's width, and it's height to the minimum height it will fit in. Helps make
     * components on the screen appear aesthetically pleasing
     * @param component the component to re-adjust
     */
    protected void setMaxWidthMinHeight(JComponent component) {
        int width = vaccineSystem.getWidth();
        int height = component.getMinimumSize().height;
        component.setMaximumSize(new Dimension(width, height));
    }

    /**
     * Creates a panel which can be used to input an integer value between the given minimum and maximum value. Removes the
     * need to check the user's input as the user cannot input anything outside the minimum and maximum values.
     * @param minValue smallest value the user can input
     * @param maxValue largest value the user can input
     * @param columns how many digits the input field shows
     * @return the JSpinner input field
     */
    public static JSpinner createJSpinner(int minValue, int maxValue, int columns) {
        ArrayList<Integer> possibleValues = new ArrayList<>();
        for (int i = minValue; i < maxValue; i++) {
            possibleValues.add(i);
        }

        SpinnerListModel spinnerListModel = new SpinnerListModel(possibleValues);

        JSpinner spinner = new JSpinner(spinnerListModel);
        JFormattedTextField textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        textField.setEditable(false);
        textField.setColumns(columns);

        return spinner;
    }

    /**
     * Creates a panel which can be used to input a time
     * @param hourSpinner Input method used to input the hour
     * @param minuteSpinner Input method used to input the minute
     * @param initialHour what the default hour should be
     * @param initialMin what the default minute should be
     * @return the time panel
     */
    protected JPanel createTimePanel(JSpinner hourSpinner, JSpinner minuteSpinner, int initialHour, int initialMin) {
        hourSpinner.setValue(initialHour);
        minuteSpinner.setValue(initialMin);

        JPanel timePanel = new JPanel();;
        timePanel.add(hourSpinner);
        timePanel.add(new JLabel(":"));
        timePanel.add(minuteSpinner);

        return timePanel;
    }

    protected void createPopupFrame(JFrame frame, JPanel panel, int width, int height) {
        frame.add(panel);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null); // Sets window to centre of screen
        frame.setVisible(true);
    }

    public void errorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void errorMessage(String message, boolean displayError) {
        if (displayError) {
            errorMessage(message);
        }
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
