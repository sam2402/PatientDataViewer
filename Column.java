import java.util.ArrayList;

public class Column {
    private final String name;
    private final ArrayList<String> rows = new ArrayList<>();

    public Column(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return rows.size();
    }

    public String getRowValue(int index) {
        try {
            return rows.get(index);
        } catch (IndexOutOfBoundsException err) {
            throw new IndexOutOfBoundsException("No such row with index " + index + " in row with name " + name);
        }
    }

    public void setRowValue(int index, String value) {
        try {
            rows.set(index, value);
        } catch (IndexOutOfBoundsException err) {
            throw new IndexOutOfBoundsException("No such row with index " + index + " in row with name " + name);
        }

    }
    public void addRowValue(String value) {
        rows.add(value);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(name).append(": ");
        for (String row: rows) {
            res.append(row).append(", ");
        }
        return res.toString();
    }

}
