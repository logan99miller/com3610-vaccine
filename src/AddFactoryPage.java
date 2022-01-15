import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddFactoryPage extends AddStorageLocationPage {

    private JTextField vaccinesPerMinTextField;
    private JComboBox manufacturersComboBox;

    public AddFactoryPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Factory:");

        vaccinesPerMinTextField = new JTextField();

        String[] columnNames = {"manufacturerID", "name"};
        manufacturersComboBox = new JComboBox(getFormattedSelect(columnNames, "Manufacturer"));

        addLabelledComponent(inputGridPanel, "-*Vaccines per minute:", vaccinesPerMinTextField);
        addLabelledComponent(inputGridPanel, "Manufacturer:", manufacturersComboBox);

        setMaxWidthMinHeight(inputPanel);
    }

    protected void createStatements() {
        super.createStatements();
        statements = new ArrayList<>();

        String vaccinesPerMin = vaccinesPerMinTextField.getText();
        String manufacturer = (String) manufacturersComboBox.getSelectedItem();

        int manufacturerID = Integer.parseInt(manufacturer.split(":")[0]);

        values = storageLocationID + ", " + manufacturerID + ", " + vaccinesPerMin;
        statements.add("INSERT INTO Factory (storageLocationID, manufacturerID, vaccinesPerMin) VALUES (" + values + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
