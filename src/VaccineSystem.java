import javax.swing.*;
import java.awt.*;


public class VaccineSystem extends JFrame {

    private String pageName, user, password;
    CardLayout cardLayout;
    JPanel cards;

    public static void main(String[] args) {
        VaccineSystem vaccineSystem = new VaccineSystem("Vaccine System");
    }

    public VaccineSystem(String titleBarText) {
        super(titleBarText);

        configureWindow(700, 700);
        createInterface();
    }

    private void configureWindow(int width, int height) {
        this.setSize(width, height);
        this.setLocationRelativeTo(null); // Sets window to centre of screen
        this.setVisible(true);
    }

    private void createInterface() {
        cardLayout = new CardLayout();
         cards = new JPanel(cardLayout);

        LoginPage loginPage = new LoginPage(this);
        JPanel loginPanel = loginPage.getPanel();
        cards.add(loginPanel, "login");

        MainPage mainPage = new MainPage(this);
        JPanel mainPanel = mainPage.getPanel();
        cards.add(mainPanel, "main");

        cardLayout.last(cards);

        this.add(cards);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
