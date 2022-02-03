import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddVaccinePriorityPage extends AddPage {

    private JCheckBox eligibleCheckbox;
    private JComboBox vaccineComboBox;
    private JSpinner lowestAgeSpinner, highestAgeSpinner, doseNumberSpinner;
    private JList<String> vaccinePriorityList;

    public AddVaccinePriorityPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Vaccine Priority:");

        JPanel listPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(listPanel);

        String[] vaccineColumnNames = new String[]{"vaccineID", "name"};
        String[] vaccinePriorityColumnNames = new String[] {"vaccinePriorityID", "eligible", "vaccineID", "lowestAge", "highestAge", "doseNumber", "positionInQueue"};

        ListModel vaccinePriorities = ArrayListToListModel(getFormattedSelect(vaccinePriorityColumnNames, "VaccinePriority"));

        eligibleCheckbox = new JCheckBox();
        vaccineComboBox = new JComboBox(getFormattedSelect(vaccineColumnNames, "Vaccine").toArray());
        lowestAgeSpinner = createJSpinner(1, 100, 3);
        highestAgeSpinner = createJSpinner(1, 100, 3);
        doseNumberSpinner = createJSpinner(1, 100, 3);
        vaccinePriorityList = new JList(vaccinePriorities);

        eligibleCheckbox.setSelected(true);

        // Wait until you're making the view table to do position in queue as it will involve needing to see all details of other vaccine priority

        addLabelledComponent(inputGridPanel, "Eligible:", eligibleCheckbox);
        addLabelledComponent(inputGridPanel, "Vaccine:", vaccineComboBox);
        addLabelledComponent(inputGridPanel, "Lowest Age:", lowestAgeSpinner);
        addLabelledComponent(inputGridPanel, "Highest Age:", highestAgeSpinner);
        addLabelledComponent(inputGridPanel, "Dose Number:", doseNumberSpinner);
        addLabelledComponent(listPanel, "Position in queue:", vaccinePriorityList);

        setMaxWidthMinHeight(inputPanel);
    }


    private void createStatements() {
        statements = new ArrayList<>();

        String vaccine = (String) vaccineComboBox.getSelectedItem();

        int vaccineID = Integer.parseInt(vaccine.split(":")[0]);
        boolean eligible = eligibleCheckbox.isSelected();
        int lowestAge = (int) lowestAgeSpinner.getValue();
        int highestAge = (int) highestAgeSpinner.getValue();
        int doseNumber = (int) doseNumberSpinner.getValue();
        int positionInQueue = 1;

        String values = vaccineID + ", " + lowestAge + ", " + highestAge + ", " + doseNumber + ", " + positionInQueue + ", " + eligible;
        statements.add("INSERT INTO VaccinePriority (vaccineID, lowestAge, highestAge, doseNumber, positionInQueue, eligible) VALUES (" + values + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
