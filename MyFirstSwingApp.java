import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class MyFirstSwingApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pharmacy Data Entry");
        frame.setSize(1500, 600);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    BufferedImage img = ImageIO.read(new File("background.jpg"));
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        panel.setLayout(null);

        JLabel idLabel = new JLabel("Medication ID:");
        idLabel.setForeground(Color.WHITE);
        idLabel.setBounds(20, 20, 100, 30);
        JTextField idField = new JTextField();
        idField.setBounds(120, 20, 100, 30);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(20, 60, 100, 30);
        JTextField nameField = new JTextField();
        nameField.setBounds(120, 60, 100, 30);

        JLabel manufacturerLabel = new JLabel("Manufacturer:");
        manufacturerLabel.setForeground(Color.WHITE);
        manufacturerLabel.setBounds(20, 100, 100, 30);
        JTextField manufacturerField = new JTextField();
        manufacturerField.setBounds(120, 100, 100, 30);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setForeground(Color.WHITE);
        descriptionLabel.setBounds(20, 140, 100, 30);
        JTextField descriptionField = new JTextField();
        descriptionField.setBounds(120, 140, 100, 30);

        JLabel strengthLabel = new JLabel("Strength:");
        strengthLabel.setForeground(Color.WHITE);
        strengthLabel.setBounds(20, 180, 100, 30);
        JTextField strengthField = new JTextField();
        strengthField.setBounds(120, 180, 100, 30);

        JLabel dosageFormLabel = new JLabel("Dosage Form:");
        dosageFormLabel.setForeground(Color.WHITE);
        dosageFormLabel.setBounds(20, 220, 100, 30);
        JTextField dosageFormField = new JTextField();
        dosageFormField.setBounds(120, 220, 100, 30);

        JButton submitButton = new JButton("Submit");
        JButton backButton = new JButton("Back");

        submitButton.setBounds(20, 260, 100, 30);
        backButton.setBounds(130, 260, 100, 30);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int medicationId = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                String manufacturer = manufacturerField.getText();
                String description = descriptionField.getText();
                String strength = strengthField.getText();
                String dosageForm = dosageFormField.getText();
                submitData(medicationId, name, manufacturer, description, strength, dosageForm);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runAnotherJavaFile();
                frame.dispose();
            }
        });

        panel.add(idLabel);
        panel.add(idField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(manufacturerLabel);
        panel.add(manufacturerField);
        panel.add(descriptionLabel);
        panel.add(descriptionField);
        panel.add(strengthLabel);
        panel.add(strengthField);
        panel.add(dosageFormLabel);
        panel.add(dosageFormField);
        panel.add(submitButton);
        panel.add(backButton);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        idField.addKeyListener(new EnterKeyListener(nameField));
        nameField.addKeyListener(new EnterKeyListener(manufacturerField));
        manufacturerField.addKeyListener(new EnterKeyListener(descriptionField));
        descriptionField.addKeyListener(new EnterKeyListener(strengthField));
        strengthField.addKeyListener(new EnterKeyListener(dosageFormField));
        dosageFormField.addKeyListener(new EnterKeyListener(submitButton));
    }

    private static void submitData(int medicationId, String name, String manufacturer, String description, String strength, String dosageForm) {
        String url = "jdbc:postgresql://localhost:5432/pharmacy_04_new";
        String username = "postgres";
        String password = "12345678";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);

            String query = "INSERT INTO Medication (MedicationID, Name, Description, Manufacturer, Strength, DosageForm) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, medicationId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, manufacturer);
            preparedStatement.setString(5, strength);
            preparedStatement.setString(6, dosageForm);

            int rowsAffected = preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Data inserted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to insert data.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    private static void runAnotherJavaFile() {
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
}

class EnterKeyListener extends KeyAdapter {
    private final Object objectToClick;

    EnterKeyListener(Object objectToClick) {
        this.objectToClick = objectToClick;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (objectToClick instanceof JButton) {
                ((JButton) objectToClick).doClick();
            } else if (objectToClick instanceof JTextField) {
                ((JTextField) objectToClick).transferFocus();
            }
        }
    }
}
