import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddPersonPage extends AddPage {

    JTextField forenameTextField, surnameTextField, DoBTextField;

    public AddPersonPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Person:");

        forenameTextField = new JTextField();
        surnameTextField = new JTextField();
        DoBTextField = new JTextField();

        addLabelledComponent(inputGridPanel, "*Forename:", forenameTextField);
        addLabelledComponent(inputGridPanel, "*Surname:", surnameTextField);
        addLabelledComponent(inputGridPanel, "*DoB (YYYY-MM-DD):", DoBTextField);

        setMaxWidthMinHeight(inputPanel);
    }

    private void createStatements() {
        statements = new ArrayList<>();

        String forename = forenameTextField.getText();
        String surname = surnameTextField.getText();
        String DoB = DoBTextField.getText();

        values = "\"" + forename + "\", \"" + surname + "\", '" + DoB + "'";
        statements.add("INSERT INTO Person (forename, surname, DoB) VALUES (" + values + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
