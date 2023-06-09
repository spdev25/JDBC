import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class QueryCRUD extends JFrame {

    private JComboBox<String> queryComboBox;
    private JTextField customQueryTextField;
    private JTextArea resultTextArea;
    private Connection connection;

    public QueryCRUD() {
        initializeUI();
        initializeDatabaseConnection();
    }

    private void initializeUI() {
        setTitle("Books Query Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        queryComboBox = new JComboBox<>();
        queryComboBox.addItem("Select all authors from the Authors table");
        queryComboBox.addItem("Select a specific author and list all books for that author");
        queryComboBox.addItem("Select a specific title and list all authors for that title");
        queryComboBox.addItem("Custom Query");
        queryComboBox.addActionListener(new ComboBoxActionListener());

        customQueryTextField = new JTextField(30);
        customQueryTextField.setEnabled(false);
        customQueryTextField.addActionListener(new CustomQueryActionListener());

        resultTextArea = new JTextArea(10, 40);
        resultTextArea.setEditable(false);

        add(new JLabel("Predefined Queries:"));
        add(queryComboBox);
        add(customQueryTextField);
        add(new JScrollPane(resultTextArea));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeDatabaseConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost/dvdrental", "postgres", "0812");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to the database.");
            System.exit(1);
        }
    }

    private void executeQuery(String query) {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            StringBuilder resultBuilder = new StringBuilder();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    resultBuilder.append(resultSet.getString(i)).append("\t");
                }
                resultBuilder.append("\n");
            }

            resultTextArea.setText(resultBuilder.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to execute the query.");
        }
    }

    private class ComboBoxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedQuery = (String) queryComboBox.getSelectedItem();
            assert selectedQuery != null;
            if (selectedQuery.equals("Custom Query")) {
                customQueryTextField.setEnabled(true);
            } else {
                customQueryTextField.setEnabled(false);
                customQueryTextField.setText("");
                executePredefinedQuery(selectedQuery);
            }
        }
    }

    private class CustomQueryActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String customQuery = customQueryTextField.getText();
            executeQuery(customQuery);
        }
    }

    private void executePredefinedQuery(String query) {
        switch (query) {
            case "Select all authors from the Authors table":
                executeQuery("SELECT * FROM authors ORDER BY last_name, first_name");
                break;
            case "Select a specific author and list all books for that author":
                String authorName = JOptionPane.showInputDialog(null, "Enter author's name (last name, first name):");
                executeQuery("SELECT b.title, b.year, b.isbn FROM books b " +
                        "JOIN authors a ON b.author_id = a.author_id " +
                        "WHERE CONCAT(a.last_name, ', ', a.first_name) = '" + authorName + "' " +
                        "ORDER BY a.last_name, a.first_name");
                break;
            case "Select a specific title and list all authors for that title":
                String bookTitle = JOptionPane.showInputDialog(null, "Enter book title:");
                executeQuery("SELECT a.last_name, a.first_name FROM authors a " +
                        "JOIN books b ON a.author_id = b.author_id " +
                        "WHERE b.title = '" + bookTitle + "' " +
                        "ORDER BY a.last_name, a.first_name");
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QueryCRUD::new);
    }
}
