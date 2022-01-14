import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainPage extends Page {

    private String pageName;
    private JButton addPageButton, editPageButton, logPageButton, mapPageButton, logoutButton;
    private CardLayout cardLayout;
    private JPanel cards;

    public MainPage(VaccineSystem vaccineSystem) {
        super(vaccineSystem);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(createNavPanel(), BorderLayout.NORTH);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);

//        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
//        setSize();
    }

//    private void setSize() {
//        final int BORDER = 10;
//        final int WIDTH = vaccineSystem.getWidth() - (BORDER * 2);
//        final int HEIGHT = vaccineSystem.getHeight() - (BORDER * 2);
//        System.out.println(WIDTH);
//        System.out.println(HEIGHT);
//        mainPanel.setSize(new Dimension(WIDTH, HEIGHT));
//    }

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
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        SelectAddPage selectAddPage = new SelectAddPage(vaccineSystem, this);
        SelectEditPage selectEditPage = new SelectEditPage(vaccineSystem, this);
        Page logPage = new Page(vaccineSystem);
        Page mapPage = new Page(vaccineSystem);

        JPanel addPanel = selectAddPage.getPanel();
        JPanel editPanel = selectEditPage.getPanel();
        JPanel logPanel = logPage.getPanel();
        JPanel mapPanel = mapPage.getPanel();

        cards.add(addPanel, getSanatizedtext(addPageButton));
        cards.add(editPanel, getSanatizedtext(editPageButton));
        cards.add(logPanel, getSanatizedtext(logPageButton));
        cards.add(mapPanel, getSanatizedtext(mapPageButton));

        cardLayout.show(cards, "add");

        return cards;
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
                    button.setFont(button.getFont().deriveFont(Font.BOLD));
                }
            }
            else {
                button.setFont(button.getFont().deriveFont(Font.PLAIN));
            }
        }
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void updatePage() {
        cardLayout.show(cards, pageName);
    }

    public JPanel getCards() {
        return cards;
    }

    public void setCards(JPanel cards) {
        this.cards = cards;
    }

    public void addCard(JComponent component, String name) {
        cards.add(component, name);
    }
}
