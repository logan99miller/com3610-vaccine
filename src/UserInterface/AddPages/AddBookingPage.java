package UserInterface.AddPages;

import UserInterface.AddPage;
import UserInterface.MainPage;
import UserInterface.Page;
import Core.VaccineSystem;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddBookingPage extends AddPage {

    private JComboBox personComboBox, vaccinationCentreComboBox;
    private JTextField dateTextField;
    private JSpinner hourSpinner, minuteSpinner;

    public AddBookingPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Booking:");

        String[] personColumnNames = {"personID", "forename", "surname"};
        String[] vaccinationCentreColumnNames = new String[] {"vaccinationCentreID", "name"};

        personComboBox = new JComboBox(getFormattedSelect(personColumnNames, "Person").toArray());
        vaccinationCentreComboBox = new JComboBox(getFormattedSelect(vaccinationCentreColumnNames, "VaccinationCentre").toArray());
        dateTextField = new JTextField();
        hourSpinner = Page.createJSpinner(0, 24, 2);
        minuteSpinner = Page.createJSpinner(0, 60, 2);

        addLabelledComponent(inputGridPanel, "Name:", personComboBox);
        addLabelledComponent(inputGridPanel, "Vaccination Centre:", vaccinationCentreComboBox);
        addLabelledComponent(inputGridPanel, "*Date (YYYY-MM-DD):", dateTextField);
        addLabelledComponent(inputGridPanel, "Time:", createTimePanel(hourSpinner, minuteSpinner, 9, 0));

        setMaxWidthMinHeight(inputPanel);
    }

    private void createStatements() {
        statements = new ArrayList<>();

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
            createStatements();
        }
        super.actionPerformed(e);
    }
}
