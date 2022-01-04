import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class VaccineSystem extends JFrame implements ActionListener {

    enum PageName {
        LOGIN,
        HOME
    }

    private PageName pageName;
    private Page page;
    private String user, password;
    private JButton loginButton;

    public static void main(String[] args) {
        VaccineSystem vaccineSystem = new VaccineSystem("Vaccine System");

        final int WINDOW_WIDTH = 700;
        final int WINDOW_HEIGHT = 700;

        vaccineSystem.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        vaccineSystem.setLocationRelativeTo(null); // Sets window to centre of screen
        vaccineSystem.setVisible(true);
    }

    public VaccineSystem(String titleBarText) {
        super(titleBarText);
        page = new Page();
        pageName = PageName.LOGIN;
        updatePage();
    }

    private void updatePage() {
        switch (pageName) {
            case LOGIN:
                page = new LoginPage();
                loginButton = ((LoginPage) page).getSubmitButton();
                loginButton.addActionListener(this);
                break;
            case HOME:
                page = new Page();
                System.out.println("Home page");
                break;
        }
        this.add(page.getPanel());
    }

    private boolean connected() throws SQLException {
        try (Connection ignored = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306", user, password)) {
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            user = ((LoginPage) page).getUser();
            password = ((LoginPage) page).getPassword();

            try {
                if (connected()) {
                    pageName = PageName.HOME;
                    updatePage();
                }
                else {
                    String message = "Incorrect database login details";
                    String title = "Error";
                    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
                }
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
