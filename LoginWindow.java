import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginWindow extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;

    public LoginWindow() {
        super("Login");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 190);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        panel.add(new JLabel("Login:"));
        loginField = new JTextField();
        panel.add(loginField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticate(login, password)) {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Login successful!");
                    // Run another Java file
                    runAnotherJavaFile();
                } else {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Invalid login or password. Please try again.");
                }
            }
        });
        panel.add(loginButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        panel.add(exitButton);

        add(panel);
    }

    private boolean authenticate(String login, String password) {
        String url = "jdbc:postgresql://localhost:5432/pharmacy_04_new";
        String username = "postgres";
        String dbPassword = "12345678";

        try (Connection connection = DriverManager.getConnection(url, username, dbPassword)) {
            String query = "SELECT * FROM login WHERE login = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(login)); // Assuming login is an integer
            statement.setInt(2, Integer.parseInt(password)); // Assuming password is an integer
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            return false;
        }
    }

    private void runAnotherJavaFile() {
        try {
            // Load the class dynamically
            Class<?> clazz = Class.forName("WindowMenuWithBackground");

            // Get the main method of the class
            java.lang.reflect.Method method = clazz.getMethod("main", String[].class);

            // Invoke the main method
            method.invoke(null, (Object) new String[0]);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginWindow().setVisible(true);
            }
        });
    }
}
