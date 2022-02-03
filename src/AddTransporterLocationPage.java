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
        setMaxWidthMinHeight(inputPanel);
    }

    private void createInputFieldsGridPanel() {
        JPanel inputFieldsGridPanel = new JPanel(new GridLayout(0, 2));

        totalCapacityTextField = new JTextField();

        String[] columnNames = {"transporterID", "name"};
        transportersComboBox = new JComboBox(getFormattedSelect(columnNames, "Transporter").toArray());

        inputFieldsGridPanel.add(new JLabel("-*Total capacity:"));
        inputFieldsGridPanel.add(totalCapacityTextField);

        inputFieldsGridPanel.add(new JLabel("Transporter:"));
        inputFieldsGridPanel.add(transportersComboBox);

        inputPanel.add(inputFieldsGridPanel);
    }

    protected void createStatements() {
        super.createStatements();

        totalCapacity = totalCapacityTextField.getText();
        String transporter = (String) transportersComboBox.getSelectedItem();

        int transporterID = Integer.parseInt(transporter.split(":")[0]);

        values = transporterID + ", " + locationID + ", " + totalCapacity + ", " + totalCapacity;
        statements.add("INSERT INTO TransporterLocation (transporterID, locationID, totalCapacity, availableCapacity) VALUES (" + values + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
