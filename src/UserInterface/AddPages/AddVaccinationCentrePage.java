package UserInterface.AddPages;

import Core.VaccineSystem;
import UserInterface.MainPage;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class AddVaccinationCentrePage extends AddStorageLocationPage {

    private JTextField nameTextField, vaccinesPerHourTextField;

    public AddVaccinationCentrePage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Vaccination Centre:");
        nameTextField = new JTextField();
        vaccinesPerHourTextField = new JTextField();
        addLabelledComponent(inputGridPanel,"*Name:", nameTextField);
        addLabelledComponent(inputGridPanel,"#*Vaccines Per Hour:", vaccinesPerHourTextField);
        setMaxWidthMinHeight(inputPanel);
    }

    protected void createStatements() {
        super.createStatements();

        String name = nameTextField.getText();
        String vaccinesPerHour = vaccinesPerHourTextField.getText();

        String[] columnNames = new String[] {"storageLocationID", "name", "vaccinesPerHour"};
        Object[] values = new Object[]  {storageLocationID, name, vaccinesPerHour};
        inserts.add(new Insert(columnNames, values, "VaccinationCentre"));
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
