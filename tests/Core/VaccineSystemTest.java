package Core;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class VaccineSystemTest {

    @Test
    void delete() throws SQLException {
        VaccineSystem vaccineSystem = new VaccineSystem("Title Text", false);

        String[] columnNames = new String[] {"varOne", "varTwo"};
        Object[] values = new Object[] {60, 10};
        String tableName = "TableTwo";

        vaccineSystem.insert(columnNames, values, tableName);
        vaccineSystem.delete("varOne", "10", "TestTwo");
    }

    @Test
    void insert() throws SQLException {
        VaccineSystem vaccineSystem = new VaccineSystem("Title Text", false);

        String[] columnNames = new String[] {"varOne", "varTwo"};
        Object[] values = new Object[] {60, 10};
        String tableName = "TableTwo";

        vaccineSystem.insert(columnNames, values, tableName);
    }

    @Test
    void update() throws SQLException {
        VaccineSystem vaccineSystem = new VaccineSystem("Title Text", false);

        String[] columnNames = new String[] {"varOne", "varTwo"};
        Object[] values = new Object[] {60, 10};
        String tableName = "TableTwo";

        vaccineSystem.insert(columnNames, values, tableName);

        values = new Object[] {5, 15};
        vaccineSystem.update(columnNames, values, tableName, "varTwo = 10");
    }

    @Test
    void select() throws SQLException {
        VaccineSystem vaccineSystem = new VaccineSystem("Title Text", false);

        String[] columnNames = new String[] {"varOne"};
        String tableName = "TestTwo";

        HashMap<String, Object> subMap = new HashMap<>();
        subMap.put("varOne", 11);

        HashMap<String, HashMap<String, Object>> hashMap = new HashMap<>();
        hashMap.put("11", subMap);

        assertEquals(hashMap.toString(), vaccineSystem.select(columnNames, tableName).toString());

        assertEquals(new HashMap<>(), vaccineSystem.select(columnNames, tableName, "varTwo = 719"));
    }

    @Test
    void updatePage() {
        VaccineSystem vaccineSystem = new VaccineSystem("Title Text", false);

        vaccineSystem.updatePage();
    }
}