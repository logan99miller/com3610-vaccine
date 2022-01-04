import javax.swing.*;
import java.awt.*;

public class LoginPage extends Page {

    private JTextField userField, passwordField;
    private JButton submitButton;

    public LoginPage() {
        super();
        final int COLUMN_SIZE = 15;
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createUserPanel(COLUMN_SIZE));
        panel.add(createPasswordPanel(COLUMN_SIZE));
        panel.add(createSubmitPanel());
    }

    private JPanel createInputPanel(JComponent component, String inputText) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));

        panel.add(new JLabel(inputText));
        panel.add(component);
        panel.setMaximumSize(panel.getPreferredSize());
        return panel;
    }

    private JPanel createUserPanel(int columnSize) {
        userField = new JTextField();
        userField.setColumns(columnSize);
        return createInputPanel(userField, "User:");
    }

    private JPanel createPasswordPanel(int columnSize) {
        passwordField = new JPasswordField();
        passwordField.setColumns(columnSize);
        return createInputPanel(passwordField, "Password:");
    }

    private JPanel createSubmitPanel() {
        submitButton = new JButton("Submit");
//        submitButton.addActionListener(this);

        JPanel panel = new JPanel();
        panel.add(submitButton);
        return panel;
    }

    public JPanel getPanel() {
        return panel;
    }

    public JButton getSubmitButton() {
        return submitButton;
    }

    public String getUser() {
        return userField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }
}
