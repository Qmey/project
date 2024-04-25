import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import javax.swing.table.TableRowSorter;

public class SelectPrescribers {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SelectPrescribers::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("List of Prescribers");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 600);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        panel.add(searchPanel, BorderLayout.NORTH);

        JTable table = new JTable();
        table.setFillsViewportHeight(true);
        table.setModel(createNonEditableTableModel());

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/pharmacy_04_new", "postgres", "12345678");

            Statement statement = connection.createStatement();
            String query = "SELECT * FROM prescriber";
            ResultSet resultSet = statement.executeQuery(query);

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Clear existing columns
            model.setColumnCount(0);

            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                model.addColumn(metaData.getColumnName(columnIndex));
            }

            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    rowData[i] = resultSet.getObject(i + 1);
                }
                model.addRow(rowData);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }

        // Add listener to the search field
        searchField.addActionListener(e -> {
            String searchText = searchField.getText().trim().toLowerCase();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(model);
            table.setRowSorter(rowSorter);
            if (searchText.length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
            }
        });

        // Highlight search text in the table
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null && searchField.getText().trim().length() > 0) {
                    String text = value.toString().toLowerCase();
                    String searchText = searchField.getText().trim().toLowerCase();
                    if (text.contains(searchText)) {
                        c.setBackground(Color.YELLOW);
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static DefaultTableModel createNonEditableTableModel() {
        return new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
