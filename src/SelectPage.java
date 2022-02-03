import javax.swing.*;

public class SelectPage extends Page {

    protected MainPage mainPage;

    public SelectPage(VaccineSystem vaccineSystem, MainPage mainPage, String buttonAction) {
        super(vaccineSystem);
        this.mainPage = mainPage;
        this.buttonAction = buttonAction;

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel pageTitle = new JLabel(buttonAction + ":");
        mainPanel.add(pageTitle);
    }
}
