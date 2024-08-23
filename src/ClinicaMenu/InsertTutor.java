package ClinicaMenu;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InsertTutor extends JFrame {
    private JTextField cpfField;
    private JTextField nomeField;
    private JTextField logradouroField;
    private JDateChooser dateChooser;
    private JButton submitButton;
    private Connection dbManager;

    public InsertTutor(Connection dbManager) {
        this.dbManager = dbManager;

        setTitle("Insert Tutor");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));
        setLocationRelativeTo(null);

        // Create components
        JLabel cpfLabel = new JLabel("CPF:");
        cpfField = new JTextField();

        JLabel nomeLabel = new JLabel("Nome:");
        nomeField = new JTextField();

        JLabel logradouroLabel = new JLabel("Logradouro:");
        logradouroField = new JTextField();

        JLabel dateLabel = new JLabel("Data de Nascimento:");
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd"); // Set the format as needed

        submitButton = new JButton("Inserir");

        // Add components to frame
        add(cpfLabel);
        add(cpfField);
        add(nomeLabel);
        add(nomeField);
        add(logradouroLabel);
        add(logradouroField);
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

    private void handleSubmit() {
        String cpf = cpfField.getText();
        String nome = nomeField.getText();
        String logradouro = logradouroField.getText();
        java.util.Date selectedDate = dateChooser.getDate();

        if (cpf.isEmpty() || nome.isEmpty() || selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert java.util.Date to java.sql.Date
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        try {
            String insertQuery = "INSERT INTO tutor (cpf, nome, datadenasc, logradouro) VALUES (?, ?, ?, ?)";
                        
            try (PreparedStatement pstmt = dbManager.prepareStatement(insertQuery)) {
                   // Set parameters
                   pstmt.setString(1, cpf);
                   pstmt.setString(2, nome);
                   pstmt.setDate(3, sqlDate);
                   pstmt.setString(4, logradouro);
                   
                   // Execute the insert
                   int affectedRows = pstmt.executeUpdate();
                   
                   if (affectedRows > 0) {
                       JOptionPane.showMessageDialog(this, "Tutor Inserido com Sucesso!", "Success", JOptionPane.INFORMATION_MESSAGE);
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