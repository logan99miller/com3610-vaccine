import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddMedicalConditionPage extends AddPage {

    private JTextField nameTextField;
    private JSpinner vulnerabilityLevelSpinner;
    private JList<String> vaccinesList;

    public AddMedicalConditionPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Medical Condition:");

        JPanel listPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(listPanel);

        nameTextField = new JTextField();
        vulnerabilityLevelSpinner = createJSpinner(1, 100, 3);
        vaccinesList = getColumnsAsJList(new String[] {"vaccineID", "name"}, "vaccine");

        addLabelledComponent(inputGridPanel, "*Name:", nameTextField);
        addLabelledComponent(inputGridPanel, "Vulnerability Level:", vulnerabilityLevelSpinner);
        addLabelledComponent(listPanel, "Vaccine exemptions: ", vaccinesList);

        setMaxWidthMinHeight(inputPanel);
    }

    private void createStatements() {
        statements = new ArrayList<>();

        String values = "\"" + nameTextField.getText() + "\", \"" + vulnerabilityLevelSpinner.getValue() + "\"";
        String statement = "INSERT INTO MedicalCondition (name, vulnerabilityLevel) VALUES (" + values + ");";
        int medicalConditionID = insertAndGetID(statement, "medicalConditionID", "MedicalCondition");

        for (String vaccine : vaccinesList.getSelectedValuesList()) {
            int vaccineID = Integer.parseInt(vaccine.split(":")[0]);
            values = vaccineID + "," + medicalConditionID;
            statements.add("INSERT INTO VaccineExemption (vaccineID, medicalConditionID) VALUES (" + values + ");");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
