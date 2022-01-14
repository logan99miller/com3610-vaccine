import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class AddPage extends Page {

    private MainPage mainPage;
    private JButton backButton;
    protected JPanel inputFieldsPanel;
    protected JButton submitButton;
    protected ArrayList<String> statements;
    protected String values;

    public AddPage(VaccineSystem vaccineSystem, MainPage mainPage, String title) {
        super(vaccineSystem);
        this.mainPage = mainPage;
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        statements = new ArrayList<>();

        createBackButton();
        createPageTitle(title);
        createInputFieldsPanel();
        createSubmitButton();
        createFieldExplanations();
    }

    public AddPage() {
        super();
    }

    protected static JSpinner createJSpinner(int minValue, int maxValue, int columns) {
        ArrayList<Integer> possibleValues = new ArrayList<>();
        for (int i = minValue; i < maxValue; i++) {
            possibleValues.add(i);
        }

        SpinnerListModel spinnerListModel = new SpinnerListModel(possibleValues);

        JSpinner spinner = new JSpinner(spinnerListModel);
        JFormattedTextField textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        textField.setEditable(false);
        textField.setColumns(columns);

        return spinner;


    }

    private void createBackButton() {
        backButton = new JButton("Back");
        addButton(backButton, mainPanel);
    }

    protected void createPageTitle(String title) {
        JLabel pageTitle = new JLabel(title);
        mainPanel.add(pageTitle);
    }

    protected void createInputFieldsPanel() {
        inputFieldsPanel = new JPanel();
        inputFieldsPanel.setLayout(new BoxLayout(inputFieldsPanel, BoxLayout.Y_AXIS));
        inputFieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(inputFieldsPanel);
    }

    protected void createSubmitButton() {
        submitButton = new JButton("Submit");
        addButton(submitButton, mainPanel);
    }

    protected void createFieldExplanations() {
        mainPanel.add(new JLabel("Fields marked with a * are required"));
        mainPanel.add(new JLabel("Fields marked with a - require a numeric input"));
    }

    protected void returnToSelectPage() {
        statements = new ArrayList<>();
        mainPage.setPageName("add");
        mainPage.updatePage();
    }

    protected void emptyFieldsMessage() {
        String message = "Not all required fields have been filled";
        String title = "Error";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    protected void numericFieldsMessage() {
        String message = "Fields marked with a - must have contain numeric values";
        String title = "Error";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    protected void incorrectDateMessage() {
        String message = "Dates must be in the format YYYY-MM-DD";
        String title = "Error";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    protected boolean checkDate(String dateAsString) {
        ArrayList<Integer> dateComponents = new ArrayList<>();
        try {
            for (String dateComponent : dateAsString.split("-")) {
                dateComponents.add(Integer.parseInt(dateComponent));
            }

            LocalDate date = LocalDate.of(dateComponents.get(0), dateComponents.get(1), dateComponents.get(2));
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    protected boolean fieldConditionsMet() {
        for (Component component : inputFieldsPanel.getComponents()) {

            if (component instanceof JPanel) {
                Component[] panelComponents = ((JPanel) component).getComponents();

                // If panel contains a JLabel and an input field
                if ((panelComponents.length > 1) && (panelComponents[0] instanceof JLabel)) {
                    String labelText = ((JLabel) panelComponents[0]).getText();

                    for (int i = 1; i < panelComponents.length; i++) {

                        if ((panelComponents[i] instanceof JTextField)) {

                            String inputText = ((JTextField) panelComponents[i]).getText();

                            // If label marked with a *, check field is not empty
                            if (labelText.substring(0, 1).equals("*") || (labelText.substring(0, 2).equals("-*"))) {
                                if (inputText.equals("")) {
                                    emptyFieldsMessage();
                                    return false;
                                }
                            }
                            // If label marked with a -, check input is a numeric value
                            if (labelText.substring(0, 1).equals("-")) {
                                try {
                                    Float.parseFloat(inputText);
                                } catch (NumberFormatException e) {
                                    numericFieldsMessage();
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    protected Object[] getColumns(String[] columnNames, String tableName) {
        ArrayList<String> records = new ArrayList<>();

        try {

            ArrayList<ArrayList<String>> resultSet = vaccineSystem.executeSelect(columnNames, tableName);
            for (ArrayList<String> record : resultSet) {
                String addToRecord = record.get(0) + ":";
                for (int i = 1; i < record.size(); i++) {
                    addToRecord += " " + record.get(i);
                }
                records.add(addToRecord);
            }
        } catch (SQLException e) {}

        return (records.toArray());
    }

    protected void performStatements() {
        try {
            for (String statement : statements) {
                vaccineSystem.executeUpdate(statement);
            }
            returnToSelectPage();
        }
        catch (SQLException ex) {}
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            returnToSelectPage();
        }
        else if (e.getSource() == submitButton) {
            if (fieldConditionsMet()) {
                performStatements();
            }
        }
    }

}
