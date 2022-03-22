/**
 * Page used to insert a vaccine into the system's database
 */
package UserInterface.AddPages;

import Core.VaccineSystem;
import UserInterface.AddUtils.AddVaccineLifespan;
import UserInterface.AddPopupPages.AddVaccineLifespanPage;
import UserInterface.AddUtils.Insert;
import UserInterface.LoggedInPage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import static UserInterface.AddUtils.CheckInputs.*;

public class AddVaccinePage extends AddPage {

    private JTextField nameTextField;
    private JSpinner dosesNeededSpinner;
    private JTextField daysBetweenDosesTextField;
    private JTextField minimumAgeTextField;
    private JTextField maximumAgeTextField;
    private JButton lifespanButton;
    private JComboBox numLifespanComboBox;
    private JList<String> medicalConditionsList;
    private ArrayList<AddVaccineLifespan> addLifespans;

    public AddVaccinePage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage) {
        super(vaccineSystem, loggedInPage, "Add Vaccine:");

        JPanel lifespanPanel = new JPanel(new GridLayout(0, 2));
        JPanel exemptionsPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(lifespanPanel);
        inputPanel.add(exemptionsPanel);

        ArrayList<String> medicalConditions = getFormattedSelect(new String[] {"medicalConditionID", "name"}, "MedicalCondition");
        ListModel medicalConditionsListModel = ArrayListToListModel(medicalConditions);

        nameTextField = new JTextField();
        dosesNeededSpinner = createJSpinner(1, 100, 3);
        daysBetweenDosesTextField = new JTextField();
        minimumAgeTextField = new JTextField();
        maximumAgeTextField = new JTextField();
        medicalConditionsList = new JList(medicalConditionsListModel);

        addLabelledComponent(inputGridPanel, "Name:", nameTextField);
        addLabelledComponent(inputGridPanel, "Doses Needed:", dosesNeededSpinner);
        addLabelledComponent(inputGridPanel, "#*Days Between Doses:", daysBetweenDosesTextField);
        addLabelledComponent(inputGridPanel, "#*Minimum age:", minimumAgeTextField);
        addLabelledComponent(inputGridPanel, "#*Maximum age:", maximumAgeTextField);
        addLabelledComponent(lifespanPanel, "Number of lifespan temperature variations:", createLifespanPanel(10));
        addLabelledComponent(exemptionsPanel, "Medical Conditions:", medicalConditionsList);

        setMaxWidthMinHeight(inputPanel);

        addLifespans = new ArrayList<>();
    }

    /**
     *
     * @param maxRange
     * @return
     */
    private JPanel createLifespanPanel(int maxRange) {
        JPanel lifespanPanel = new JPanel();

        String[] numLifespans = new String[maxRange];
        for (int i = 0; i < maxRange; i++) {
            numLifespans[i] = Integer.toString(i + 1);
        }

        numLifespanComboBox = new JComboBox(numLifespans);
        lifespanButton = new JButton("Add Lifespans");

        lifespanPanel.add(numLifespanComboBox);
        addButton(lifespanButton, lifespanPanel);

        return lifespanPanel;
    }

    /**
     * Creates the SQL statements required and adds them to the inserts list
     */
    private void createStatements() {
        inserts = new ArrayList<>();

        String name = nameTextField.getText();
        int dosesNeeded = (int) dosesNeededSpinner.getValue();
        String daysBetweenDoses = daysBetweenDosesTextField.getText();
        String minimumAge = minimumAgeTextField.getText();
        String maximumAge = maximumAgeTextField.getText();

        String[] columnNames = new String[] {"name", "dosesNeeded", "daysBetweenDoses", "minimumAge", "maximumAge"};
        Object[] values = new Object[] {name, dosesNeeded, daysBetweenDoses, minimumAge, maximumAge};
        String vaccineID = insertAndGetID(columnNames, values, "Vaccine", "vaccineID");

        createExemptionStatements(vaccineID);
        createLifespanStatements(vaccineID);
    }

    /**
     * Adds the associated vaccine exemptions to the insert list
     */
    private void createExemptionStatements(String vaccineID) {
        for (String medicalCondition : medicalConditionsList.getSelectedValuesList()) {
            int medicalConditionID = Integer.parseInt( medicalCondition.split(":")[0]);

            String[] columnNames = new String[] {"vaccineID", "medicalConditionID"};
            Object[] values = new Object[] {vaccineID, medicalConditionID};
            inserts.add(new Insert(columnNames, values, "VaccineExemption"));
        }
    }

    /**
     * Adds the associated vaccine lifespans to the insert list
     */
    private void createLifespanStatements(String vaccineID) {
        for (AddVaccineLifespan addLifespan : addLifespans) {
            String lifespan = addLifespan.getLifespan();
            int lowestTemperature = (int) addLifespan.getMinimumTemperature();
            int highestTemperature = (int) addLifespan.getMaximumTemperature();

            String[] columnNames = new String[] {"vaccineID", "lifespan", "lowestTemperature", "highestTemperature"};
            Object[] values = new Object[] {vaccineID, lifespan, lowestTemperature, highestTemperature};
            inserts.add(new Insert(columnNames, values, "VaccineLifespan"));
        }
    }

    private void createLifespanFrame() {
        final int FRAME_WIDTH = 800;

        JFrame addLifespanFrame = new JFrame();
        addLifespanFrame.setResizable(false);

        AddVaccineLifespanPage addLifespanPage = new AddVaccineLifespanPage(this, addLifespanFrame, FRAME_WIDTH);
        addLifespanFrame.add(addLifespanPage.getPanel());

        createPopupFrame(addLifespanFrame, addLifespanPage.getPanel(), FRAME_WIDTH, 500);
    }

    /**
     * Checks the user's input against criteria in AddPage as well as checking conditions specific to adding vaccines
     * @param displayError if an error message should be displayed to the user, setting it to false can prevent multiple
     *                     error messages being displayed if the input conditions are checked several times (e.g. if they
     *                     have to be checked while the user still has more data to input)
     * @return
     */
    protected boolean checkInputConditions(boolean displayError) {
        if (super.checkInputConditions(displayError)) {
            if (!checkLifespanConditions(addLifespans)) {
                errorMessage("Lifespans must be an integer greater than 0", displayError);
                return false;
            }
            else if (!checkTemperatureConditions(addLifespans)) {
                errorMessage("Minimum temperature cannot be greater than maximum temperature", displayError);
                return false;
            }
            else if (!checkAgeConditions(minimumAgeTextField, maximumAgeTextField)) {
                errorMessage("Minimum age cannot be greater than maximum age", displayError);
                return false;
            }
            return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == lifespanButton) {
            createLifespanFrame();
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

    public JComboBox getNumLifespanComboBox() {
        return numLifespanComboBox;
    }

    public void setAddLifespans(ArrayList<AddVaccineLifespan> addLifespans) {
        this.addLifespans = addLifespans;
    }
}
