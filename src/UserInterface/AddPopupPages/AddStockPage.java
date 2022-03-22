package UserInterface.AddPopupPages;

import UserInterface.AddPages.AddPage;
import Core.VaccineSystem;
import UserInterface.AddUtils.Insert;
import UserInterface.LoggedInPage;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;

import static UserInterface.AddUtils.CheckInputs.checkStockLevelsConditions;

public class AddStockPage extends AddPage {

    final private String VACCINATION_CENTRE_PREFIX = "(Vaccination Centre)    ";
    final private String DISTRIBUTION_CENTRE_PREFIX = "(Distribution Centres)    ";
    final private String FACTORY_PREFIX = "(Factory)    ";

    private JComboBox facilityComboBox, vaccineComboBox;
    private JTextField creationDateTextField;
    private JTextField expirationDateTextField;
    private JButton stockLevelButton;
    private ArrayList<HashMap<String, Object>> stockLevels;

    public AddStockPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage) {
        super(vaccineSystem, loggedInPage, "Add Stocks:");

        String[] vaccinationCentreColumnNames = {"vaccinationCentreID", "name"};
        String[] distributionCentreColumnNames = {"distributionCentreID"};
        String[] factoryColumnNames = {"factoryID"};
        String[] vaccineColumnNames = {"vaccineID", "name"};

        ArrayList<String> vaccinationCentres = getFormattedSelect(vaccinationCentreColumnNames, "VaccinationCentre");
        ArrayList<String> distributionCentres = getFormattedSelect(distributionCentreColumnNames, "DistributionCentre");
        ArrayList<String> factories = getFormattedSelect(factoryColumnNames, "Factory");

        vaccinationCentres = addToElements(vaccinationCentres, VACCINATION_CENTRE_PREFIX);
        distributionCentres = addToElements(distributionCentres, DISTRIBUTION_CENTRE_PREFIX);
        factories = addToElements(factories, FACTORY_PREFIX);

        stockLevels = new ArrayList<>();

        ArrayList<String> facilities = new ArrayList<>();
        facilities.addAll(vaccinationCentres);
        facilities.addAll(distributionCentres);
        facilities.addAll(factories);

        facilityComboBox = new JComboBox(facilities.toArray());
        vaccineComboBox = new JComboBox(getFormattedSelect(vaccineColumnNames, "Vaccine").toArray());
        creationDateTextField = new JTextField();
        expirationDateTextField = new JTextField();
        stockLevelButton = new JButton("Continue");

        addLabelledComponent(inputGridPanel, "Facility:", facilityComboBox);
        addLabelledComponent(inputGridPanel, "Vaccine:", vaccineComboBox);
        addLabelledComponent(inputGridPanel, "Creation Date (YYYY-MM-DD):", creationDateTextField);
        addLabelledComponent(inputGridPanel, "Expiration Date (YYYY-MM-DD):", expirationDateTextField);
        addLabelledComponent(inputGridPanel, "Stock Levels:", stockLevelButton);

        stockLevelButton.addActionListener(this);

        setMaxWidthMinHeight(inputPanel);
    }

    private ArrayList<String> addToElements(ArrayList<String> arrayList, String string) {
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.set(i, string + arrayList.get(i));
        }
        return arrayList;
    }

    private void createStockLevelFrame() {
        final int FRAME_WIDTH = 800;

        JFrame addStockLevelFrame = new JFrame();

        String facility = (String) facilityComboBox.getSelectedItem();

        AddStockLevelPage addStockLevelPage = new AddStockLevelPage(vaccineSystem, this, addStockLevelFrame, facility);
        addStockLevelFrame.add(addStockLevelPage.getPanel());

        createPopupFrame(addStockLevelFrame, addStockLevelPage.getPanel(), FRAME_WIDTH, 500);
    }

    public void setStockLevels(ArrayList<HashMap<String, Object>> stockLevels) {
        this.stockLevels = stockLevels;
    }

    private void createStatements() {
        String vaccine = (String) vaccineComboBox.getSelectedItem();

        String vaccineID = vaccine.split(":")[0];
        String creationDate = creationDateTextField.getText();
        String expirationDate = expirationDateTextField.getText();

        for (HashMap<String, Object> stockLevelMap : stockLevels) {
            String storeID = (String) stockLevelMap.get("storeID");
            String stockLevel = ((JTextField) stockLevelMap.get("textField")).getText();

            String[] columnNames = new String[] {"vaccineID", "storeID", "stockLevel", "creationDate", "expirationDate"};
            Object[] values = new Object[] {vaccineID, storeID, stockLevel, creationDate, expirationDate};
            inserts.add(new Insert(columnNames, values, "VaccineInStorage"));
        }
    }

    protected boolean checkInputConditions(boolean displayError) {
        if (super.checkInputConditions(displayError)) {
            if (checkStockLevelsConditions(stockLevels)) {
                return true;
            }
            else {
                errorMessage("Stock levels must be integers greater than 0", displayError);
                return false;
            }
        }
        return false;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stockLevelButton) {
            createStockLevelFrame();
        }
        else if (e.getSource() == submitButton) {
            if (checkInputConditions(true)) {
                createStatements();
                super.actionPerformed(e);
            }
        }
        else {
            super.actionPerformed(e);
        }
    }

    public String getVACCINATION_CENTRE_PREFIX() {
        return VACCINATION_CENTRE_PREFIX;
    }

    public String getDISTRIBUTION_CENTRE_PREFIX() {
        return DISTRIBUTION_CENTRE_PREFIX;
    }

    public String getFACTORY_PREFIX() {
        return FACTORY_PREFIX;
    }
}
