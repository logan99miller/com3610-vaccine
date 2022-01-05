import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainPage extends Page {

    private JButton addPageButton, logoutButton;

    public MainPage(VaccineSystem vaccineSystem) {
        super(vaccineSystem);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(createNavPanel(), BorderLayout.NORTH);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createNavPanel() {
        JPanel panel = new JPanel();

        addPageButton = new JButton("Add");
        logoutButton = new JButton("Log Out");

        panel.add(addPageButton);
        panel.add(logoutButton);

        addPageButton.addActionListener(this);
        logoutButton.addActionListener(this);

        return panel;
    }

    private JPanel createContentPanel() {
        CardLayout mainCardLayout = new CardLayout();
        JPanel panel = new JPanel(mainCardLayout);

        return panel;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addPageButton) {
            System.out.println("Add Page");
        }
        else if (e.getSource() == logoutButton) {
            System.out.println("Log out");
            vaccineSystem.setPageName("login");
        }
    }

}
