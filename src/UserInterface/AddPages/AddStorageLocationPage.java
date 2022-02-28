package UserInterface.AddPages;

import Core.VaccineSystem;
import UserInterface.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddStorageLocationPage extends AddLocationPage {

    private JComboBox numStoreComboBox;
    private JButton storeButton;
    private ArrayList<AddStore> addStores;
    protected int storageLocationID;

    public AddStorageLocationPage(VaccineSystem vaccineSystem, MainPage mainPage, String title) {
        super(vaccineSystem, mainPage, title);

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

    private boolean checkCapacitiesCondition() {
        try {
            for (AddStore addStore : addStores) {
                int capacity = Integer.parseInt(addStore.getCapacity());
                if (capacity < 1) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


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
            if (checkCapacitiesCondition()) {
                super.actionPerformed(e);
            }
            else {
                errorMessage("Capacities must be an integer greater than 0");
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
