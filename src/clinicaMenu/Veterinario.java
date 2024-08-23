package clinicaMenu;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Veterinario extends JFrame {
    private JTextField registroField;
    private JTextField cpfField;
    private JTextField nomeField;
    private JTextField logradouroField;
    private JDateChooser dateChooser;
    private JButton submitButton;
    private JButton updateButton;
    private JButton deleteButton;
    private Connection dbManager;
    private String registro;

    // Construtor para inserção
    public Veterinario(Connection dbManager) {
        this(dbManager, null, null, null, null, null);
    }

    // Construtor para alterar ou deletar
    public Veterinario(Connection dbManager, String registro, String cpf, String nome, Date dataDeNasc, String logradouro) {
        this.dbManager = dbManager;
        this.registro = registro;

        setTitle(registro == null ? "Inserir Veterinario" : "Alterar/Deletar Veterinario");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Criar componentes
        JLabel registroLabel = new JLabel("Registro:");
        registroField = new JTextField(registro != null ? registro : "");

        JLabel cpfLabel = new JLabel("CPF:");
        cpfField = new JTextField(cpf != null ? cpf : "");

        JLabel nomeLabel = new JLabel("Nome:");
        nomeField = new JTextField(nome != null ? nome : "");

        JLabel logradouroLabel = new JLabel("Logradouro:");
        logradouroField = new JTextField(logradouro != null ? logradouro : "");

        JLabel dateLabel = new JLabel("Data de Nascimento:");
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        if (dataDeNasc != null) {
            dateChooser.setDate(dataDeNasc);
        }

        submitButton = new JButton("Inserir");
        updateButton = new JButton("Atualizar");
        deleteButton = new JButton("Deletar");

        // Adicionar componentes ao frame
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

        if (registro == null) {
            // Modo de inserção
            add(submitButton, gbc);
        } else {
            // Modo de alteração e deleção
        	
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(updateButton);
            buttonPanel.add(deleteButton);
            add(buttonPanel, gbc);
        }

        // Adicionar listeners aos botões
        submitButton.addActionListener(e -> handleSubmit());
        updateButton.addActionListener(e -> handleUpdate());
        deleteButton.addActionListener(e -> handleDelete(registro));

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

        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        try {
            String insertQuery = "INSERT INTO veterinario (registro, cpf, nome, datadenasc, logradouro) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = dbManager.prepareStatement(insertQuery)) {
                pstmt.setString(1, registro);
                pstmt.setString(2, cpf);
                pstmt.setString(3, nome);
                pstmt.setDate(4, sqlDate);
                pstmt.setString(5, logradouro);

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

    private void handleUpdate() {
    	String registro = registroField.getText();
        String cpf = cpfField.getText();
        String nome = nomeField.getText();
        String logradouro = logradouroField.getText();
        java.util.Date selectedDate = dateChooser.getDate();

        if (cpf.isEmpty() || nome.isEmpty() || selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        try {
            String updateQuery = "UPDATE veterinario SET registro = ?, cpf = ?, nome = ?, datadenasc = ?, logradouro = ? WHERE registro = ?";

            try (PreparedStatement pstmt = dbManager.prepareStatement(updateQuery)) {
            	pstmt.setString(1, registro);
                pstmt.setString(2, cpf);
                pstmt.setString(3, nome);
                pstmt.setDate(4, sqlDate);
                pstmt.setString(5, logradouro);
                pstmt.setString(6, this.registro);

                int affectedRows = pstmt.executeUpdate();
                System.out.println(this.registro);
                System.out.println(pstmt);
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Veterinário Atualizado com Sucesso!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Atualização Falhou. Tente novamente.", "Update Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao Atualizar no Banco de Dados!\n Verifique o Console para mais informações.", "Update Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Formato de CPF inválido. Por favor, insira um CPF válido.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete(String registro) {
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza de que deseja deletar este veterinário?", "Confirmação", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String deleteQuery = "DELETE FROM veterinario WHERE registro = ?";

                try (PreparedStatement pstmt = dbManager.prepareStatement(deleteQuery)) {
                    pstmt.setString(1, registro);

                    int affectedRows = pstmt.executeUpdate();

                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(this, "Veterinário Deletado com Sucesso!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // Fecha a janela após a exclusão
                    } else {
                        JOptionPane.showMessageDialog(this, "Deleção Falhou. Tente novamente.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Erro ao Deletar no Banco de Dados!\n Verifique o Console para mais informações.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao processar a deleção.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
