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

        String[] columnNames = {"transporterID", "name"};
        transportersComboBox = new JComboBox(getFormattedSelect(columnNames, "Transporter").toArray());

        addLabelledComponent(inputGridPanel, "#*Number of vans:", numberOfVansTextField);
        addLabelledComponent(inputGridPanel, "#*Van capacity:", vanCapacityTextField);
        addLabelledComponent(inputGridPanel, "#*Transporter:", transportersComboBox);

        inputPanel.add(inputFieldsGridPanel);
    }

    protected void createStatements() {
        super.createStatements();

        int numberOfVans = Integer.parseInt(numberOfVansTextField.getText());
        String vanCapacity = vanCapacityTextField.getText();
        String transporter = (String) transportersComboBox.getSelectedItem();

        int transporterID = Integer.parseInt(transporter.split(":")[0]);

        String[] columnNames = new String[] {"transporterID", "locationID"};
        Object[] values = new Object[] {transporterID, locationID};
        String transporterLocationID = insertAndGetID(columnNames, values, "TransporterLocation", "transporterLocationID");

        String longitude = "";
        String latitude = "";
        try {
            columnNames = new String[] {"locationID", "longitude", "latitude"};
            String where = "locationID = " + locationID;
            HashMap<String, HashMap<String, Object>> locations = vaccineSystem.executeSelect(columnNames, "Location", where);
            HashMap<String, Object> location = locations.get(String.valueOf(locationID));
            longitude = (String) location.get("longitude");
            latitude = (String) location.get("latitude");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < numberOfVans; i++) {
            columnNames = new String[] {"longitude", "latitude"};
            values = new Object[] {longitude, latitude};
            locationID = insertAndGetID(columnNames, values, "Location", "locationID");

            columnNames = new String[] {"locationID"};
            values = new Object[] {locationID};
            String storageLocationID = insertAndGetID(columnNames, values, "StorageLocation", "storageLocationID");

            columnNames = new String[] {"storageLocationID", "temperature", "capacity"};
            values = new Object[]  {storageLocationID, 0, vanCapacity};
            inserts.add(new Insert(columnNames, values, "Store"));

            columnNames = new String[] {"deliveryStage", "remainingTime", "storageLocationID", "originID", "destinationID", "transporterLocationID"};
            values = new Object[] {"waiting", 0, storageLocationID, null, null, transporterLocationID};
            inserts.add(new Insert(columnNames, values, "Van"));
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            if (checkInputConditions(false)) {
                createStatements();
            }
        }
        super.actionPerformed(e);
    }
}
