import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

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

    protected void errorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected JPanel addLabelledComponent(JPanel panel, String label, JComponent component) {
        panel.add(new JLabel(label));
        panel.add(component);
        return panel;
    }

    protected void addButton(JButton button, JPanel panel) {
        button.addActionListener(this);
        panel.add(button);
    }

    protected String getSanitizedButtonText(JButton button) {
        return button.getText().replace(" ", "").toLowerCase();
    }


    protected void setMaxWidthMinHeight(JPanel panel) {
        int width = vaccineSystem.getWidth();
        int height = panel.getMinimumSize().height;
        panel.setMaximumSize(new Dimension(width, height));
    }

    protected JList getColumnsAsJList(String[] columnNames, String tableName) {

        DefaultListModel<String> listModel = new DefaultListModel<>();
        Object[] records = getFormattedSelect(columnNames, tableName);

        for (Object record : records) {
            listModel.addElement((String) record);
        }
        return (new JList<>(listModel));
    }

    protected Object[] getFormattedSelect(String[] columnNames, String tableName) {
        ArrayList<String> output = new ArrayList<>();

        try {
            ArrayList<ArrayList<String>> resultSet = vaccineSystem.executeSelect(columnNames, tableName);

            for (ArrayList<String> record : resultSet) {
                String addToOutput = record.get(0) + ":";

                for (int i = 1; i < record.size(); i++) {
                    addToOutput += " " + record.get(i);
                }

                output.add(addToOutput);
            }
        } catch (SQLException e) {}

        return (output.toArray());
    }

    protected static JSpinner createJSpinner(int minValue, int maxValue, int columns) {
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

    public JPanel getPanel() {
        return mainPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}
