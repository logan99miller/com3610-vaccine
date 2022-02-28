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
        inserts = new ArrayList<>();

        String[] columnNames = {"name"};
        Object[] values = {nameTextField.getText()};
        inserts.add(new Insert(columnNames, values, "Transporter"));
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
