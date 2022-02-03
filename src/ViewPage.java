import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewPage extends Page {

    private MainPage mainPage;
    private String[] headings;
    private String[] columnNames;
    private Object[] references;
    private String tableName;
    private String where;
    private boolean deleteOption;

    public ViewPage(VaccineSystem vaccineSystem, MainPage mainPage, String title, String[] headings, String[] columnNames,
        Object[] references, String tableName, boolean deleteOption, String where) {
        super(vaccineSystem);

        this.mainPage = mainPage;
        this.headings = headings;
        this.columnNames = columnNames;
        this.references = references;
        this.tableName = tableName;
        this.deleteOption = deleteOption;
        this.where = where;

        mainPanel.add(new JLabel(title));
        mainPanel.add(createTablePanel());

        setMaxWidthMinHeight(mainPanel);
    }

    public ViewPage(VaccineSystem vaccineSystem, MainPage mainPage, String title, String[] headings, String[] columnNames,
        Object[] references, String tableName, boolean deleteOption) {
        this(vaccineSystem, mainPage, title, headings, columnNames, references, tableName, deleteOption, null);
    }

    private String[] addElement(String element, String[] array) {
        String[] newArray = new String[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = element;
        return newArray;
    }

    private void addHeadings(JPanel tablePanel) {

        if (deleteOption) {
            headings = addElement("Delete", headings);
        }

        for (String heading : headings) {
            JLabel header = new JLabel(heading);
            header.setFont(header.getFont().deriveFont(Font.BOLD));
            tablePanel.add(header);
        }
    }

    private String createWhere(HashMap<String, Object> reference, String id) {
        String where = "(" + reference.get("IDFieldName") + " = " + id + ")";

        String linkerTable = (String) reference.get("linkerTableName");
        String linkerIDFieldName = (String) reference.get("linkerIDFieldName");

        if ((linkerTable != null) && (linkerIDFieldName != null)) {
            try {
                ArrayList<ArrayList<String>> records = vaccineSystem.executeSelect(new String[]{linkerIDFieldName}, linkerTable, where);
                return "(" + reference.get("linkerIDFieldName") + " = " + records.get(0).get(0) + ")";
            }
            catch (SQLException e) {}
        }
        return where;
    }

    private void addReferenceButton(HashMap<String, Object> reference, String id, JPanel tablePanel) {
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

            if (!reference.containsKey("references")) {
                reference.put("references", null);
            }

            viewPage = new ViewPage(
                vaccineSystem, mainPage,
                (String) reference.get("title"),
                (String[]) reference.get("headings"),
                (String[]) reference.get("columnNames"),
                (Object[]) reference.get("references"),
                (String) reference.get("tableName"),
                false, where);

            JFrame frame = new JFrame();
            frame.add(viewPage.getPanel());

            createPopupFrame(frame, viewPage.getPanel(), 800, 500);
        });

        tablePanel.add(button);
    }

    private void deleteRow(String tableName, String IDFieldName, String ID) {
        try {
            String statementText = "DELETE FROM " + tableName + " WHERE " + IDFieldName +" = " + ID;
            vaccineSystem.executeUpdate(statementText);
        }
        catch (Exception e) {
            errorMessage("ex");
        }
    }

    private void addTableContents(ArrayList<ArrayList<String>> contents, JPanel tablePanel) {
        for (ArrayList<String> row : contents) {
            for (int i = 0; i < row.size() + 1; i ++) {

                if (i > row.size() - 1) {
                    if (deleteOption) {
                        JButton button = new JButton("X");
                        button.addActionListener(e -> { deleteRow(tableName, columnNames[0], row.get(0)); });
                        tablePanel.add(button);
                    }
                }
                else {
                    boolean isReference = false;

                    if (references != null) {
                        for (Object referenceObject : references) {
                            HashMap<String, Object> reference = (HashMap<String, Object>) referenceObject;

                            if (reference.get("columnNumber").equals(i)) {
                                addReferenceButton(reference, row.get(i), tablePanel);
                                isReference = true;
                            }
                        }
                    }
                    if (!isReference) {
                        tablePanel.add(new JLabel(row.get(i)));
                    }
                }
            }
        }
    }

    private JPanel createTablePanel() {

        JPanel tablePanel = new JPanel();
        if (deleteOption) {
            tablePanel.setLayout(new GridLayout(0, columnNames.length + 1));
        }
        else {
            tablePanel.setLayout(new GridLayout(0, columnNames.length));
        }
        addHeadings(tablePanel);

        for (int i = 0; i < columnNames.length; i++) {

            if (references != null) {
                for (Object referenceObject : references) {
                    HashMap<String, Object> reference = (HashMap<String, Object>) referenceObject;

                    if (reference.get("heading").equals(headings[i])) {
                        reference.put("columnNumber", i);
                    }
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

            addTableContents(tableContents, tablePanel);
        }
        catch (SQLException ignored) {}

        setMaxWidthMinHeight(tablePanel);
        return tablePanel;
    }
}
