import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddStockLevelPage extends AddPage {

    private AddStockPage addStockPage;
    private JFrame addStockLevelFrame;
    private ArrayList<HashMap<String, Object>> stockLevels;
    private String facility;

    public AddStockLevelPage(VaccineSystem vaccineSystem, AddStockPage addStockPage, JFrame addStockLevelFrame, String facility) {
        this.addStockPage = addStockPage;
        this.addStockLevelFrame = addStockLevelFrame;
        this.facility = facility;
        this.vaccineSystem = vaccineSystem;

        stockLevels = new ArrayList<>();

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        createPageTitle("Stock Levels:");
        createInputFieldsPanel();

        inputGridPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(inputGridPanel);

        createInputFieldsPanel();
        createAddStockLevelsPanel();
        createSubmitButton();

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
            if (facility.startsWith(VACCINATION_CENTRE_PREFIX)) {
                String where = "vaccinationCentreID = " + prefixAndID.replace(VACCINATION_CENTRE_PREFIX, "");
                storageLocationID = vaccineSystem.executeSelect(columnNames, "vaccinationCentre", where).get(0).get(0);
            } else if (facility.startsWith(DISTRIBUTION_CENTRE_PREFIX)) {
                String where = "distributionCentreID = " + prefixAndID.replace(DISTRIBUTION_CENTRE_PREFIX, "");
                System.out.println(vaccineSystem.executeSelect(columnNames, "distributionCentre", where));
                storageLocationID = vaccineSystem.executeSelect(columnNames, "distributionCentre", where).get(0).get(0);
            } else if (facility.startsWith(FACTORY_PREFIX)) {
                String where = "factoryID = " + prefixAndID.replace(FACTORY_PREFIX, "");
                storageLocationID = vaccineSystem.executeSelect(columnNames, "factory", where).get(0).get(0);
            }
        } catch (SQLException ignored) {}

        columnNames = new String[] {"storeID", "capacity", "temperature"};
        String where = "storageLocationID = " + storageLocationID;
        try {
            ArrayList<ArrayList<String>> stores = vaccineSystem.executeSelect(columnNames, "store", where);

            for (ArrayList<String> store : stores) {
                String labelText = "-" + store.get(1) + " at " + store.get(2) + " degrees:";

                stockLevels.add(new HashMap<>());
                int lastIndex = stockLevels.size() - 1;

                stockLevels.get(lastIndex).put("storeID", store.get(0));
                stockLevels.get(lastIndex).put("textField", new JTextField());

                addLabelledComponent(inputGridPanel, labelText, (JTextField) stockLevels.get(lastIndex).get("textField"));
            }
        } catch (SQLException ignored) {}
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            addStockPage.setStockLevels(stockLevels);
            addStockLevelFrame.setVisible(false);
        }
    }
}
