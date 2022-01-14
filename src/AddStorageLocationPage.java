import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class AddStorageLocationPage extends AddLocationPage {

    private JComboBox numStoreComboBox;
    private JButton storeButton;
    private ArrayList<AddStore> addStores;
    protected int storageLocationID;
    private ArrayList<String> capacities;

    public AddStorageLocationPage(VaccineSystem vaccineSystem, MainPage mainPage, String title) {
        super(vaccineSystem, mainPage, title);
        createStorePanel();
        addStores = new ArrayList<>();
    }

    private void createStorePanel() {
        JPanel storePanel = new JPanel();

        storeButton = new JButton("Add Capacities");

        final int MAX_NUM_STORAGE_TEMPS = 10;
        String[] numStorageTemps = new String[MAX_NUM_STORAGE_TEMPS];
        for (int i = 0; i < MAX_NUM_STORAGE_TEMPS; i++) {
            numStorageTemps[i] = Integer.toString(i + 1);
        }
        numStoreComboBox = new JComboBox(numStorageTemps);

        storePanel.add(new JLabel("How many different storage temperatures:"));
        storePanel.add(numStoreComboBox);
        addButton(storeButton, storePanel);

        inputFieldsPanel.add(storePanel);
    }

    protected void createStatements() {
        super.createStatements();

        if (checkCoordinates()) {
            try {
                String statement = "INSERT INTO StorageLocation (locationID) VALUES (" + locationID + ");";
                vaccineSystem.executeUpdate(statement);
                String[] columnNames = new String[]{"MAX(storageLocationID)"};
                ArrayList<ArrayList<String>> resultSet = vaccineSystem.executeSelect(columnNames, "storageLocation");
                storageLocationID = Integer.parseInt(resultSet.get(0).get(0));
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }

            updateCapacities();
            for (AddStore addStore : addStores) {
                int temperature = (int) addStore.getTemperature();
                String capacity = addStore.getCapacity();

                values = storageLocationID + ", " + temperature + ", " + capacity;
                statements.add("INSERT INTO Store (storageLocationID, temperature, capacity) VALUES (" + values + ");");
            }
        }
    }

    protected void emptyCapacityMessage() {
        String message = "Capacity must be an integer greater than 0";
        String title = "Error";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    protected void updateCapacities() {
        capacities = new ArrayList<>();
        for (AddStore addStore: addStores) {
            String capacity = addStore.getCapacity();
            capacities.add(capacity);
        }
    }

    protected boolean capacityConditionsMet() {
        updateCapacities();
        if (capacities.size() < 1) {
            return false;
        }
        try {
            for (String capacity : capacities) {
                if (Integer.parseInt(capacity) < 1) {
                    return false;
                }
            }
            return true;
        }
        catch (NumberFormatException ex) {}
        return false;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == storeButton) {
            JFrame addStoreFrame = new JFrame();

            final int ADD_STORE_WIDTH = 400;
            final int ADD_STORE_HEIGHT = 500;

            AddStorePage addStorePage = new AddStorePage(this, addStoreFrame, ADD_STORE_WIDTH);
            addStoreFrame.add(addStorePage.getPanel());

            addStoreFrame.setSize(ADD_STORE_WIDTH, ADD_STORE_HEIGHT);
            addStoreFrame.setLocationRelativeTo(null); // Sets window to centre of screen
            addStoreFrame.setVisible(true);
        }
        else if (e.getSource() == submitButton) {
            if (capacityConditionsMet()) {
                super.actionPerformed(e);
            }
            else {
                emptyCapacityMessage();
            }
        }
        else {
            super.actionPerformed(e);
        }
    }

    public JComboBox getNumStoreComboBox() {
        return numStoreComboBox;
    }

    public void setAddStores(ArrayList<AddStore> addStores) {
        this.addStores = addStores;
    }
}
