package Core;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    @Test
    void executeUpdate() throws SQLException {
        String statementText = "INSERT INTO TestOne (varOne, varTwo) VALUES (9, 60)";
        String URL = "jdbc:mysql://127.0.0.1:3306/vaccine_system";
        String user = "root";
        String password = "artstowerhas20";
        DatabaseManager.executeUpdate(statementText, URL, user, password);
    }

    @Test
    void executeSelect() throws SQLException {
        String statementText = "SELECT * FROM TestTwo";
        String[] columnNames = new String[] {"varOne"};
        String URL = "jdbc:mysql://127.0.0.1:3306/vaccine_system";
        String user = "root";
        String password = "artstowerhas20";

        HashMap<String, Object> subMap = new HashMap<>();
        subMap.put("varOne", 11);

        HashMap<String, HashMap<String, Object>> hashMap = new HashMap<>();
        hashMap.put("11", subMap);

        assertEquals(hashMap.toString(), DatabaseManager.executeSelect(statementText, columnNames, URL, user, password).toString());

        statementText = "SELECT * FROM TestTwo WHERE (varTwo = 11)";
        assertEquals(new HashMap<>(), DatabaseManager.executeSelect(statementText, columnNames, URL, user, password));
    }
}