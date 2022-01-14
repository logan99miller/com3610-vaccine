import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddTransporterLocationPage extends AddLocationPage {

    private JTextField totalCapacityTextField;
    private JComboBox transportersComboBox;
    private String totalCapacity;

    public AddTransporterLocationPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Transporter Location:");
        createInputFieldsGridPanel();
        fitPanelToMainPanel(inputFieldsPanel);
    }

    private void createInputFieldsGridPanel() {
        JPanel inputFieldsGridPanel = new JPanel(new GridLayout(0, 2));

        totalCapacityTextField = new JTextField();

        String[] columnNames = {"transporterID", "name"};
        transportersComboBox = new JComboBox(getColumns(columnNames, "Transporter"));

        inputFieldsGridPanel.add(new JLabel("-*Total capacity:"));
        inputFieldsGridPanel.add(totalCapacityTextField);

        inputFieldsGridPanel.add(new JLabel("Transporter:"));
        inputFieldsGridPanel.add(transportersComboBox);

        inputFieldsPanel.add(inputFieldsGridPanel);
    }

    protected void createStatements() {
        totalCapacity = totalCapacityTextField.getText();
        String transporter = (String) transportersComboBox.getSelectedItem();

        int transporterID = Integer.parseInt(transporter.split(":")[0]);

        values = transporterID + ", " + locationID + ", " + totalCapacity + ", " + totalCapacity;
        statements.add("INSERT INTO TransporterLocation (transporterID, locationID, totalCapacity, availableCapacity) VALUES (" + values + ");");
    }

    protected void emptyCapacityMessage() {
        String message = "Capacity must be an integer greater than 0";
        String title = "Error";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            if (checkCoordinates()) {
                super.createStatements();
                createStatements();
            }
            try {
                if (Integer.parseInt(totalCapacity) > 0) {
                    super.actionPerformed(e);
                } else {
                    emptyCapacityMessage();
                }
            } catch (NumberFormatException ex) {
                emptyCapacityMessage();
            }
        }
        else {
            super.actionPerformed(e);
        }
    }
}
