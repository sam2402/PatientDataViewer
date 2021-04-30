import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Model extends AbstractTableModel {

    private DataFrame dataFrame;
    private final TreeMap<Integer, TableColumn> hiddenColumns = new TreeMap<>();

    public void loadDataAtLocation(String path) throws FileNotFoundException, MalformedFileException, UnsupportedFileException {
        DataLoader dataLoader;
        if (path.endsWith(".csv")) {
            dataLoader = new CSVReader(path);
        } else if (path.endsWith(".json")) {
            dataLoader = new JSONReader(path);
        } else {
            throw new UnsupportedFileException("File type not supported");
        }
        dataFrame = dataLoader.getDataFrame();
        fireTableStructureChanged();
        fireTableDataChanged();
    }

    public boolean containsData() {
        return dataFrame != null;
    }

    public void hideColumn(int colIndex, TableColumn tableColumn) {
        hiddenColumns.put(colIndex, tableColumn);
    }

    public TableColumn showColumn(int colIndex) {
        return hiddenColumns.remove(colIndex);
    }

    // the index of a column in the GUI is equal to its natural index, minus the number of
    // hidden columns with a natural index less than the column being queried
    public int getGuiColumnIndex(int colIndex) {
        Map<Integer, TableColumn> result = hiddenColumns.entrySet()
                .stream()
                .filter(map -> map.getKey() < colIndex)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return colIndex-result.size();
    }

    // return a list of all visible columns if "ALL" is selected or the GUI index of the selected column
    public int[] getColumnIndex(String columnName) {
        if (columnName.equals("ALL")) {
            return integerRange(getColumnCount()-1-hiddenColumns.size());
        }
        return new int[]{getGuiColumnIndex(dataFrame.getColumnNames().indexOf(columnName))};
    }

    @Override
    public int getRowCount() {
        return dataFrame.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return dataFrame.getColumnCount();
    }

    public ArrayList<String> getColumnNames() { return dataFrame.getColumnNames(); }

    public String getColumnName(int columnIndex) {
        return dataFrame.getColumnNames().get(columnIndex);
    }

    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String columnName = getColumnName(columnIndex);
        return dataFrame.getValue(columnName, rowIndex);
    }

    public void saveJSONFile(String path) throws IOException {
        String fullPath = path.endsWith(".json") ? path : path+".json";
        FileWriter myWriter = new FileWriter(fullPath);
        myWriter.write(getJSONString());
        myWriter.close();
    }

    // generate a JSON string from the dataFrame
    private String getJSONString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n" +
                "\t\"patients\": [");
        for (int row = 0; row<getRowCount(); row++) {
            stringBuilder.append("\n\t\t{\n");
            for (int col = 0; col<getColumnCount(); col++) {
                String columnName = getColumnName(col);
                String cellData = (String) getValueAt(row, col);
                stringBuilder.append("\t\t\t");
                stringBuilder.append("\"").append(columnName).append("\": \"").append(cellData).append("\"");
                if (col != getColumnCount()-1) {
                    stringBuilder.append(",");
                }
                stringBuilder.append("\n");
            }
            stringBuilder.append("\t\t}");
            if (row != getRowCount()-1) {
                stringBuilder.append(",");
            }
        }

        stringBuilder.append("\n\n\t]\n}");


        return stringBuilder.toString();
    }

    private int[] integerRange(int x) {
        int[] res = new int[x +1];
        for (int i = 0; i<= x; i++) {
            res[i] = i;
        }
        return res;
    }
}
