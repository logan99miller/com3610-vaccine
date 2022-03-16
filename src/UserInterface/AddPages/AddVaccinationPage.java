package UserInterface.AddPages;

import UserInterface.AddPage;
import Core.VaccineSystem;
import UserInterface.AddUtils.Insert;
import UserInterface.LoggedInPage;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import static UserInterface.Utils.*;

public class AddVaccinationPage extends AddPage {

    private JComboBox personComboBox, vaccineComboBox;
    private JTextField dateTextField;

    public AddVaccinationPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage) {
        super(vaccineSystem, loggedInPage, "Add Vaccination:");

        String[] columnNames = {"personID", "forename", "surname"};
        personComboBox = new JComboBox(getFormattedSelect(columnNames, "Person").toArray());

        columnNames = new String[] {"vaccineID", "name"};
        vaccineComboBox = new JComboBox(getFormattedSelect(columnNames, "Vaccine").toArray());

        dateTextField = new JTextField();

        addLabelledComponent(inputGridPanel, "Person:", personComboBox);
        addLabelledComponent(inputGridPanel, "Vaccine:", vaccineComboBox);
        addLabelledComponent(inputGridPanel, "Date (YYYY-MM-DD):", dateTextField);

        setMaxWidthMinHeight(inputPanel);
    }

    private void createStatements() {
        inserts = new ArrayList<>();

        String person = (String) personComboBox.getSelectedItem();
        String vaccine = (String) vaccineComboBox.getSelectedItem();

        int personID = Integer.parseInt(person.split(":")[0]);
        int vaccineID = Integer.parseInt(vaccine.split(":")[0]);

        String[] columnNames = new String[] {"personID", "vaccineID", "date"};
        Object[] values = new Object[]  {personID, vaccineID, dateTextField.getText()};
        inserts.add(new Insert(columnNames, values, "VaccineReceived"));
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
