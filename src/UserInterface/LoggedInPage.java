/**
 * Page the user sees when they have logged in. Uses a card layout to switch between different pages (the map page,
 * activity log page, simulation page, select add page, select view page and various add and view pages). The different pages
 * are displayed in a content panel.
 * This page also contains a nav panel with buttons that access the different pages available to a logged-in user.
 */
package UserInterface;

import Core.VaccineSystem;
import UserInterface.Map.MapPage;
import UserInterface.SelectPages.SelectAddPage;
import UserInterface.SelectPages.SelectViewPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoggedInPage extends Page {

    private String pageName;
    private JButton addPageButton, viewPageButton, logPageButton, mapPageButton, logoutButton, simulationPageButton;
    private ActivityLogPage activityLogPage;
    private MapPage mapPage;
    private SimulationPage simulationPage;
    private CardLayout cardLayout;
    private JPanel navPanel, cards;

    public LoggedInPage(VaccineSystem vaccineSystem) {
        super(vaccineSystem);

        navPanel = createNavPanel();

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(navPanel, BorderLayout.NORTH);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
    }


    /**
     * Panel at the top of the page which allows the user to navigate between the main pages through button presses.
     * @return
     */
    private JPanel createNavPanel() {
        JPanel panel = new JPanel();

        addPageButton = new JButton("Add");
        viewPageButton = new JButton("View");
        logPageButton = new JButton("Activity Log");
        mapPageButton = new JButton("Map View");
        simulationPageButton = new JButton("Simulation");
        logoutButton = new JButton("Log Out");

        buttons.add(addPageButton);
        buttons.add(viewPageButton);
        buttons.add(logPageButton);
        buttons.add(mapPageButton);
        buttons.add(simulationPageButton);
        buttons.add(logoutButton);

        for (JButton button : buttons) {
            addButton(button, panel);
        }
        return panel;
    }

    /**
     * Panel displayed below the nav panel. The content will change depending on the current card being displayed
     * @return
     */
    private JPanel createContentPanel() {
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        SelectAddPage selectAddPage = new SelectAddPage(vaccineSystem, this);
        SelectViewPage selectViewPage = new SelectViewPage(vaccineSystem, this);

        activityLogPage = new ActivityLogPage(vaccineSystem);
        mapPage = new MapPage(vaccineSystem);
        simulationPage = new SimulationPage(vaccineSystem);

        JPanel addPanel = selectAddPage.getPanel();
        JPanel viewPanel = selectViewPage.getPanel();
        JPanel logPanel = activityLogPage.getPanel();
        JPanel mapPanel = mapPage.getPanel();
        JPanel simulationPanel = simulationPage.getPanel();

        // Each panel represented by the button's text. When a button is pressed we look at the button's text to determine
        // what page the button should cause to be displayed
        cards.add(addPanel, getSanitizedButtonText(addPageButton));
        cards.add(viewPanel, getSanitizedButtonText(viewPageButton));
        cards.add(logPanel, getSanitizedButtonText(logPageButton));
        cards.add(mapPanel, getSanitizedButtonText(mapPageButton));
        cards.add(simulationPanel, getSanitizedButtonText(simulationPageButton));

        // Initial card when the user logs in
        cardLayout.show(cards, "mapview");

        return cards;
    }

    /**
     * Called every time the system updates (specified by the updateRate in VaccineSystem) to automatically update the activity
     * log and map pages. View pages are not automatically updated as they cause the system to lag as there is a lot of them
     */
    public void autoRefresh() {
        activityLogPage.refreshPage();
        mapPage.refreshPage();
    }

    /**
     * Removes the button's text without any spaces and in all lower case. Used in the nav panel buttons as the cards
     * are referred to by the associated button's text
     * @param button the button to be sanitized
     * @return the text without any spaces or upper cases letters
     */
    protected String getSanitizedButtonText(JButton button) {
        return button.getText().replace(" ", "").toLowerCase();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logoutButton) {
            System.exit(0);
        }

        // Navigation panel buttons
        else {
            for (JButton button : buttons) {
                if (e.getSource() == button) {
                    cardLayout.show(cards, getSanitizedButtonText(button));

                    // Show what page is being displayed by setting the button's text to bold
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

    /**
     * Used by the SelectViewPage and SelectAddPage to change to content panel to the required add or view page
     * @param component
     */
    public void updatePageToComponent(JComponent component) {
        cards.add(component);
        cardLayout.last(cards);
    }
}