import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddStockPage extends AddPage {

    private JComboBox vaccineComboBox, storeComboBox;
    private JTextField expirationDateTextField;
    private JSpinner stockLevelSpinner;

    public AddStockPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Stock:");

        String[] vaccineColumnNames = new String[] {"vaccineID", "name"};
        String[] storeColumnNames = new String[] {"storeID", "capacity"};

        vaccineComboBox = new JComboBox(getFormattedSelect(vaccineColumnNames, "Vaccine").toArray());
        storeComboBox = new JComboBox(getFormattedSelect(storeColumnNames, "Store").toArray());
        expirationDateTextField = new JTextField();
        stockLevelSpinner = createJSpinner(0, 24, 2);

        addLabelledComponent(inputGridPanel, "Vaccine:", vaccineComboBox);
        addLabelledComponent(inputPanel, "Store:", storeComboBox);
        addLabelledComponent(inputGridPanel, "Stock Level:", stockLevelSpinner);
        addLabelledComponent(inputGridPanel, "*Expiration Date (YYYY-MM-DD):", expirationDateTextField);

        setMaxWidthMinHeight(inputPanel);
    }

    private void createStatements() {
        statements = new ArrayList<>();

        String vaccineID = (String) vaccineComboBox.getSelectedItem();
        String storeID = "0";
        String stockLevel = (String) stockLevelSpinner.getValue();
        String expirationDate = expirationDateTextField.getText();

        values = vaccineID + ", " + storeID + ", " + stockLevel + ", " + expirationDate;
        statements.add("INSERT INTO VaccineInStorage (vaccineID, storeID, stockLevel, expirationDate) VALUES (" + values + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
