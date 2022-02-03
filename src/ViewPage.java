import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewPage extends Page {

    public ViewPage(VaccineSystem vaccineSystem, MainPage mainPage, String title, String[] headings, String[] columnNames,
        Object[] references, String tableName, String where) {

        super(vaccineSystem);

        mainPanel.add(new JLabel(title));
        mainPanel.add(createTablePanel(mainPage, headings, columnNames, references, tableName, where));

        setMaxWidthMinHeight(mainPanel);
    }

    public ViewPage(VaccineSystem vaccineSystem, MainPage mainPage, String title, String[] headings, String[] columnNames,
        Object[] references, String tableName) {

        this(vaccineSystem, mainPage, title, headings, columnNames, references, tableName, null);
    }

    public ViewPage(VaccineSystem vaccineSystem, MainPage mainPage, String title, String[] headings, String[] columnNames,
        String tableName, String where) {

        this(vaccineSystem, mainPage, title, headings, columnNames, new Object[] {}, tableName, where);
    }

    public ViewPage(VaccineSystem vaccineSystem, MainPage mainPage, String title, String[] headings, String[] columnNames,
        String tableName) {

        this(vaccineSystem, mainPage, title, headings, columnNames, new Object[] {}, tableName, null);
    }

    private String[] addElement(String element, String[] array) {
        String[] newArray = new String[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = "Delete";
        return newArray;
    }

    private void addHeadings(String[] headings, JPanel tablePanel) {
        headings = addElement("Delete", headings);

        for (String heading : headings) {
            JLabel header = new JLabel(heading);
            header.setFont(header.getFont().deriveFont(Font.BOLD));
            tablePanel.add(header);
        }
    }

    private String createWhere(HashMap<String, Object> reference, String id) {
        String where = "(" + reference.get("idFieldName") + " = " + id + ")";

        String linkerTable = (String) reference.get("linkerTableName");
        String linkerIdFieldName = (String) reference.get("linkerIdFieldName");

        if ((linkerTable != null) && (linkerIdFieldName != null)) {
            try {
                ArrayList<ArrayList<String>> records = vaccineSystem.executeSelect(new String[]{linkerIdFieldName}, linkerTable, where);
                return "(" + reference.get("linkerIdFieldName") + " = " + records.get(0).get(0) + ")";
            }
            catch (SQLException e) {}
        }
        return where;
    }

    private void addReferenceButton(MainPage mainPage, HashMap<String, Object> reference, String id, JPanel tablePanel) {
        JButton button = new JButton();

        if (reference.containsKey("buttonText")) {
            button.setText((String) reference.get("buttonText"));
        }
        else {
            button.setText(id);
        }

        button.addActionListener(e -> {

            ViewPage viewPage;

            String where = createWhere(reference, id);

            try {
                viewPage = new ViewPage(
                    vaccineSystem,
                    mainPage,
                    (String) reference.get("title"),
                    (String[]) reference.get("headings"),
                    (String[]) reference.get("columnNames"),
                    (Object[]) reference.get("references"),
                    (String) reference.get("tableName"),
                    where);
            } catch (Exception ex) {
                viewPage = new ViewPage(
                    vaccineSystem,
                    mainPage,
                    (String) reference.get("title"),
                    (String[]) reference.get("headings"),
                    (String[]) reference.get("columnNames"),
                    (String) reference.get("tableName"),
                    where);
            }

            JFrame frame = new JFrame();
            frame.add(viewPage.getPanel());

            createPopupFrame(frame, viewPage.getPanel(), 800, 500);
        });

        tablePanel.add(button);
    }

    private void addTableContents(MainPage mainPage, ArrayList<ArrayList<String>> contents, Object[] references, JPanel tablePanel) {
        for (ArrayList<String> row : contents) {
            for (int i = 0; i < row.size() + 1; i ++) {

                if (i > row.size() - 1) {
                    JButton button = new JButton("hello");
                    tablePanel.add(button);
                }
                else {
                    boolean isReference = false;
                    for (Object referenceObject : references) {
                        HashMap<String, Object> reference = (HashMap<String, Object>) referenceObject;

                        if (reference.get("columnNumber").equals(i)) {
                            addReferenceButton(mainPage, reference, row.get(i), tablePanel);
                            isReference = true;
                        }
                    }
                    if (!isReference) {
                        tablePanel.add(new JLabel(row.get(i)));
                    }
                }
            }
        }
    }

    private JPanel createTablePanel(MainPage mainPage, String[] headings, String[] columnNames, Object[] references, String tableName, String where) {
        JPanel tablePanel = new JPanel(new GridLayout(0, columnNames.length + 1));
        addHeadings(headings, tablePanel);

        for (int i = 0; i < columnNames.length; i++) {

            for (Object referenceObject : references) {
                HashMap<String, Object> reference = (HashMap<String, Object>) referenceObject;

                if (reference.get("heading").equals(headings[i])) {
                    reference.put("columnNumber", i);
                }
            }
        }

        try {
            ArrayList<ArrayList<String>> tableContents;
            if (where == null) {
                tableContents = vaccineSystem.executeSelect(columnNames, tableName);
            }
            else {
                tableContents = vaccineSystem.executeSelect(columnNames, tableName, where);
            }

            addTableContents(mainPage, tableContents, references, tablePanel);
        }
        catch (SQLException ignored) {}

        setMaxWidthMinHeight(tablePanel);
        return tablePanel;
    }

}
