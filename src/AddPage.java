import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class AddPage extends Page {

    protected MainPage mainPage;
    private JButton backButton;
    protected JPanel inputPanel, inputGridPanel;
    protected JButton submitButton;
    protected ArrayList<String> statements;
    protected String values;

    public AddPage(VaccineSystem vaccineSystem, MainPage mainPage, String title) {
        super(vaccineSystem);
        this.mainPage = mainPage;

        statements = new ArrayList<>();

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
        mainPanel.add(new JLabel("Fields marked with a - require a numeric input"));
    }

    protected void returnToSelectPage() {
        statements = new ArrayList<>();
        mainPage.setPageName("add");
        mainPage.updatePage();
    }

    protected int insertAndGetID(String statement, String IDFieldName, String tableName) {

        if (checkInputConditions()) {
            try {
                vaccineSystem.executeUpdate(statement);
                String[] columnNames = new String[]{"MAX(" + IDFieldName + ")"};
                ArrayList<ArrayList<String>> resultSet = vaccineSystem.executeSelect(columnNames, tableName);
                return Integer.parseInt(resultSet.get(0).get(0));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    protected boolean checkInputConditions() {
        System.out.println("checkInputConditons()");
        Component previousComponent = new JPanel();

        for (Component component : inputGridPanel.getComponents()) {

            if (previousComponent instanceof JLabel) {

                String label = ((JLabel) previousComponent).getText().toLowerCase();

                if (component instanceof JTextField) {
                    String text = ((JTextField) component).getText();
                    if (!checkRequiredInput(label, text)) {
                        errorMessage("Fields marked with a * must be filled");
                        return false;
                    }
                    else if (!checkNumericInput(label, text)) {
                        errorMessage("Fields marked with a - must have a numeric value");
                        return false;
                    }
                    else if (!checkDateInput(label, text)) {
                        errorMessage("Dates must be input in the format YYYY-MM-DD");
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
            for (String statement : statements) {
                vaccineSystem.executeUpdate(statement);
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
            if (checkInputConditions()) {
                performStatements();
            }
        }
    }
}
