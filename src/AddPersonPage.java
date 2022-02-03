import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddPersonPage extends AddPage {

    private JTextField forenameTextField, surnameTextField, DoBTextField;
    private JList<String> medicalConditionsList;

    public AddPersonPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Person:");

        JPanel medicalConditionsPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(medicalConditionsPanel);

        ArrayList<String> medicalConditions = getFormattedSelect(new String[] {"medicalConditionID", "name"}, "MedicalCondition");
        ListModel medicalConditionsListModel = ArrayListToListModel(medicalConditions);

        forenameTextField = new JTextField();
        surnameTextField = new JTextField();
        DoBTextField = new JTextField();
        medicalConditionsList = new JList(medicalConditionsListModel);

        addLabelledComponent(inputGridPanel, "*Forename:", forenameTextField);
        addLabelledComponent(inputGridPanel, "*Surname:", surnameTextField);
        addLabelledComponent(inputGridPanel, "*DoB (YYYY-MM-DD):", DoBTextField);
        addLabelledComponent(medicalConditionsPanel, "Medical Exemptions:", medicalConditionsList);

        setMaxWidthMinHeight(inputPanel);
    }

    private void createStatements() {
        statements = new ArrayList<>();

        String forename = forenameTextField.getText();
        String surname = surnameTextField.getText();
        String DoB = DoBTextField.getText();

        values = "\"" + forename + "\", \"" + surname + "\", '" + DoB + "'";
        String statement = "INSERT INTO Person (forename, surname, DoB) VALUES (" + values + ");";
        int personID = insertAndGetID(statement, "personID", "Person");

        for (String medicalCondition : medicalConditionsList.getSelectedValuesList()) {
            int medicalConditionID = Integer.parseInt( medicalCondition.split(":")[0]);
            values = personID + "," + medicalConditionID;
            statements.add("INSERT INTO PersonMedicalCondition (personID, medicalConditionID) VALUES (" + values + ");");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
