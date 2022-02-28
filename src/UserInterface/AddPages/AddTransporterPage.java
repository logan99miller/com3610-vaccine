package UserInterface.AddPages;

import UserInterface.AddPage;
import Core.VaccineSystem;
import UserInterface.MainPage;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddTransporterPage extends AddPage {

    JTextField nameTextField;

    public AddTransporterPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Transporter:");
        nameTextField = new JTextField();
        addLabelledComponent(inputGridPanel, "*Name:", nameTextField);
        setMaxWidthMinHeight(inputPanel);
    }

    private void createStatements() {
        statements = new ArrayList<>();

        String values = "\"" + nameTextField.getText() + "\"";
        statements.add("INSERT INTO Transporter (name) VALUES (" + values + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
