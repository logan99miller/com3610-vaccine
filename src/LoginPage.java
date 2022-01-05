import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;

public class LoginPage extends Page {

    private JTextField userField;
    private JPasswordField passwordField;
    private JButton submitButton;

    final private int INPUT_COLUMN_SIZE = 15;

    public LoginPage(VaccineSystem vaccineSystem) {
        super(vaccineSystem);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(createUserPanel());
        mainPanel.add(createPasswordPanel());
        mainPanel.add(createSubmitPanel());
    }

    private JPanel createUserPanel() {
        userField = new JTextField();
        userField.setColumns(INPUT_COLUMN_SIZE);
        return createLabelledComponentPanel(userField, "User:");
    }

    private JPanel createPasswordPanel() {
        passwordField = new JPasswordField();
        passwordField.setColumns(INPUT_COLUMN_SIZE);
        return createLabelledComponentPanel(passwordField, "Password:");
    }

    private JPanel createSubmitPanel() {
        JPanel panel = new JPanel();
        submitButton = new JButton("Submit");
        panel.add(submitButton);
        submitButton.addActionListener(this);
        return panel;
    }

    private void invalidLoginMessage() {
        String message = "Incorrect database login details";
        String title = "Error";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String user = userField.getText();
            String password = passwordField.getText();

            try (Connection ignored = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306", user, password)) {
                vaccineSystem.setUser(user);
                vaccineSystem.setPassword(password);
                vaccineSystem.setPageName("main");
                System.out.println("Main page");
            } catch (Exception ex) {
                ex.printStackTrace();
                invalidLoginMessage();
            }
        }
    }
}