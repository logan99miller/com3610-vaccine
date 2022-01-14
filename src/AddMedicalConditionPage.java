import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class AddMedicalConditionPage extends AddPage {

    private JTextField nameTextField;
    private JSpinner vulnerabilityLevelSpinner;
    private JList<String> vaccinesList;
    private Object[] vaccines;

    public AddMedicalConditionPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Medical Condition:");

        createInputFieldsGridPanel();
        createExemptionsPanel();
        fitPanelToMainPanel(inputFieldsPanel);
    }

    private void createInputFieldsGridPanel() {
        JPanel inputFieldsGridPanel = new JPanel(new GridLayout(0, 2));

        nameTextField = new JTextField();
        vulnerabilityLevelSpinner = createJSpinner(1, 100, 3);

        inputFieldsGridPanel.add(new JLabel("Name:"));
        inputFieldsGridPanel.add(nameTextField);
        inputFieldsGridPanel.add(new JLabel("*Vulnerability Level:"));
        inputFieldsGridPanel.add(vulnerabilityLevelSpinner);

        inputFieldsPanel.add(inputFieldsGridPanel);
    }

    private void createExemptionsPanel() {
        JPanel exemptionsPanel = new JPanel(new GridLayout(0, 2));

        String[] columnNames = {"vaccineID", "name"};
        DefaultListModel<String> listModel = new DefaultListModel<>();
        vaccines = getColumns(columnNames, "Vaccine");
        for (Object vaccine : vaccines) {
            listModel.addElement((String) vaccine);
        }
        vaccinesList = new JList<>(listModel);

        exemptionsPanel.add(new JLabel("Vaccine exemptions:"));
        exemptionsPanel.add(vaccinesList);

        inputFieldsPanel.add(exemptionsPanel);
    }

    private void createStatements() {

        try {
            values = "\"" + nameTextField.getText() + "\", \"" + vulnerabilityLevelSpinner.getValue() + "\"";
            String statement = "INSERT INTO MedicalCondition (name, vulnerabilityLevel) VALUES (" + values + ");";
            vaccineSystem.executeUpdate(statement);
            String[] columnNames = new String[]{"MAX(medicalConditionID)"};
            ArrayList<ArrayList<String>> resultSet = vaccineSystem.executeSelect(columnNames, "MedicalCondition");
            int medicalConditionID = Integer.parseInt(resultSet.get(0).get(0));

            for (String vaccine : vaccinesList.getSelectedValuesList()) {
                int vaccineID = Integer.parseInt(vaccine.split(":")[0]);
                values = vaccineID + "," + medicalConditionID;
                statements.add("INSERT INTO VaccineExemption (vaccineID, medicalConditionID) VALUES (" + values + ");");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        createStatements();
        super.actionPerformed(e);
    }
}
