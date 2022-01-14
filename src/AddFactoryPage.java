import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddFactoryPage extends AddStorageLocationPage {

    private JTextField vaccinesPerMinTextField;
    private JComboBox manufacturersComboBox;

    public AddFactoryPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Factory:");
        createInputFieldsGridPanel();
        fitPanelToMainPanel(inputFieldsPanel);
    }

    private void createInputFieldsGridPanel() {
        JPanel inputFieldsGridPanel = new JPanel(new GridLayout(0, 2));

        vaccinesPerMinTextField = new JTextField();

        String[] columnNames = {"manufacturerID", "name"};
        manufacturersComboBox = new JComboBox(getColumns(columnNames, "Manufacturer"));

        inputFieldsGridPanel.add(new JLabel("-*Vaccines per minute:"));
        inputFieldsGridPanel.add(vaccinesPerMinTextField);

        inputFieldsGridPanel.add(new JLabel("Manufacturer:"));
        inputFieldsGridPanel.add(manufacturersComboBox);

        inputFieldsPanel.add(inputFieldsGridPanel);
    }

    protected void createStatements() {
        String vaccinesPerMin = vaccinesPerMinTextField.getText();
        String manufacturer = (String) manufacturersComboBox.getSelectedItem();

        int manufacturerID = Integer.parseInt(manufacturer.split(":")[0]);

        values = storageLocationID + ", " + manufacturerID + ", " + vaccinesPerMin;
        statements.add("INSERT INTO Factory (storageLocationID, manufacturerID, vaccinesPerMin) VALUES (" + values + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            if (!capacityConditionsMet()) {
                emptyCapacityMessage();
            }
            else if ((checkCoordinates()) && (fieldConditionsMet())) {
                super.createStatements();
                createStatements();
                super.actionPerformed(e);
            }
        }
        else {
            super.actionPerformed(e);
        }
    }
}
