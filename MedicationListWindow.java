import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicationListWindow extends JFrame {
    private final List<Medication> medications;
    private final JComboBox<String> medicationComboBox;
    private final JTextField searchField;
    private final JButton searchButton;

    public MedicationListWindow() {
        super("Medication List");

        medications = new ArrayList<>();
        medicationComboBox = new JComboBox<>();
        searchField = new JTextField(20);
        searchButton = new JButton("Search");

        setLayout(new FlowLayout());
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel);

        retrieveMedications();

        for (Medication medication : medications) {
            medicationComboBox.addItem(medication.getName());
        }

        add(medicationComboBox);

        medicationComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedMedicationName = (String) medicationComboBox.getSelectedItem();
                Medication selectedMedication = findMedicationByName(selectedMedicationName);
                if (selectedMedication != null) {
                    MedicationInfoWindow infoWindow = new MedicationInfoWindow(selectedMedication);
                    infoWindow.setVisible(true);
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText();
                if (!searchText.isEmpty()) {
                    searchMedications(searchText);
                } else {
                    updateMedicationComboBox(medications);
                }
            }
        });
    }

    private void retrieveMedications() {
        String url = "jdbc:postgresql://localhost:5432/pharmacy_04_new";
        String username = "postgres";
        String password = "12345678";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Medication");

            while (resultSet.next()) {
                int medicationId = resultSet.getInt("MedicationID");
                String name = resultSet.getString("Name");
                String description = resultSet.getString("Description");
                String manufacturer = resultSet.getString("Manufacturer");
                String strength = resultSet.getString("Strength");
                String dosageForm = resultSet.getString("DosageForm");

                Medication medication = new Medication(medicationId, name, description, manufacturer, strength, dosageForm);
                medications.add(medication);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    private void updateMedicationComboBox(List<Medication> medications) {
        medicationComboBox.removeAllItems();
        for (Medication medication : medications) {
            medicationComboBox.addItem(medication.getName());
        }
    }

    private Medication findMedicationByName(String name) {
        for (Medication medication : medications) {
            if (medication.getName().equals(name)) {
                return medication;
            }
        }
        return null;
    }

    private void searchMedications(String searchText) {
        List<Medication> matchingMedications = new ArrayList<>();
        for (Medication medication : medications) {
            if (medication.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    medication.getDescription().toLowerCase().contains(searchText.toLowerCase()) ||
                    medication.getManufacturer().toLowerCase().contains(searchText.toLowerCase()) ||
                    medication.getStrength().toLowerCase().contains(searchText.toLowerCase()) ||
                    medication.getDosageForm().toLowerCase().contains(searchText.toLowerCase())) {
                matchingMedications.add(medication);
            }
        }
        updateMedicationComboBox(matchingMedications);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MedicationListWindow().setVisible(true);
            }
        });
    }
}

class Medication {
    private final int medicationId;
    private final String name;
    private final String description;
    private final String manufacturer;
    private final String strength;
    private final String dosageForm;

    public Medication(int medicationId, String name, String description, String manufacturer, String strength, String dosageForm) {
        this.medicationId = medicationId;
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.strength = strength;
        this.dosageForm = dosageForm;
    }

    public int getMedicationId() {
        return medicationId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getStrength() {
        return strength;
    }

    public String getDosageForm() {
        return dosageForm;
    }
}

class MedicationInfoWindow extends JFrame {
    public MedicationInfoWindow(Medication medication) {
        super("Medication Information");

        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2));

        add(new JLabel("Medication ID:"));
        add(new JLabel(String.valueOf(medication.getMedicationId())));

        add(new JLabel("Name:"));
        add(new JLabel(medication.getName()));

        add(new JLabel("Description:"));
        add(new JLabel(medication.getDescription()));

        add(new JLabel("Manufacturer:"));
        add(new JLabel(medication.getManufacturer()));

        add(new JLabel("Strength:"));
        add(new JLabel(medication.getStrength()));

        add(new JLabel("Dosage Form:"));
        add(new JLabel(medication.getDosageForm()));
    }
}
