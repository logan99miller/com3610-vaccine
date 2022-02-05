import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainPage extends Page {

    private String pageName;
    private JButton addPageButton, viewPageButton, logPageButton, mapPageButton, logoutButton;
    private CardLayout cardLayout;
    private JPanel cards;

    public MainPage(VaccineSystem vaccineSystem) {
        super(vaccineSystem);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(createNavPanel(), BorderLayout.NORTH);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
    }


    private JPanel createNavPanel() {
        JPanel panel = new JPanel();

        addPageButton = new JButton("Add");
        viewPageButton = new JButton("View");
        logPageButton = new JButton("Activity Log");
        mapPageButton = new JButton("Map View");
        logoutButton = new JButton("Log Out");

        buttons.add(addPageButton);
        buttons.add(viewPageButton);
        buttons.add(logPageButton);
        buttons.add(mapPageButton);
        buttons.add(logoutButton);

        for (JButton button : buttons) {
            addButton(button, panel);
        }

        return panel;
    }

    private JPanel createContentPanel() {
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        SelectAddPage selectAddPage = new SelectAddPage(vaccineSystem, this);
        SelectViewPage selectViewPage = new SelectViewPage(vaccineSystem, this);
        Page logPage = new Page(vaccineSystem);
        Page mapPage = new MapPage(vaccineSystem, this);

        JPanel addPanel = selectAddPage.getPanel();
        JPanel viewPanel = selectViewPage.getPanel();
        JPanel logPanel = logPage.getPanel();
        JPanel mapPanel = mapPage.getPanel();

        // Bad code practice, need to replace
        cards.add(addPanel, getSanitizedButtonText(addPageButton));
        cards.add(viewPanel, getSanitizedButtonText(viewPageButton));
        cards.add(logPanel, getSanitizedButtonText(logPageButton));
        cards.add(mapPanel, getSanitizedButtonText(mapPageButton));

        cardLayout.show(cards, "mapview");

        return cards;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logoutButton) {
            vaccineSystem.setPageName("login");
            vaccineSystem.updatePage();
        }
        else {
            for (JButton button : buttons) {
                if (e.getSource() == button) {
                    cardLayout.show(cards, getSanitizedButtonText(button));
                    button.setFont(button.getFont().deriveFont(Font.BOLD));
                }
                else {
                    button.setFont(button.getFont().deriveFont(Font.PLAIN));

                }
            }
        }
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void updatePage() {
        cardLayout.show(cards, pageName);
    }

    public void updatePageToComponent(JComponent component) {
        cards.add(component);
        cardLayout.last(cards);
    }
}
