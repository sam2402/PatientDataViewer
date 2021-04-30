import java.util.ArrayList;

public class DataFrame {
    private final ArrayList<Column> columns = new ArrayList<>();

    public void addColumn(Column column) {
        columns.add(column);
    }

    public ArrayList<String> getColumnNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Column column: columns) {
            names.add(column.getName());
        }
        return names;
    }

    public int getRowCount() {

        if (columns.size() > 0) {
            return columns.get(0).getSize();
        } else {
            throw new RuntimeException("There are no columns in this DataFrame instance");
        }
    }

    public int getColumnCount() {
        return columns.size();
    }

    public String getValue(String columnName, int row) {
        for (Column column: columns) {
           if (column.getName().equals(columnName)) {
               try {
                   return column.getRowValue(row);
               } catch (IndexOutOfBoundsException err) {
                   throw new IndexOutOfBoundsException("No such index " + row + " in column with name " + columnName);
               }
            }
        }
        throw new RuntimeException("There is no such column with name " + columnName);
    }

    public void putValue(String columnName, int row, String value) {
        for (Column column: columns) {
            if (column.getName().equals(columnName)) {
                try {
                    column.setRowValue(row, value);
                    return;
                } catch (IndexOutOfBoundsException err) {
                    throw new IndexOutOfBoundsException("No such index " + row + " in column with name " + columnName);
                }
            }
        }
        throw new RuntimeException("There is no such column with name " + columnName);
    }

    public void addValue(String columnName, String value) {
        for (Column column: columns) {
            if (column.getName().equals(columnName)) {
               column.addRowValue(value);
               return;
            }
        }
        throw new RuntimeException("There is no such column with name " + columnName);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int row = 0; row<getRowCount(); row++) {
            for (Column column: columns) {
                res.append(column.getRowValue(row)).append("  ");
            }
            res.append("\n");
        }
        return res.toString();
    }
}
