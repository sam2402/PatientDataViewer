import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class GUI extends JFrame
{

    private JTable dataTable;
    private JPanel emptyPanel;
    private JScrollPane scrollPane;
    private JMenuBar menuBar;
    private JMenu file;
    private JTextField searchField;
    private JComboBox<Object> filterComboBox;
    private JLabel resultCountLabel;

    private final Model model;

    private TableRowSorter<Model> rowSorter;

    public GUI(Model model) {
        this.model = model;
        createGUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createGUI() {
        setTitle("Patient Data Viewer");
        createMenu();
        if (model.containsData()) {
            createDataView();
        } else {
            createEmptyView();
        }
    }

    private void createMenu() {
        menuBar = new JMenuBar();
        file = new JMenu("File");
        JMenuItem open = new JMenuItem("Open");
        open.addActionListener((ActionEvent e) -> loadFile());

        file.add(open);
        menuBar.add(file);
        if (model.containsData()) {
            createViewMenuBarItem();
            createSearchBar();
            createExportJSON();
        }

        setJMenuBar(menuBar);
    }

    private void createViewMenuBarItem() {
        JMenu view = new JMenu("View");
        for (int i = 0; i<model.getColumnCount(); i++) {
            JMenuItem item = new JCheckBoxMenuItem(model.getColumnName(i), true);
            int finalI = i;
            item.addActionListener(e -> {
                if (item.isSelected()) {
                    showColumn(finalI);
                } else {
                    hideColumn(finalI);
                }
            });
            view.add(item);
        }
        menuBar.add(view);
    }

    private void createSearchBar() {
        searchField = new JTextField("Search");

        searchField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) { searchTable(); }

            @Override
            public void removeUpdate(DocumentEvent e) { searchTable(); }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("This operation is not supported");
            }

        });
        menuBar.add(searchField);

        createSearchFieldOptions();

        resultCountLabel = new JLabel();
        menuBar.add(resultCountLabel);
    }

    private void createSearchFieldOptions() {
        ArrayList<String> searchFieldOptions = new ArrayList<>(model.getColumnNames());
        searchFieldOptions.add(0, "ALL");
        filterComboBox = new JComboBox<>(searchFieldOptions.toArray());
        filterComboBox.addActionListener(e -> searchTable());
        menuBar.add(filterComboBox);
    }

    private void searchTable() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            int[] columnsToSearch = model.getColumnIndex((String) Objects.requireNonNull(filterComboBox.getSelectedItem()));
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, columnsToSearch));
        }

        if (text.equals("")) {
            resultCountLabel.setText("");
        } else {
            int resultCount = dataTable.getRowCount();
            String resultText = resultCount == 1 ? " result " : " results ";
            resultCountLabel.setText(resultCount + resultText);
        }
    }

    private void createExportJSON() {
        JMenuItem exportJSON = new JMenuItem("Export JSON");
        exportJSON.addActionListener(e -> exportJSONFile());
        file.add(exportJSON);
    }

    private void showColumn(int columnIndex) {
        TableColumn column = model.showColumn(columnIndex);
        dataTable.addColumn(column);
        dataTable.moveColumn(dataTable.getColumnCount()-1, model.getGuiColumnIndex(columnIndex));
    }

    private void hideColumn(int columnIndex) {
        TableColumn column = dataTable.getColumnModel().getColumn(model.getGuiColumnIndex(columnIndex));
        dataTable.removeColumn(column);
        model.hideColumn(columnIndex, column);
    }

    private void createDataView() {
        dataTable = new JTable(model);
        dataTable.setAutoCreateRowSorter(true);
        rowSorter = new TableRowSorter<>(model);
        dataTable.setRowSorter(rowSorter);
        resizeColumnWidth(dataTable);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        scrollPane = new JScrollPane(dataTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);
    }

    private void createEmptyView() {
        emptyPanel = new JPanel(new BorderLayout());
        JLabel emptyNotificationLabel = new JLabel("Use \"File -> Open\" to choose a CSV or JSON file containing patient data to view", SwingConstants.CENTER);
        emptyPanel.add(emptyNotificationLabel);
        add(emptyPanel);
    }

    private void loadFile()
    {
        JFileChooser fileChooser = new JFileChooser(".");
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                model.loadDataAtLocation(file.getPath());
                makeDataTable();
            } catch (FileNotFoundException err) {
                JOptionPane.showMessageDialog(this, "Unable to load the file", "File Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (MalformedFileException err) {
                JOptionPane.showMessageDialog(this, "File not well formed", "File Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (UnsupportedFileException err) {
                JOptionPane.showMessageDialog(this,
                        "File type not supported. Please open a CSV or JSON file", "File Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void makeDataTable() {
        if (dataTable == null) {
            getContentPane().remove(emptyPanel);
        } else {
            getContentPane().remove(scrollPane);
        }
        createGUI();
        revalidate();
    }

    private void exportJSONFile() {

        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

            try {
                model.saveJSONFile(fileChooser.getSelectedFile().getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting JSON file", "File Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    // Source for this method: https://stackoverflow.com/a/17627497/4934861
    // I have slightly altered the version seen on StackOverflow
    private void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 30; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width +20 , width);
            }
            if(width > 300)
                width=300;
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

}