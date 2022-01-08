import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Locale;

public class MainPage extends Page {

    private String contentPageName;
    private JButton addPageButton, editPageButton, logPageButton, mapPageButton, logoutButton;
    CardLayout cardLayout;
    JPanel cards;

    public MainPage(VaccineSystem vaccineSystem) {
        super(vaccineSystem);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(createNavPanel(), BorderLayout.NORTH);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createNavPanel() {
        JPanel panel = new JPanel();

        addPageButton = new JButton("Add");
        editPageButton = new JButton("View / Edit");
        logPageButton = new JButton("Activity Log");
        mapPageButton = new JButton("Map View");
        logoutButton = new JButton("Log Out");

        buttons.add(addPageButton);
        buttons.add(editPageButton);
        buttons.add(logPageButton);
        buttons.add(mapPageButton);
        buttons.add(logoutButton);

        for (JButton button : buttons) {
            addButton(button, panel);
        }

        return panel;
    }

    private JPanel createContentPanel() {
//        CardLayout cardLayout = new CardLayout();
//        JPanel cards = new JPanel(cardLayout);
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        SelectTablePage addPage = new SelectTablePage(vaccineSystem, this, addPageButton.getText());
        SelectTablePage editPage = new SelectTablePage(vaccineSystem, this, editPageButton.getText());
        Page logPage = new Page(vaccineSystem);
        Page mapPage = new Page(vaccineSystem);

        JPanel addPanel = addPage.getPanel();
        JPanel editPanel = editPage.getPanel();
        JPanel logPanel = logPage.getPanel();
        JPanel mapPanel = mapPage.getPanel();

        cards.add(addPanel, getSanatizedtext(addPageButton));
        cards.add(editPanel, getSanatizedtext(editPageButton));
        cards.add(logPanel, getSanatizedtext(logPageButton));
        cards.add(mapPanel, getSanatizedtext(mapPageButton));

        cardLayout.first(cards); // Needs fixing

        return cards;
    }

    private String getSanatizedtext(JButton button) {
        return button.getText().replace(" ", "").toLowerCase();
    }

    public void actionPerformed(ActionEvent e) {
        for (JButton button : buttons) {
            if (e.getSource() == button) {

                String buttonText = getSanatizedtext(button);
                if (buttonText.equals("logout")) {
                    vaccineSystem.setPageName("login");
                    vaccineSystem.updatePage();
                }
                else {
                    cardLayout.show(cards, getSanatizedtext(button));
                }
            }
        }
    }

    public String getContentPageName() {
        return contentPageName;
    }

    public void setContentPageName(String contentPageName) {
        this.contentPageName = contentPageName;
    }
}
