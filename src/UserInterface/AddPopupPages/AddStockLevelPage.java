package UserInterface.AddPopupPages;

import Core.VaccineSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddStockLevelPage extends AddPopupPage {

    private AddStockPage addStockPage;
    private ArrayList<HashMap<String, Object>> stockLevels;
    private String facility;

    public AddStockLevelPage(VaccineSystem vaccineSystem, AddStockPage addStockPage, JFrame frame, String facility) {
        super(frame);

        this.addStockPage = addStockPage;
        this.facility = facility;
        this.vaccineSystem = vaccineSystem;

        stockLevels = new ArrayList<>();

        inputGridPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(inputGridPanel);

        createAddStockLevelsPanel();

        setMaxWidthMinHeight(inputGridPanel);
    }

    private void createAddStockLevelsPanel() {
        final String VACCINATION_CENTRE_PREFIX = addStockPage.getVACCINATION_CENTRE_PREFIX();
        final String DISTRIBUTION_CENTRE_PREFIX = addStockPage.getDISTRIBUTION_CENTRE_PREFIX();
        final String FACTORY_PREFIX = addStockPage.getFACTORY_PREFIX();

        String prefixAndID = facility.split(":")[0];
        String storageLocationID = "";
        String[] columnNames = new String[] {"storageLocationID"};

        try {
            HashMap<String, HashMap<String, Object>> facilities = new HashMap<>();
            if (facility.startsWith(VACCINATION_CENTRE_PREFIX)) {
                String where = "vaccinationCentreID = " + prefixAndID.replace(VACCINATION_CENTRE_PREFIX, "");
                facilities = vaccineSystem.select(columnNames, "vaccinationCentre", where);
            }
            else if (facility.startsWith(DISTRIBUTION_CENTRE_PREFIX)) {
                String where = "distributionCentreID = " + prefixAndID.replace(DISTRIBUTION_CENTRE_PREFIX, "");
                facilities = vaccineSystem.select(columnNames, "distributionCentre", where);
            }
            else if (facility.startsWith(FACTORY_PREFIX)) {
                String where = "factoryID = " + prefixAndID.replace(FACTORY_PREFIX, "");
                facilities = vaccineSystem.select(columnNames, "factory", where);
            }
            String key = facilities.keySet().iterator().next();
            storageLocationID = (String) facilities.get(key).get("storageLocationID");
        }
        catch (SQLException ignored) {}

        columnNames = new String[] {"storeID", "capacity", "temperature"};
        String where = "storageLocationID = " + storageLocationID;
        try {
            HashMap<String, HashMap<String, Object>> stores = vaccineSystem.select(columnNames, "store", where);

            for (String key : stores.keySet()) {
                HashMap<String, Object> store = stores.get(key);
                String labelText = "-" + store.get("capacity") + " at " + store.get("temperature") + " degrees:";

                stockLevels.add(new HashMap<>());
                int lastIndex = stockLevels.size() - 1;

                stockLevels.get(lastIndex).put("storeID", store.get("storeID"));
                stockLevels.get(lastIndex).put("textField", new JTextField());

                addLabelledComponent(inputGridPanel, labelText, (JTextField) stockLevels.get(lastIndex).get("textField"));
            }
        } catch (SQLException ignored) {}
    }
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getSource() == submitButton) {
            addStockPage.setStockLevels(stockLevels);
            frame.setVisible(false);
        }
    }
}
