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

public class AddMedicalConditionPage extends AddPage {

    private JTextField nameTextField;
    private JSpinner vulnerabilityLevelSpinner;
    private JList<String> vaccinesList;

    public AddMedicalConditionPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage) {
        super(vaccineSystem, loggedInPage, "Add Medical Condition:");

        JPanel listPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(listPanel);

        ListModel vaccines = ArrayListToListModel(getFormattedSelect(new String[] {"vaccineID", "name"}, "vaccine"));

        nameTextField = new JTextField();
        vulnerabilityLevelSpinner = createJSpinner(1, 100, 3);
        vaccinesList = new JList(vaccines);

        addLabelledComponent(inputGridPanel, "*Name:", nameTextField);
        addLabelledComponent(inputGridPanel, "Vulnerability Level:", vulnerabilityLevelSpinner);
        addLabelledComponent(listPanel, "Vaccine exemptions: ", vaccinesList);

        setMaxWidthMinHeight(inputPanel);
    }

    private void createStatements() {
        inserts = new ArrayList<>();

        String[] columnNames = new String[] {"name", "vulnerabilityLevel"};
        Object[] values = new Object[] {nameTextField.getText(), vulnerabilityLevelSpinner.getValue()};
        String medicalConditionID = insertAndGetID(columnNames, values, "MedicalCondition", "medicalConditionID");

        for (String vaccine : vaccinesList.getSelectedValuesList()) {
            int vaccineID = Integer.parseInt(vaccine.split(":")[0]);

            columnNames = new String[] {"vaccineID", "medicalConditionID"};
            values = new Object[] {vaccineID, medicalConditionID};
            inserts.add(new Insert(columnNames, values, "VaccineExemption"));
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
