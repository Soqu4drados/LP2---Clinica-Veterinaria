package ClinicaMenu;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class InsertAgenda extends JFrame {
    private JDateChooser dateChooser;
    private JSpinner hourSpinner;
    private JComboBox<String> veterinarioComboBox;
    private JComboBox<String> animalComboBox;
    private Map<String, String> veterinarioMap = new HashMap<>();
    private Map<String, Integer> animalMap = new HashMap<>();
    private JButton submitButton;
    private Connection dbManager;

    public InsertAgenda(Connection dbManager) {
        this.dbManager = dbManager;

        setTitle("Insert Agenda");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 10, 10));
        setLocationRelativeTo(null);

        // Create components
        JLabel dateLabel = new JLabel("Data:");
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd"); // Set the format as needed

        JLabel hourLabel = new JLabel("Hora:");
        hourSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor hourEditor = new JSpinner.DateEditor(hourSpinner, "HH:mm");
        hourSpinner.setEditor(hourEditor);

        JLabel veterinarioLabel = new JLabel("Veterinario:");
        veterinarioComboBox = new JComboBox<>();

        JLabel animalLabel = new JLabel("Animal:");
        animalComboBox = new JComboBox<>();
        
        submitButton = new JButton("Inserir");

        // Populate combo boxes
        populateVeterinarioOptions();
        populateAnimalOptions();

        // Add components to frame
        add(dateLabel);
        add(dateChooser);
        add(hourLabel);
        add(hourSpinner);
        add(veterinarioLabel);
        add(veterinarioComboBox);
        add(animalLabel);
        add(animalComboBox);
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

    private void populateVeterinarioOptions() {
        String searchQuery = "SELECT nome, registro FROM veterinario;";
        try (Statement stmt = dbManager.createStatement();
             ResultSet rs = stmt.executeQuery(searchQuery)) {
            
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            veterinarioMap.clear();

            while (rs.next()) {
                String nome = rs.getString("nome");
                String codigo = rs.getString("registro");
                model.addElement(nome);
                veterinarioMap.put(nome, codigo);
            }
            
            veterinarioComboBox.setModel(model);
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao Pesquisar em Veterinario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateAnimalOptions() {
        String searchQuery = "SELECT nomeAnimal, codigo FROM animal;";
        try (Statement stmt = dbManager.createStatement();
             ResultSet rs = stmt.executeQuery(searchQuery)) {
            
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            animalMap.clear();

            while (rs.next()) {
                String nome = rs.getString("nomeAnimal");
                int codigo = rs.getInt("codigo");
                model.addElement(nome);
                animalMap.put(nome, codigo);
            }
            
            animalComboBox.setModel(model);
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao Pesquisar em Animal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSubmit() {
        java.util.Date selectedDate = dateChooser.getDate();
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma data.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert java.util.Date to java.sql.Date
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
        
        // Get time from spinner
        java.util.Date selectedTime = (java.util.Date) hourSpinner.getValue();
        java.sql.Time sqlTime = new java.sql.Time(selectedTime.getTime());

        String selectedVeterinario = (String) veterinarioComboBox.getSelectedItem();
        String selectedAnimal = (String) animalComboBox.getSelectedItem();

        if (selectedDate == null || selectedVeterinario == null || selectedAnimal == null) {
            JOptionPane.showMessageDialog(this, "Preencha todas as Colunas.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String veterinarioCodigo = veterinarioMap.get(selectedVeterinario);
            Integer animalCodigo = animalMap.get(selectedAnimal);

            String insertQuery = "INSERT INTO agendamento (dataagendamento, horaagendamento, veterinario, animal) VALUES (?, ?, ?, ?)";
                        
            try (PreparedStatement pstmt = dbManager.prepareStatement(insertQuery)) {
                   // Set parameters
                   pstmt.setDate(1, sqlDate);
                   pstmt.setTime(2, sqlTime);
                   pstmt.setString(3, veterinarioCodigo);
                   pstmt.setInt(4, animalCodigo);
                   
                   // Execute the insert
                   int affectedRows = pstmt.executeUpdate();
                   
                   if (affectedRows > 0) {
                       JOptionPane.showMessageDialog(this, "Agendamento Inserido com Sucesso!", "Success", JOptionPane.INFORMATION_MESSAGE);
                   } else {
                       JOptionPane.showMessageDialog(this, "Inserção Falhou. Tente novamente.", "Insert Error", JOptionPane.ERROR_MESSAGE);
                   }
                   
               } catch (SQLException e) {
                   JOptionPane.showMessageDialog(this, "Erro ao Inserir no Banco de Dados!\n Verifique o Console para mais informações.", "Insert Error", JOptionPane.ERROR_MESSAGE);
                   e.printStackTrace();
               }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Formato de Codigo inválido. Por favor, insira um número válido.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
