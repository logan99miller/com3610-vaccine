/**
 * Parent class for any page used to insert a storage location into the system's database
 */
package UserInterface.AddPages;

import Core.VaccineSystem;
import UserInterface.*;
import UserInterface.AddUtils.AddStore;
import UserInterface.AddPopupPages.AddStorePage;
import UserInterface.AddUtils.Insert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import static UserInterface.AddUtils.CheckInputs.checkCapacitiesCondition;

public class AddStorageLocationPage extends AddLocationPage {

    private JComboBox numStoreComboBox;
    private JButton storeButton;
    private ArrayList<AddStore> addStores;
    protected String storageLocationID;

    public AddStorageLocationPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage, String title) {
        super(vaccineSystem, loggedInPage, title);

        JPanel temperatureVariationsPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(temperatureVariationsPanel);

        JPanel storePanel = createStorePanel(10);
        addLabelledComponent(temperatureVariationsPanel, "Number of storage temperature variations:", storePanel);

        addStores = new ArrayList<>();
    }

    private JPanel createStorePanel(int maxRange) {
        JPanel storePanel = new JPanel();

        String[] numLifespans = new String[maxRange];
        for (int i = 0; i < maxRange; i++) {
            numLifespans[i] = Integer.toString(i + 1);
        }

        numStoreComboBox = new JComboBox(numLifespans);
        storeButton = new JButton("Add Capacities");

        storePanel.add(numStoreComboBox);
        addButton(storeButton, storePanel);

        return storePanel;
    }

    /**
     * Creates the SQL statements required and adds them to the inserts list
     */
    protected void createStatements() {
        super.createStatements();

        String[] columnNames = new String[] {"locationID"};
        Object[] values = new Object[] {locationID};
        storageLocationID = insertAndGetID(columnNames, values, "StorageLocation", "storageLocationID");

        for (AddStore addStore : addStores) {
            int temperature = (int) addStore.getTemperature();
            String capacity = addStore.getCapacity();

            columnNames = new String[] {"storageLocationID", "temperature", "capacity"};
            values = new Object[] {storageLocationID, temperature, capacity};
            inserts.add(new Insert(columnNames, values, "Store"));
        }
    }

    /**
     * Checks the user's input against criteria in AddPage and AddLocationPage as well as checking that capacities given for
     * each store is an integer greater than 0
     * @param displayError if an error message should be displayed to the user, setting it to false can prevent multiple
     *                     error messages being displayed if the input conditions are checked several times (e.g. if they
     *                     have to be checked while the user still has more data to input)
     * @return
     */
    protected boolean checkInputConditions(boolean displayError) {
        if (super.checkInputConditions(displayError)) {
            if (checkCapacitiesCondition(addStores)) {
                return true;
            }
            else {
                if (displayError) {
                    errorMessage("Capacities must be an integer greater than 0", displayError);
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Creates a pop-up store frame, as the number of stores is unknown until the user informs us so the input field required
     * cannot be generated in the original page
     */
    private void createStoreFrame() {
        final int FRAME_WIDTH = 400;

        JFrame addStoreFrame = new JFrame();
        addStoreFrame.setResizable(false);

        AddStorePage addStorePage = new AddStorePage(this, addStoreFrame, FRAME_WIDTH);
        addStoreFrame.add(addStorePage.getPanel());

        createPopupFrame(addStoreFrame, addStorePage.getPanel(), FRAME_WIDTH, 500);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == storeButton) {
            createStoreFrame();
        }
        else if (e.getSource() == submitButton) {
            if (checkInputConditions(true)) {
                super.actionPerformed(e);
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
