package UserInterface.AddPages;

import Core.VaccineSystem;
import UserInterface.MainPage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.HashMap;

public class AddTransporterLocationPage extends AddLocationPage {

    private JTextField numberOfVansTextField;
    private JTextField vanCapacityTextField;
    private JTextField vanTemperatureTextField;
    private JComboBox transportersComboBox;

    public AddTransporterLocationPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Transporter Location:");
        createInputFieldsGridPanel();
        setMaxWidthMinHeight(inputPanel);
    }

    private void createInputFieldsGridPanel() {
        JPanel inputFieldsGridPanel = new JPanel(new GridLayout(0, 2));

        numberOfVansTextField = new JTextField();
        vanCapacityTextField = new JTextField();
        vanTemperatureTextField = new JTextField();

        String[] columnNames = {"transporterID", "name"};
        transportersComboBox = new JComboBox(getFormattedSelect(columnNames, "Transporter").toArray());

        inputFieldsGridPanel.add(new JLabel("-*Number of vans:"));
        inputFieldsGridPanel.add(numberOfVansTextField);

        inputFieldsGridPanel.add(new JLabel("-*Van capacity:"));
        inputFieldsGridPanel.add(vanCapacityTextField);

        inputFieldsGridPanel.add(new JLabel("-*Van Temperature (degrees C):"));
        inputFieldsGridPanel.add(vanTemperatureTextField);

        inputFieldsGridPanel.add(new JLabel("Transporter:"));
        inputFieldsGridPanel.add(transportersComboBox);

        inputPanel.add(inputFieldsGridPanel);
    }

    protected void createStatements() {
        super.createStatements();

        int numberOfVans = Integer.parseInt(numberOfVansTextField.getText());
        String vanCapacity = vanCapacityTextField.getText();
        String vanTemperature = vanTemperatureTextField.getText();
        String transporter = (String) transportersComboBox.getSelectedItem();

        int transporterID = Integer.parseInt(transporter.split(":")[0]);

        String transporterLocationValues = transporterID + ", " + locationID;
        String statement = "INSERT INTO TransporterLocation (transporterID, locationID) VALUES (" + transporterLocationValues + ");";
        int transporterLocationID = insertAndGetID(statement, "transporterLocationID", "TransporterLocation");

        String longitude = "";
        String latitude = "";
        try {
            String[] columnNames = {"locationID", "longitude", "latitude"};
            String where = "locationID = " + locationID;
            HashMap<String, HashMap<String, Object>> locations = vaccineSystem.executeSelect(columnNames, "Location", where);
            HashMap<String, Object> location = locations.get(String.valueOf(locationID));
            longitude = (String) location.get("longitude");
            latitude = (String) location.get("longitude");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < numberOfVans; i++) {
            String locationValues = longitude + ", " + latitude;
            statement = "INSERT INTO Location (longitude, latitude) VALUES (" + locationValues + ");";
            locationID = insertAndGetID(statement, "locationID", "Location");

            statement = "INSERT INTO StorageLocation (locationID) VALUES (" + locationID + ");";
            int storageLocationID = insertAndGetID(statement, "storageLocationID", "StorageLocation");

            String storeValues = storageLocationID + ", " + vanTemperature + ", " + vanCapacity;
            statements.add("INSERT INTO Store (storageLocationID, temperature, capacity) VALUES (" + storeValues + ");");

            String vanValues = "'waiting', 0, " + storageLocationID + ", null, null, " + transporterLocationID;
            statements.add("INSERT INTO Van (deliveryStage, remainingTime, storageLocationID, originID, destinationID, transporterLocationID) VALUES (" + vanValues+ ");");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
