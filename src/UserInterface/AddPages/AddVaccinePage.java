package UserInterface.AddPages;

import UserInterface.AddPage;
import Core.VaccineSystem;
import UserInterface.MainPage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

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

    public AddVaccinePage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Vaccine:");

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
        addLabelledComponent(inputGridPanel, "-Days Between Doses:", daysBetweenDosesTextField);
        addLabelledComponent(inputGridPanel, "-*Minimum age:", minimumAgeTextField);
        addLabelledComponent(inputGridPanel, "-*Maximum age:", maximumAgeTextField);
        addLabelledComponent(lifespanPanel, "Number of lifespan temperature variations:", createLifespanPanel(10));
        addLabelledComponent(exemptionsPanel, "Medical Conditions:", medicalConditionsList);

        setMaxWidthMinHeight(inputPanel);

        addLifespans = new ArrayList<>();
    }


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

    private boolean checkLifespanConditions() {
        try {
            for (AddVaccineLifespan addLifespan : addLifespans) {
                int lifespan = Integer.parseInt(addLifespan.getLifespan());
                if (lifespan < 1) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean checkTemperatureConditions() {
        for (AddVaccineLifespan addLifespan : addLifespans) {
            int minimumTemperature = (int) addLifespan.getMinimumTemperature();
            int maximumTemperature = (int) addLifespan.getMaximumTemperature();
            if (minimumTemperature > maximumTemperature) {
                return false;
            }
        }
        return true;
    }

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

        for (String medicalCondition : medicalConditionsList.getSelectedValuesList()) {
            int medicalConditionID = Integer.parseInt( medicalCondition.split(":")[0]);

            columnNames = new String[] {"vaccineID", "medicalConditionID"};
            values = new Object[] {vaccineID, medicalConditionID};
            inserts.add(new Insert(columnNames, values, "VaccineExemption"));
        }

        for (AddVaccineLifespan addLifespan : addLifespans) {
            String lifespan = addLifespan.getLifespan();
            int lowestTemperature = (int) addLifespan.getMinimumTemperature();
            int highestTemperature = (int) addLifespan.getMaximumTemperature();

            columnNames = new String[] {"vaccineID", "lifespan", "lowestTemperature", "highestTemperature"};
            values = new Object[] {vaccineID, lifespan, lowestTemperature, highestTemperature};
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

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == lifespanButton) {
            createLifespanFrame();
        }
        else if (e.getSource() == submitButton) {
            if (!checkLifespanConditions()) {
                errorMessage("Lifespans must be an integer greater than 0");
            }
            else if (!checkTemperatureConditions()) {
                errorMessage("Minimum temperature cannot be greater than maximum temperature");
            }
            else {
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
