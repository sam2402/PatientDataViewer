import javax.naming.NamingException;
import java.io.*;
import java.util.Scanner;

abstract class DataLoader {

    protected final DataFrame dataFrame = new DataFrame();
    protected Scanner fileScanner;

    protected boolean fileExists;
    protected boolean fileWellFormatted;
    protected boolean fileTypeSupported;
    protected final String fileName;

    public DataLoader(String fileName) {
        this.fileName = fileName;
        try {
            fileScanner = new Scanner(new File(fileName));
            loadColumnNames();
            loadData();
            fileExists = true;
            fileWellFormatted = true;
            fileTypeSupported = true;
        } catch (FileNotFoundException err) {
            fileExists = false;
        } catch (NamingException err) {
            fileWellFormatted = false;
        }
    }

    public DataFrame getDataFrame() throws FileNotFoundException, MalformedFileException, UnsupportedFileException {
        if (!fileExists) {
            throw new FileNotFoundException("File does not exist");
        } else if (!fileWellFormatted) {
            throw new MalformedFileException("File is not well formatted");
        } else if (!fileTypeSupported) {
            throw new UnsupportedFileException("File type not supported");
        } else {
            return dataFrame;
        }
    }

    protected abstract void loadData() throws FileNotFoundException ;

    protected abstract void loadColumnNames() throws NamingException ;

    protected void validateColumnName(String name) throws NamingException {
        if (dataFrame.getColumnNames().contains(name)) {
            throw new NamingException("Column with name " + name + " already exists");
        }
    }

}
