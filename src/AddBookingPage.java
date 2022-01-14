import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddBookingPage extends AddPage {

    private JComboBox personComboBox, vaccinationCentreComboBox;
    private JTextField dateTextField;
    private JSpinner hourSpinner, minuteSpinner;

    public AddBookingPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Booking:");
        createInputFieldsGridPanel();
        fitPanelToMainPanel(inputFieldsPanel);
    }

    private void createInputFieldsGridPanel() {
        JPanel inputFieldsGridPanel = new JPanel(new GridLayout(0, 2));

        String[] columnNames = {"personID", "forename", "surname"};
        personComboBox = new JComboBox(getColumns(columnNames, "Person"));

        columnNames = new String[] {"vaccinationCentreID", "name"};
        vaccinationCentreComboBox = new JComboBox(getColumns(columnNames, "VaccinationCentre"));

        dateTextField = new JTextField();

        inputFieldsGridPanel.add(new JLabel("Person:"));
        inputFieldsGridPanel.add(personComboBox);
        inputFieldsGridPanel.add(new JLabel("Vaccination Centre:"));
        inputFieldsGridPanel.add(vaccinationCentreComboBox);
        inputFieldsGridPanel.add(new JLabel("*Date (YYYY-MM-DD):"));
        inputFieldsGridPanel.add(dateTextField);
        inputFieldsGridPanel.add(new JLabel("Time:"));
        inputFieldsGridPanel.add(getTimePanel());

        inputFieldsPanel.add(inputFieldsGridPanel);
    }

    public JPanel getTimePanel() {
        hourSpinner = createJSpinner(0, 24, 2);
        minuteSpinner = createJSpinner(0, 60, 2);

        hourSpinner.setValue(9);

        JPanel timePanel = new JPanel();
        timePanel.add(new JLabel("Start (HH:MM):"));
        timePanel.add(hourSpinner);
        timePanel.add(new JLabel(":"));
        timePanel.add(minuteSpinner);
        return timePanel;
    }


    private void createStatements() {
        String person = (String) personComboBox.getSelectedItem();
        String vaccinationCentre = (String) vaccinationCentreComboBox.getSelectedItem();

        int personID = Integer.parseInt(person.split(":")[0]);
        int vaccinationCentreID = Integer.parseInt(vaccinationCentre.split(":")[0]);
        String date = dateTextField.getText() + " " + hourSpinner.getValue() + ":" + minuteSpinner.getValue();

        values = personID + ", " + vaccinationCentreID + ", '" + date + "'";
        statements.add("INSERT INTO Booking (personID, vaccinationCentreID, date) VALUES (" + values + ")");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            if (checkDate(dateTextField.getText())) {
                createStatements();
                super.actionPerformed(e);
            }
            else {
                incorrectDateMessage();
            }
        }
        else {
            super.actionPerformed(e);
        }
    }
}
