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

        JLabel idLabel = new JLabel("Pill ID:");
        idLabel.setForeground(Color.WHITE);
        idLabel.setBounds(20, 20, 100, 30);
        JTextField idField = new JTextField();
        idField.setBounds(120, 20, 100, 30);

        JLabel nameLabel = new JLabel("Pill Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(20, 60, 100, 30);
        JTextField nameField = new JTextField();
        nameField.setBounds(120, 60, 100, 30);

        JLabel manufacturerLabel = new JLabel("Manufacturer:");
        manufacturerLabel.setForeground(Color.WHITE);
        manufacturerLabel.setBounds(20, 100, 100, 30);
        JTextField manufacturerField = new JTextField();
        manufacturerField.setBounds(120, 100, 100, 30);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setBounds(20, 140, 100, 30);
        JTextField priceField = new JTextField();
        priceField.setBounds(120, 140, 100, 30);

        JLabel quantityLabel = new JLabel("Quantity in Stock:");
        quantityLabel.setForeground(Color.WHITE);
        quantityLabel.setBounds(20, 180, 120, 30);
        JTextField quantityField = new JTextField();
        quantityField.setBounds(150, 180, 100, 30);

        JLabel expiryLabel = new JLabel("Expiry Date:");
        expiryLabel.setForeground(Color.WHITE);
        expiryLabel.setBounds(20, 220, 100, 30);
        JTextField expiryField = new JTextField();
        expiryField.setBounds(120, 220, 100, 30);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(20, 260, 100, 30);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get data from text fields
                int pillId = Integer.parseInt(idField.getText());
                String pillName = nameField.getText();
                String manufacturer = manufacturerField.getText();
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                String expiryDate = expiryField.getText();
                submitData(pillId, pillName, manufacturer, price, quantity, expiryDate);
            }
        });

        panel.add(idLabel);
        panel.add(idField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(manufacturerLabel);
        panel.add(manufacturerField);
        panel.add(priceLabel);
        panel.add(priceField);
        panel.add(quantityLabel);
        panel.add(quantityField);
        panel.add(expiryLabel);
        panel.add(expiryField);
        panel.add(submitButton);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
        idField.addKeyListener(new EnterKeyListener(nameField));
        nameField.addKeyListener(new EnterKeyListener(manufacturerField));
        manufacturerField.addKeyListener(new EnterKeyListener(priceField));
        priceField.addKeyListener(new EnterKeyListener(quantityField));
        quantityField.addKeyListener(new EnterKeyListener(expiryField));
        expiryField.addKeyListener(new EnterKeyListener(submitButton));
    }
    private static void submitData(int pillId, String pillName, String manufacturer, double price, int quantity, String expiryDate) {

        String url = "jdbc:postgresql://localhost:5432/Pharmacy db";
        String username = "postgres";
        String password = "12345678";

        try {

            Connection connection = DriverManager.getConnection(url, username, password);

            String query = "INSERT INTO pills (pill_id, pill_name, pill_manufacturer, pill_price, pill_quantity_in_stock, expiry_date) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, pillId);
            preparedStatement.setString(2, pillName);
            preparedStatement.setString(3, manufacturer);
            preparedStatement.setDouble(4, price);
            preparedStatement.setInt(5, quantity);
            preparedStatement.setDate(6, Date.valueOf(expiryDate));

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
