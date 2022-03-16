package UserInterface.AddPages;

import UserInterface.AddPage;
import Core.VaccineSystem;
import UserInterface.AddUtils.Insert;
import UserInterface.LoggedInPage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import static UserInterface.Utils.*;

public class AddPersonPage extends AddPage {

    private JTextField forenameTextField, surnameTextField, DoBTextField;
    private JList<String> medicalConditionsList;

    public AddPersonPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage) {
        super(vaccineSystem, loggedInPage, "Add Person:");

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
        inserts = new ArrayList<>();

        String forename = forenameTextField.getText();
        String surname = surnameTextField.getText();
        String DoB = DoBTextField.getText();

        String[] columnNames = new String[] {"forename", "surname", "DoB"};
        Object[] values = new Object[] {forename, surname, DoB};
        String personID = insertAndGetID(columnNames, values, "Person", "personID");

        for (String medicalCondition : medicalConditionsList.getSelectedValuesList()) {
            int medicalConditionID = Integer.parseInt( medicalCondition.split(":")[0]);

            columnNames = new String[] {"personID", "medicalConditionID"};
            values = new Object[] {personID, medicalConditionID};
            inserts.add(new Insert(columnNames, values, "PersonMedicalCondition"));
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
