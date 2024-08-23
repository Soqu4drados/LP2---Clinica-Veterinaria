package ClinicaMenu;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InsertVeterinario extends JFrame {
    private JTextField registroField;
    private JTextField cpfField;
    private JTextField nomeField;
    private JTextField logradouroField;
    private JDateChooser dateChooser;
    private JButton submitButton;
    private Connection dbManager;

    public InsertVeterinario(Connection dbManager) {
        this.dbManager = dbManager;

        setTitle("Insert Veterinario");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Create components
        JLabel registroLabel = new JLabel("Registro:");
        registroField = new JTextField();

        JLabel cpfLabel = new JLabel("CPF:");
        cpfField = new JTextField();

        JLabel nomeLabel = new JLabel("Nome:");
        nomeField = new JTextField();

        JLabel logradouroLabel = new JLabel("Logradouro:");
        logradouroField = new JTextField();

        JLabel dateLabel = new JLabel("Data de Nascimento:");
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");

        submitButton = new JButton("Inserir");

        // Add components to frame
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(registroLabel, gbc);

        gbc.gridx = 1;
        add(registroField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(cpfLabel, gbc);

        gbc.gridx = 1;
        add(cpfField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(nomeLabel, gbc);

        gbc.gridx = 1;
        add(nomeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(logradouroLabel, gbc);

        gbc.gridx = 1;
        add(logradouroField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(dateLabel, gbc);

        gbc.gridx = 1;
        add(dateChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        // Add button action listener
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });

        setVisible(true);
    }

    private void handleSubmit() {
        String registro = registroField.getText();
        String cpf = cpfField.getText();
        String nome = nomeField.getText();
        String logradouro = logradouroField.getText();
        java.util.Date selectedDate = dateChooser.getDate();

        if (registro.isEmpty() || cpf.isEmpty() || nome.isEmpty() || selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert java.util.Date to java.sql.Date
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        try {
            String insertQuery = "INSERT INTO veterinario (registro, cpf, nome, datadenasc, logradouro) VALUES (?, ?, ?, ?, ?)";
                        
            try (PreparedStatement pstmt = dbManager.prepareStatement(insertQuery)) {
                   // Set parameters
                   pstmt.setString(1, registro);
                   pstmt.setString(2, cpf);
                   pstmt.setString(3, nome);
                   pstmt.setDate(4, sqlDate);
                   pstmt.setString(5, logradouro);
                   
                   // Execute the insert
                   int affectedRows = pstmt.executeUpdate();
                   
                   if (affectedRows > 0) {
                       JOptionPane.showMessageDialog(this, "Veterinário Inserido com Sucesso!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
