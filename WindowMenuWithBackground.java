import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowMenuWithBackground extends JFrame {
    private JFrame frame;

    public WindowMenuWithBackground(JFrame frame) {
        super("Menu");
        this.frame = frame;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        // Create panel with background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image backgroundImage = new ImageIcon("background.jpg").getImage();
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        backgroundPanel.setLayout(new GridLayout(5, 1, 10, 10));
        add(backgroundPanel);

        // Create buttons for running Java files
        String[] fileNames = {"MyFirstSwingApp.java", "SelectPatients.java", "SelectMedications.java", "MedicationListWindow.java", "SelectMedications.java"};
        String[] buttonLabels = {"First Swing App", "Select Patients", "Select Medications", "Medication List Window", "Medication List Window v2"};
        for (int i = 0; i < fileNames.length; i++) {
            JButton button = new JButton(buttonLabels[i]);
            button.setPreferredSize(new Dimension(200, 50)); // Set preferred size for buttons
            String fileName = fileNames[i];
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    runJavaFile(fileName);
                    frame.dispose(); // Close the window
                }
            });
            backgroundPanel.add(button);
        }
    }

    private void runJavaFile(String fileName) {
        try {
            // Remove the ".java" extension from the file name
            String className = fileName.substring(0, fileName.lastIndexOf("."));

            // Load the class dynamically
            Class<?> clazz = Class.forName(className);

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
                JFrame frame = new JFrame();
                WindowMenuWithBackground window = new WindowMenuWithBackground(frame);
                window.setVisible(true);
            }
        });
    }
}
