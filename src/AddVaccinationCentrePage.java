import javax.swing.*;
import java.awt.event.ActionEvent;

public class AddVaccinationCentrePage extends AddStorageLocationPage {

    private JTextField nameTextField, vaccinesPerMinTextField;

    public AddVaccinationCentrePage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Vaccination Centre:");
        nameTextField = new JTextField();
        vaccinesPerMinTextField = new JTextField();
        addLabelledComponent(inputGridPanel,"*Name:", nameTextField);
        addLabelledComponent(inputGridPanel,"-*Vaccines Per Min:", vaccinesPerMinTextField);
        setMaxWidthMinHeight(inputPanel);
    }

    protected void createStatements() {
        super.createStatements();

        String name = nameTextField.getText();
        String vaccinesPerMin = vaccinesPerMinTextField.getText();

        values = storageLocationID + ", \"" + name + "\", " + vaccinesPerMin;
        statements.add("INSERT INTO VaccinationCentre (storageLocationID, name, vaccinesPerMin) VALUES (" + values + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
