import javax.naming.NamingException;
import java.io.*;
import java.util.Scanner;

public class JSONReader extends DataLoader{

    public JSONReader(String fileName) {
        super(fileName);
    }

    // for each column name, find each occurrence of it on the left of a colon
    // then assign the data to the right of it to it's corresponding column
    protected void loadData() throws FileNotFoundException {

        for (String columnName : dataFrame.getColumnNames()) {
            fileScanner = new Scanner(new File(fileName));
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.contains("\""+columnName+"\"")) {
                    Scanner lineScanner = new Scanner(line);
                    lineScanner.useDelimiter("");
                    int quoteCount = 0;
                    StringBuilder data = new StringBuilder();
                    while (lineScanner.hasNext()) {
                        String token = lineScanner.next();
                        if (token.equals("\"")) {
                            quoteCount++;
                        }
                        if (quoteCount==3 && !token.equals("\"")) {
                            data.append(token);
                        }
                        if (quoteCount==4) {
                            dataFrame.addValue(columnName, data.toString());
                            break;
                        }
                    }
                }
            }
            fileScanner.close();
        }
    }

    // for the first object in the JSON file, get the column names by
    // counting the number of apostrophes seen on that line
    protected void loadColumnNames() throws NamingException {

        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            lineScanner.useDelimiter("");
            if (line.contains(":") && !line.contains("[")) {
                int quoteCount = 0;
                StringBuilder columnName = new StringBuilder();
                while (lineScanner.hasNext()) {
                    String token = lineScanner.next();
                    if (token.equals("\"")) {
                        quoteCount++;
                    }
                    if (quoteCount==1 && !token.equals("\"")) {
                        columnName.append(token);
                    }
                    if (quoteCount==2) {
                        validateColumnName(columnName.toString());
                        dataFrame.addColumn(new Column(columnName.toString()));
                        break;
                    }
                }
            }
            if (line.contains("}")) {
                break;
            }
        }

    }

}
