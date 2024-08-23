package ClinicaMenu;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertAnimal extends JFrame {
    private JComboBox<String> cpfComboBox;
    private JTextField nomeAnimalField;
    private JTextField racaField;
    private JDateChooser dateChooser;
    private JButton submitButton;
    private Connection dbManager;

    public InsertAnimal(Connection dbManager) {
        this.dbManager = dbManager;

        setTitle("Insert Animal");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));
        setLocationRelativeTo(null);

        // Create components
        JLabel cpfLabel = new JLabel("CPF Tutor:");
        cpfComboBox = new JComboBox<>();
        populateCpfOptions();

        JLabel nomeAnimalLabel = new JLabel("Nome do Animal:");
        nomeAnimalField = new JTextField();

        JLabel racaLabel = new JLabel("Raça:");
        racaField = new JTextField();

        JLabel dateLabel = new JLabel("Data de Nascimento:");
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd"); // Set the format as needed

        submitButton = new JButton("Inserir");

        // Add components to frame
        add(cpfLabel);
        add(cpfComboBox);
        add(nomeAnimalLabel);
        add(nomeAnimalField);
        add(racaLabel);
        add(racaField);
        add(dateLabel);
        add(dateChooser);
        add(submitButton);

        // Add button action listener
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });

        setVisible(true);
    }

    private void populateCpfOptions() {
        String searchQuery = "SELECT cpf FROM tutor;";
        try (Statement stmt = dbManager.createStatement();
             ResultSet rs = stmt.executeQuery(searchQuery)) {
            
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            while (rs.next()) {
                String cpf = rs.getString("cpf");
                model.addElement(cpf);
            }
            
            cpfComboBox.setModel(model);
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao Pesquisar em Tutor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSubmit() {
        String cpfTutor = (String) cpfComboBox.getSelectedItem();
        String nomeAnimal = nomeAnimalField.getText();
        String raca = racaField.getText();
        java.util.Date selectedDate = dateChooser.getDate();

        if (cpfTutor == null || nomeAnimal.isEmpty() || selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert java.util.Date to java.sql.Date
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        try {
            String insertQuery = "INSERT INTO animal (cpfTutor, nomeAnimal, raca, datadenasc) VALUES (?, ?, ?, ?)";
                        
            try (PreparedStatement pstmt = dbManager.prepareStatement(insertQuery)) {
                   // Set parameters
                   pstmt.setString(1, cpfTutor);
                   pstmt.setString(2, nomeAnimal);
                   pstmt.setString(3, raca);
                   pstmt.setDate(4, sqlDate);
                   
                   // Execute the insert
                   int affectedRows = pstmt.executeUpdate();
                   
                   if (affectedRows > 0) {
                       JOptionPane.showMessageDialog(this, "Animal Inserido com Sucesso!", "Success", JOptionPane.INFORMATION_MESSAGE);
                   } else {
                       JOptionPane.showMessageDialog(this, "Inserção Falhou. Tente novamente.", "Insert Error", JOptionPane.ERROR_MESSAGE);
                   }
                   
               } catch (SQLException e) {
                   JOptionPane.showMessageDialog(this, "Erro ao Inserir no Banco de Dados!\n Verifique o Console para mais informações.", "Insert Error", JOptionPane.ERROR_MESSAGE);
                   e.printStackTrace();
               }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Formato de CPF inválido. Por favor, insira um CPF válido.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
