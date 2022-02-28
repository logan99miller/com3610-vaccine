package UserInterface;

import Core.VaccineSystem;
import UserInterface.Page;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;

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

        userField.setText("root");

        addLabelledComponent(gridPanel, "User:", userField);
        addLabelledComponent(gridPanel, "Password", passwordField);
        addButton(submitButton, mainPanel);

        setMaxWidthMinHeight(gridPanel);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String user = userField.getText();
            String password = String.valueOf(passwordField.getPassword());
            String url = vaccineSystem.getURL();
            try (Connection ignored = DriverManager.getConnection(url, user, password)) {
                vaccineSystem.setUser(user);
                vaccineSystem.setPassword(password);
                vaccineSystem.setPageName("main");
                vaccineSystem.updatePage();
            } catch (Exception ex) {
                errorMessage("Incorrect database login details");
            }
        }
    }
}