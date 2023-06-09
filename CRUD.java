import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class CRUD extends JFrame {
    private JTextArea textArea;
    private JTextField textField;

    public CRUD() {
        setTitle("SQL using Java");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        textField = new JTextField();
        JButton submit = new JButton("Submit");
        textArea = new JTextArea();
        textArea.setEditable(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Enter Query: "), BorderLayout.WEST);
        topPanel.add(textField, BorderLayout.CENTER);
        topPanel.add(submit, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeQuery();
            }
        });
        setContentPane(mainPanel);
        setVisible(true);
    }

    private void executeQuery() {
        String url = "jdbc:postgresql://localhost/dvdrental";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "0812");

        try (Connection conn = DriverManager.getConnection(url, props);
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(textField.getText())) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int colNo = metaData.getColumnCount();

            StringBuilder resultBuilder = new StringBuilder();
            for (int i = 1; i <= colNo; i++) {
                resultBuilder.append(String.format("%-8s\t", metaData.getColumnName(i)));
            }
            resultBuilder.append("\n");

            while (resultSet.next()) {
                for (int i = 1; i <= colNo; i++) {
                    resultBuilder.append(String.format("%-8s\t", resultSet.getObject(i)));
                }
                resultBuilder.append("\n");
            }
            textArea.setText(resultBuilder.toString());

            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CRUD();
            }
   });
  }
}