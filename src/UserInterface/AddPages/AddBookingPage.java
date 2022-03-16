package UserInterface.AddPages;

import UserInterface.AddPage;
import UserInterface.LoggedInPage;
import UserInterface.Page;
import Core.VaccineSystem;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import static UserInterface.Utils.*;

public class AddBookingPage extends AddPage {

    private JComboBox personComboBox, vaccinationCentreComboBox;
    private JTextField dateTextField;
    private JSpinner hourSpinner, minuteSpinner;

    public AddBookingPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage) {
        super(vaccineSystem, loggedInPage, "Add Booking:");

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
        inserts = new ArrayList<>();

        String person = (String) personComboBox.getSelectedItem();
        String vaccinationCentre = (String) vaccinationCentreComboBox.getSelectedItem();

        int personID = Integer.parseInt(person.split(":")[0]);
        int vaccinationCentreID = Integer.parseInt(vaccinationCentre.split(":")[0]);
        String date = dateTextField.getText() + " " + hourSpinner.getValue() + ":" + minuteSpinner.getValue();

        String[] columnNames = new String[] {"personID", "vaccinationCentreID", "date"};
        Object[] values = new Object[] {personID, vaccinationCentreID, date};
        try {
            vaccineSystem.insert(columnNames, values, "Booking");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
