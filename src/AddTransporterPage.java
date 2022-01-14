import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddTransporterPage extends AddPage {

    JTextField nameTextField;

    public AddTransporterPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Transporter:");
        createNamePanel();
        fitPanelToMainPanel(inputFieldsPanel);
    }

    private void createNamePanel() {
        JPanel namePanel = new JPanel(new GridLayout(0, 2));

        nameTextField = new JTextField();

        namePanel.add(new JLabel("*Name:"));
        namePanel.add(nameTextField);

        inputFieldsPanel.add(namePanel);
    }

    private void createStatements() {
        values = "\"" + nameTextField.getText() + "\"";
        statements.add("INSERT INTO Transporter (name) VALUES (" + values + ");");
    }

    public void actionPerformed(ActionEvent e) {
        createStatements();
        super.actionPerformed(e);
    }
}
