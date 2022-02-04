import javax.swing.*;
import java.awt.event.ActionEvent;

public class AddVaccinationCentrePage extends AddStorageLocationPage {

    JTextField nameTextField;

    public AddVaccinationCentrePage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Vaccination Centre:");
        nameTextField = new JTextField();
        addLabelledComponent(inputGridPanel,"*Name:", nameTextField);
        setMaxWidthMinHeight(inputPanel);
    }

    protected void createStatements() {
        super.createStatements();

        String name = nameTextField.getText();

        values = storageLocationID + ", \"" + name + "\"";
        statements.add("INSERT INTO VaccinationCentre (storageLocationID, name) VALUES (" + values + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
