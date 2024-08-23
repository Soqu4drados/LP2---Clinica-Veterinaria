package clinicaMenu;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Tutor extends JFrame {
    private JTextField cpfField;
    private JTextField nomeField;
    private JTextField logradouroField;
    private JDateChooser dateChooser;
    private JButton submitButton;
    private JButton updateButton;
    private JButton deleteButton;
    private Connection dbManager;

    public Tutor(Connection dbManager) {
        this(dbManager, null, null, null, null);
    }

    public Tutor(Connection dbManager, String cpf, String nome, Date datadenasc, String logradouro) {
        this.dbManager = dbManager;

        setTitle(cpf == null ? "Insert Tutor" : "Update/Delete Tutor");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 10, 10));
        setLocationRelativeTo(null);

        // Create components
        JLabel cpfLabel = new JLabel("CPF:");
        cpfField = new JTextField(cpf);
        //cpfField.setEnabled(cpf == null); // Disable CPF field if updating/deleting

        JLabel nomeLabel = new JLabel("Nome:");
        nomeField = new JTextField(nome);

        JLabel logradouroLabel = new JLabel("Logradouro:");
        logradouroField = new JTextField(logradouro);

        JLabel dateLabel = new JLabel("Data de Nascimento:");
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        if (datadenasc != null) {
            dateChooser.setDate(datadenasc);
        }

        submitButton = new JButton("Inserir");
        updateButton = new JButton("Alterar");
        deleteButton = new JButton("Excluir");

        // Add components to frame
        add(cpfLabel);
        add(cpfField);
        add(nomeLabel);
        add(nomeField);
        add(logradouroLabel);
        add(logradouroField);
        add(dateLabel);
        add(dateChooser);

        if (cpf == null) {
            add(submitButton);
        } else {
            add(updateButton);
            add(deleteButton);
        }

        // Add action listeners for buttons
        submitButton.addActionListener(e -> handleInsert());
        updateButton.addActionListener(e -> handleUpdate());
        deleteButton.addActionListener(e -> handleDelete());

        setVisible(true);
    }

    private void handleInsert() {
        String cpf = cpfField.getText();
        String nome = nomeField.getText();
        String logradouro = logradouroField.getText();
        java.util.Date selectedDate = dateChooser.getDate();

        if (cpf.isEmpty() || nome.isEmpty() || selectedDate == null) {
            showError("Preencha todos os campos.");
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        try {
            String insertQuery = "INSERT INTO tutor (cpf, nome, datadenasc, logradouro) VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = dbManager.prepareStatement(insertQuery)) {
                pstmt.setString(1, cpf);
                pstmt.setString(2, nome);
                pstmt.setDate(3, sqlDate);
                pstmt.setString(4, logradouro);

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    showInfo("Tutor Inserido com Sucesso!");
                } else {
                    showError("Inserção Falhou. Tente novamente.");
                }
            } catch (SQLException e) {
                showError("Erro ao Inserir no Banco de Dados!");
                e.printStackTrace();
            }
        } catch (NumberFormatException ex) {
            showError("Formato de CPF inválido. Por favor, insira um CPF válido.");
            ex.printStackTrace();
        }
    }

    private void handleUpdate() {
        String cpf = cpfField.getText();
        String nome = nomeField.getText();
        String logradouro = logradouroField.getText();
        java.util.Date selectedDate = dateChooser.getDate();

        if (cpf.isEmpty() || nome.isEmpty() || selectedDate == null) {
            showError("Preencha todos os campos.");
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        try {
            String updateQuery = "UPDATE tutor SET nome = ?, datadenasc = ?, logradouro = ? WHERE cpf = ?";

            try (PreparedStatement pstmt = dbManager.prepareStatement(updateQuery)) {
                pstmt.setString(1, nome);
                pstmt.setDate(2, sqlDate);
                pstmt.setString(3, logradouro);
                pstmt.setString(4, cpf);

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    showInfo("Tutor Atualizado com Sucesso!");
                } else {
                    showError("Atualização Falhou. Tente novamente.");
                }
            } catch (SQLException e) {
                showError("Erro ao Atualizar o Banco de Dados!");
                e.printStackTrace();
            }
        } catch (NumberFormatException ex) {
            showError("Formato de CPF inválido. Por favor, insira um CPF válido.");
        }
    }

    private void handleDelete() {
        String cpf = cpfField.getText();

        if (cpf.isEmpty()) {
            showError("CPF não pode estar vazio.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este tutor?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String deleteQuery = "DELETE FROM tutor WHERE cpf = ?";

            try (PreparedStatement pstmt = dbManager.prepareStatement(deleteQuery)) {
                pstmt.setString(1, cpf);

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    showInfo("Tutor Excluído com Sucesso!");
                } else {
                    showError("Exclusão Falhou. Tente novamente.");
                }
            } catch (SQLException e) {
                showError("Erro ao Excluir do Banco de Dados!");
                e.printStackTrace();
            }
        } catch (NumberFormatException ex) {
            showError("Formato de CPF inválido. Por favor, insira um CPF válido.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
