import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;


public class VaccineSystem extends JFrame {

    final private String URL = "jdbc:mysql://127.0.0.1:3306/vaccine_system";
    private String pageName, user, password;
    private CardLayout cardLayout;
    private JPanel cards;

    public static void main(String[] args) {
        new VaccineSystem("Vaccine System");
    }

    public VaccineSystem(String titleBarText) {
        super(titleBarText);

        user = "root";
        password = "";

        configureWindow();
        createInterface();
    }

    private void configureWindow() {
        this.setSize(1000, 700);
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

    public void executeUpdate(String statementText) throws SQLException {
        Connection connection;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(URL, user, password);
            statement = connection.createStatement();
            System.out.println(statementText);
            statement.executeUpdate(statementText);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public ArrayList<ArrayList<String>> executeSelect(String[] columnLabels, String tableName, String where) throws SQLException {
        Connection connection = null;
        Statement statement;

        ArrayList<ArrayList<String>> res = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        String statementText = "SELECT ";
        for (String columnLabel : columnLabels) {
            statementText += columnLabel + ", ";
        }
        statementText = statementText.substring(0, statementText.length() - 2);
        statementText += " FROM " + tableName;

        if (where != null) {
            statementText += " WHERE " + where;
        }

        System.out.println(statementText);
        try {
            connection = DriverManager.getConnection(URL, user, password);
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(statementText);
            while(resultSet.next()) {
                for (String columnLabel : columnLabels) {
                    values.add(resultSet.getString(columnLabel));
                }
                res.add(values);
                values = new ArrayList<>();
            }
            resultSet.close();
            return res;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    public ArrayList<ArrayList<String>> executeSelect(String[] columnLabels, String tableName) throws SQLException {
        return executeSelect(columnLabels, tableName, null);
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void updatePage() {
        cardLayout.show(cards, pageName);
    }

    public String getURL() {
        return URL;
    }
}
