import javax.swing.*;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, MalformedFileException {
        SwingUtilities.invokeLater(() -> new GUI(new Model()));
    }
}
