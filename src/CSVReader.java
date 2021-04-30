import javax.naming.NamingException;
import java.util.Scanner;

public class CSVReader extends DataLoader {

    public CSVReader(String fileName) {
        super(fileName);
    }

    protected void loadData() {

        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            lineScanner.useDelimiter(",");
            int colIndex = 0;
            while (lineScanner.hasNext()) {
                String token = lineScanner.next();
                dataFrame.addValue(dataFrame.getColumnNames().get(colIndex), token);
                colIndex = (colIndex + 1) % dataFrame.getColumnCount();
            }
            lineScanner.close();
            if (line.endsWith(",")) {
                String lastColumnName = dataFrame.getColumnNames().get(dataFrame.getColumnNames().size()-1);
                dataFrame.addValue(lastColumnName, "");
            }
        }
        fileScanner.close();
    }

    protected void loadColumnNames() throws NamingException {
        if (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            lineScanner.useDelimiter(",");
            while (lineScanner.hasNext()) {
                String token = lineScanner.next();
                validateColumnName(token);
                dataFrame.addColumn(new Column(token));
            }
            lineScanner.close();
        }

    }

}
