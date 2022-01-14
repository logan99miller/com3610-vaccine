import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class AddVaccinePage extends AddPage {

    private JTextField nameTextField;
    private JSpinner dosesNeededSpinner;
    private JButton lifespanButton;
    private JComboBox numLifespanComboBox;
    private JList<String> medicalConditionsList;
    private Object[] medicalConditions;
    private ArrayList<AddVaccineLifespan> addLifespans;
    private ArrayList<String> lifespans;


    public AddVaccinePage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Vaccine:");

        createInputFieldsGridPanel();
        createExemptionsPanel();
        createLifespanPanel();
        fitPanelToMainPanel(inputFieldsPanel);

        addLifespans = new ArrayList<>();
    }

    private void createInputFieldsGridPanel() {
        JPanel inputFieldsGridPanel = new JPanel(new GridLayout(0, 2));

        nameTextField = new JTextField();
        dosesNeededSpinner = createJSpinner(1, 100, 3);

        inputFieldsGridPanel.add(new JLabel("Name:"));
        inputFieldsGridPanel.add(nameTextField);
        inputFieldsGridPanel.add(new JLabel("*Doses Needed:"));
        inputFieldsGridPanel.add(dosesNeededSpinner);

        inputFieldsPanel.add(inputFieldsGridPanel);
    }

    private void createLifespanPanel() {
        JPanel lifespanPanel = new JPanel();

        lifespanButton = new JButton("Add Lifespans");

        final int MAX_NUM_LIFESPANS = 10;
        String[] numLifespans = new String[MAX_NUM_LIFESPANS];
        for (int i = 0; i < MAX_NUM_LIFESPANS; i++) {
            numLifespans[i] = Integer.toString(i + 1);
        }
        numLifespanComboBox = new JComboBox(numLifespans);

        lifespanPanel.add(new JLabel("How many different temperature ranges for lifespan:"));
        lifespanPanel.add(numLifespanComboBox);
        addButton(lifespanButton, lifespanPanel);

        inputFieldsPanel.add(lifespanPanel);
    }


    private void createExemptionsPanel() {
        JPanel exemptionsPanel = new JPanel(new GridLayout(0, 2));

        String[] columnNames = {"medicalConditionID", "name"};
        DefaultListModel<String> listModel = new DefaultListModel<>();
        medicalConditions = getColumns(columnNames, "MedicalCondition");
        for (Object medicalCondition : medicalConditions) {
            listModel.addElement((String) medicalCondition);
        }
        medicalConditionsList = new JList<>(listModel);

        exemptionsPanel.add(new JLabel("Medical Exemptions:"));
        exemptionsPanel.add(medicalConditionsList);

        inputFieldsPanel.add(exemptionsPanel);
    }

    protected void updateLifespans() {
        lifespans = new ArrayList<>();
        for (AddVaccineLifespan addLifespan: addLifespans) {
            String capacity = addLifespan.getLifespan();
            lifespans.add(capacity);
        }
    }

    protected void emptyLifespanMessage() {
        String message = "Lifespan must be an integer greater than 0";
        String title = "Error";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    protected boolean lifespansConditionsMet() {
        updateLifespans();
        if (lifespans.size() < 1) {
            return false;
        }
        try {
            for (String lifespan : lifespans) {
                if (Integer.parseInt(lifespan) < 1) {
                    return false;
                }
            }
            return true;
        }
        catch (NumberFormatException ex) {}
        return false;
    }


    private void createStatements() {

        try {
            values = "\"" + nameTextField.getText() + "\", " + dosesNeededSpinner.getValue();
            String statement = "INSERT INTO Vaccine (name, dosesNeeded) VALUES (" + values + ");";
            vaccineSystem.executeUpdate(statement);
            String[] columnNames = new String[]{"MAX(vaccineID)"};
            ArrayList<ArrayList<String>> resultSet = vaccineSystem.executeSelect(columnNames, "Vaccine");
            int vaccineID = Integer.parseInt(resultSet.get(0).get(0));

            for (String medicalCondition : medicalConditionsList.getSelectedValuesList()) {
                int medicalConditionID = Integer.parseInt( medicalCondition.split(":")[0]);
                values = vaccineID + "," + medicalConditionID;
                statements.add("INSERT INTO VaccineExemption (vaccineID, medicalConditionID) VALUES (" + values + ");");
            }

            for (AddVaccineLifespan addLifespan : addLifespans) {
                String lifespan = addLifespan.getLifespan();
                int lowestTemperature = (int) addLifespan.getMinimumTemperature();
                int highestTemperature = (int) addLifespan.getMaximumTemperature();

                values = vaccineID + ", " + lifespan + ", " + lowestTemperature + ", " + highestTemperature;
                statements.add("INSERT INTO VaccineLifespan (vaccineID, lifespan, lowestTemperature, highestTemperature) VALUES (" + values + ");");
            }
        }
        catch (SQLException ex) {}
        catch (NumberFormatException ex) {}
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == lifespanButton) {
            JFrame addLifespanFrame = new JFrame();

            final int ADD_LIFESPAN_WIDTH = 800;
            final int ADD_LIFESPAN_HEIGHT = 500;

            AddVaccineLifespanPage addLifespanPage = new AddVaccineLifespanPage(this, addLifespanFrame, ADD_LIFESPAN_WIDTH);
            addLifespanFrame.add(addLifespanPage.getPanel());

            addLifespanFrame.setSize(ADD_LIFESPAN_WIDTH, ADD_LIFESPAN_HEIGHT);
            addLifespanFrame.setLocationRelativeTo(null); // Sets window to centre of screen
            addLifespanFrame.setVisible(true);
        }
        else if (e.getSource() == submitButton) {
            if (lifespansConditionsMet()) {
                if (fieldConditionsMet()) {
                    createStatements();
                    super.actionPerformed(e);
                }
            }
            else {
                emptyLifespanMessage();
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
