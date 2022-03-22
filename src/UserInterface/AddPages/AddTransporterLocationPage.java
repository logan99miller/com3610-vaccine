/**
 * Page used to insert a transporter location into the system's database
 */
package UserInterface.AddPages;

import Core.VaccineSystem;
import UserInterface.AddUtils.Insert;
import UserInterface.LoggedInPage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.HashMap;

public class AddTransporterLocationPage extends AddLocationPage {

    private JTextField numberOfVansTextField;
    private JTextField vanCapacityTextField;
    private JComboBox transportersComboBox;

    public AddTransporterLocationPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage) {
        super(vaccineSystem, loggedInPage, "Add Transporter Location:");
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

    /**
     * Creates the SQL statements required and adds the transporter location and associated vans to the inserts list
     */
    protected void createStatements() {
        super.createStatements();

        int numberOfVans = Integer.parseInt(numberOfVansTextField.getText());

        String vanCapacity = vanCapacityTextField.getText();
        String transporter = (String) transportersComboBox.getSelectedItem();

        // Get the ID from the formatted drop-down selection box
        int transporterID = Integer.parseInt(transporter.split(":")[0]);

        String[] columnNames = new String[] {"transporterID", "locationID"};
        Object[] values = new Object[] {transporterID, locationID};
        String transporterLocationID = insertAndGetID(columnNames, values, "TransporterLocation", "transporterLocationID");

        createVansStatements(numberOfVans, vanCapacity, transporterLocationID);
    }

    /**
     * Adds the associated vans to the inserts list to be created when the SQL statements are performed
     * @param numberOfVans how many vans to create
     * @param vanCapacity how many vaccines each van can store
     * @param transporterLocationID the transporter location the van is associated with
     */
    private void createVansStatements(int numberOfVans, String vanCapacity, String transporterLocationID) {
        String longitude = "";
        String latitude = "";

        // Get the longitude and latitude of the transporter location (also used for the van's coordinates)
        try {
            String[] columnNames = new String[] {"locationID", "longitude", "latitude"};
            String where = "locationID = " + locationID;
            HashMap<String, HashMap<String, Object>> locations = vaccineSystem.select(columnNames, "Location", where);

            HashMap<String, Object> location = locations.get(String.valueOf(locationID));
            longitude = (String) location.get("longitude");
            latitude = (String) location.get("latitude");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < numberOfVans; i++) {
            createVanStatements(longitude, latitude, vanCapacity, transporterLocationID);
        }
    }

    /**
     * Adds a van to the inserts list to be created when the SQL statements are performed
     * @param longitude
     * @param latitude
     * @param vanCapacity
     * @param transporterLocationID
     */
    private void createVanStatements(String longitude, String latitude, String vanCapacity, String transporterLocationID) {
        String[] columnNames = new String[]{"longitude", "latitude"};
        Object[] values = new Object[]{longitude, latitude};
        locationID = insertAndGetID(columnNames, values, "Location", "locationID");

        columnNames = new String[]{"locationID"};
        values = new Object[]{locationID};
        String storageLocationID = insertAndGetID(columnNames, values, "StorageLocation", "storageLocationID");

        columnNames = new String[]{"storageLocationID", "temperature", "capacity"};
        values = new Object[]{storageLocationID, 0, vanCapacity};
        inserts.add(new Insert(columnNames, values, "Store"));

        columnNames = new String[]{"deliveryStage", "totalTime", "remainingTime", "storageLocationID", "originID", "destinationID", "transporterLocationID"};
        values = new Object[]{"waiting", 0, 0, storageLocationID, null, null, transporterLocationID};
        inserts.add(new Insert(columnNames, values, "Van"));
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
