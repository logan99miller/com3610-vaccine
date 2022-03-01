package UserInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import Core.VaccineSystem;
import UserInterface.AddPages.Insert;

public class AddPage extends Page {

    protected MainPage mainPage;
    private JButton backButton;
    protected JPanel inputPanel, inputGridPanel;
    protected JButton submitButton;
    protected ArrayList<Insert> inserts;
    protected String values;

    public AddPage(VaccineSystem vaccineSystem, MainPage mainPage, String title) {
        super(vaccineSystem);
        this.mainPage = mainPage;

        inserts = new ArrayList<>();

        createBackButton();
        createPageTitle(title);
        createInputFieldsPanel();
        createSubmitButton();
        createFieldExplanations();

        inputGridPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(inputGridPanel);
    }

    public AddPage() {
        super();
    }

    private void createBackButton() {
        backButton = new JButton("Back");
        addButton(backButton, mainPanel);
    }

    protected void createPageTitle(String title) {
        mainPanel.add(new JLabel(title));
    }

    protected void createInputFieldsPanel() {
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(inputPanel);
    }

    protected void createSubmitButton() {
        submitButton = new JButton("Submit");
        addButton(submitButton, mainPanel);
    }

    private void createFieldExplanations() {
        mainPanel.add(new JLabel("Fields marked with a * are required"));
        mainPanel.add(new JLabel("Fields marked with a # require an integer input"));
        mainPanel.add(new JLabel("Fields marked with a - require a numeric input"));
    }

    protected void returnToSelectPage() {
        inserts = new ArrayList<>();
        mainPage.setPageName("add");
        mainPage.updatePage();
    }

    protected String insertAndGetID(String[] columnNames, Object[] values, String tableName, String IDFieldName) {

        if (checkInputConditions(false)) {
            try {
                vaccineSystem.insert(columnNames, values, tableName);
                final String maxID = "MAX(" + IDFieldName + ")";
                HashMap<String, HashMap<String, Object>> resultSet = vaccineSystem.executeSelect(new String[]{maxID}, tableName);
                String key = resultSet.keySet().iterator().next();

                return (String) resultSet.get(key).get(maxID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    protected boolean checkInputConditions(boolean displayError) {
        Component previousComponent = new JPanel();

        for (Component component : inputGridPanel.getComponents()) {
            if (previousComponent instanceof JLabel) {

                String label = ((JLabel) previousComponent).getText().toLowerCase();

                if (component instanceof JTextField) {
                    String text = ((JTextField) component).getText();
                    if (!checkRequiredInput(label, text)) {
                        errorMessage("Fields marked with a * must be filled", displayError);
                        return false;
                    }
                    else if (!checkNumericInput(label, text)) {
                        errorMessage("Fields marked with a - must have a numeric value", displayError);
                        return false;
                    }
                    else if (!checkIntegerInput(label, text)) {
                        errorMessage("Fields marked with a # must have an integer value", displayError);
                        return false;
                    }
                    else if (!checkDateInput(label, text)) {
                        errorMessage("Dates must be input in the format YYYY-MM-DD", displayError);
                        return false;
                    }
                }
                else if (component instanceof JComboBox) {
                    JComboBox comboBox = (JComboBox) component;
                    if (comboBox.getSelectedItem() == null) {
                        errorMessage("Combo box is empty", displayError);
                        return false;
                    }
                }
            }
            previousComponent = component;
        }
        return true;
    }

    private boolean checkRequiredInput(String label, String text) {
        if (label.startsWith("*") || (label.startsWith("-*"))) {
            return !text.equals("");
        }
        return true;
    }

    private boolean checkNumericInput(String label, String text) {
        if (label.startsWith("-")) {
            try {
                Float.parseFloat(text);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private boolean checkIntegerInput(String label, String text) {
        if (label.startsWith("#")) {
            try {
                Integer.parseInt(text);
            }
            catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private boolean checkDateInput(String label, String text) {
        if (label.contains("yyyy-mm-dd")) {
            ArrayList<Integer> dateComponents = new ArrayList<>();
            try {
                for (String dateComponent : text.split("-")) {
                    dateComponents.add(Integer.parseInt(dateComponent));
                }

                LocalDate.of(dateComponents.get(0), dateComponents.get(1), dateComponents.get(2));
            }
            catch (Exception ex) {
                return false;
            }
        }
        return true;
    }

    protected void performStatements() {

        try {
            for (Insert insert : inserts) {
                vaccineSystem.insert(insert.getColumnNames(), insert.getValues(), insert.getTableName());
            }
            returnToSelectPage();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            returnToSelectPage();
        }
        else if (e.getSource() == submitButton) {
            if (checkInputConditions(true)) {
                performStatements();
            }
        }
    }
}
