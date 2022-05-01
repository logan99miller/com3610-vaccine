/**
 * Initial page the user sees. Requires them to input the database's username and password before accessing the system.
 */
package UserInterface;

import Core.VaccineSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginPage extends Page {

    private JTextField userField;
    private JPasswordField passwordField;
    private JButton submitButton;

    public LoginPage(VaccineSystem vaccineSystem) {
        super(vaccineSystem);

        JPanel gridPanel = new JPanel(new GridLayout(0, 2));
        mainPanel.add(gridPanel);

        userField = new JTextField();
        passwordField = new JPasswordField();
        submitButton = new JButton("Submit");

        addLabelledComponent(gridPanel, "User:", userField);
        addLabelledComponent(gridPanel, "Password", passwordField);
        addButton(submitButton, mainPanel);

        setMaxWidthMinHeight(gridPanel);
    }

    /**
     * If submit button is pressed, attempts to access the system with given credentials. If successful then user is granted
     * access to the system
     */
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == submitButton) {

            String user = userField.getText();
            String password = String.valueOf(passwordField.getPassword());

            // URL pre-defined in vaccine system
            String url = vaccineSystem.getURL();

            // If given credentials allow a successful connection, then user can access system
            try (Connection ignored = DriverManager.getConnection(url, user, password)) {
                vaccineSystem.login(user, password);
            } catch (SQLException ex) {
                errorMessage("Incorrect database login details");
            }
        }
    }
}