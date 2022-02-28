package UserInterface.AddPages;

public class Insert {
    private String[] columnNames;
    private Object[] values;
    private String tableName;

    public Insert(String[] columnNames, Object[] values, String tableName) {
        this.columnNames = columnNames;
        this.values = values;
        this.tableName = tableName;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public Object[] getValues() {
        return values;
    }

    public String getTableName() {
        return tableName;
    }
}