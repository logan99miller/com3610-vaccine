package UserInterface.SelectPages;

import Core.VaccineSystem;
import UserInterface.LoggedInPage;
import UserInterface.Page;

import javax.swing.*;

public class SelectPage extends Page {

    protected LoggedInPage loggedInPage;

    public SelectPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage, String buttonAction) {
        super(vaccineSystem);
        this.loggedInPage = loggedInPage;
        this.buttonAction = buttonAction;

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel pageTitle = new JLabel(buttonAction + ":");
        mainPanel.add(pageTitle);
    }
}
