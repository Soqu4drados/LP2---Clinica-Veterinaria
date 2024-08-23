package clinicaMenu;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Date;

public class Animal extends JFrame {
    private JComboBox<String> cpfComboBox;
    private JTextField nomeAnimalField;
    private JTextField racaField;
    private JDateChooser dateChooser;
    private JButton submitButton;
    private JButton updateButton;
    private JButton deleteButton;
    private Connection dbManager;
    private int codigo;
    private String cpfTutor;

    public Animal(Connection dbManager) {
        this(dbManager, null, 0, null, null, null);
    }

    public Animal(Connection dbManager, String cpfTutor, int codigo, String nomeAnimal, String raca, Date datadenasc) {
        this.dbManager = dbManager;
        this.codigo = codigo;
        this.cpfTutor = cpfTutor;

        setTitle(codigo == 0 ? "Inserir Animal" : "Atualizar/Excluir Animal");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 10, 10));
        setLocationRelativeTo(null);

        initializeComponents(nomeAnimal, raca, datadenasc);
        populateCpfOptions();

        if (codigo == 0) {
            add(submitButton);
        } else {
            add(updateButton);
            add(deleteButton);
        }

        addActionListeners();
        setVisible(true);
    }

    private void initializeComponents(String nomeAnimal, String raca, Date datadenasc) {
        JLabel cpfLabel = new JLabel("CPF Tutor:");
        cpfComboBox = new JComboBox<>();
        if (cpfTutor != null) {
            cpfComboBox.setSelectedItem(cpfTutor);
        }

        JLabel nomeAnimalLabel = new JLabel("Nome do Animal:");
        nomeAnimalField = new JTextField(nomeAnimal != null ? nomeAnimal : "");

        JLabel racaLabel = new JLabel("Raça:");
        racaField = new JTextField(raca != null ? raca : "");

        JLabel dateLabel = new JLabel("Data de Nascimento:");
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        if (datadenasc != null) {
            dateChooser.setDate(datadenasc);
        }

        submitButton = new JButton("Inserir");
        updateButton = new JButton("Alterar");
        deleteButton = new JButton("Excluir");

        add(cpfLabel);
        add(cpfComboBox);
        add(nomeAnimalLabel);
        add(nomeAnimalField);
        add(racaLabel);
        add(racaField);
        add(dateLabel);
        add(dateChooser);
    }

    private void populateCpfOptions() {
        String query = "SELECT cpf FROM tutor";
        try (Statement stmt = dbManager.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            while (rs.next()) {
                model.addElement(rs.getString("cpf"));
            }
            cpfComboBox.setModel(model);
        } catch (SQLException e) {
            showError("Erro ao Pesquisar em Tutor: " + e.getMessage());
        }
    }

    private void addActionListeners() {
        submitButton.addActionListener(e -> handleSubmit());
        updateButton.addActionListener(e -> handleUpdate());
        deleteButton.addActionListener(e -> handleDelete());
    }

    private void handleSubmit() {
        String cpfTutor = (String) cpfComboBox.getSelectedItem();
        String nomeAnimal = nomeAnimalField.getText();
        String raca = racaField.getText();
        java.util.Date selectedDate = dateChooser.getDate();

        if (cpfTutor == null || nomeAnimal.isEmpty() || selectedDate == null) {
            showWarning("Preencha todos os campos.");
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        String query = "INSERT INTO animal (cpfTutor, nomeAnimal, raca, datadenasc) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = dbManager.prepareStatement(query)) {
            pstmt.setString(1, cpfTutor);
            pstmt.setString(2, nomeAnimal);
            pstmt.setString(3, raca);
            pstmt.setDate(4, sqlDate);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showInfo("Animal Inserido com Sucesso!");
            } else {
                showError("Inserção Falhou. Tente novamente.");
            }
        } catch (SQLException e) {
            showError("Erro ao Inserir no Banco de Dados: " + e.getMessage());
        }
    }

    private void handleUpdate() {
        String cpfTutor = (String) cpfComboBox.getSelectedItem();
        String nomeAnimal = nomeAnimalField.getText();
        String raca = racaField.getText();
        java.util.Date selectedDate = dateChooser.getDate();

        if (cpfTutor == null || nomeAnimal.isEmpty() || selectedDate == null) {
            showWarning("Preencha todos os campos.");
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        String query = "UPDATE animal SET cpfTutor = ?, nomeAnimal = ?, raca = ?, datadenasc = ? WHERE codigo = ?";
        try (PreparedStatement pstmt = dbManager.prepareStatement(query)) {
            pstmt.setString(1, cpfTutor);
            pstmt.setString(2, nomeAnimal);
            pstmt.setString(3, raca);
            pstmt.setDate(4, sqlDate);
            pstmt.setInt(5, codigo);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showInfo("Animal Atualizado com Sucesso!");
            } else {
                showError("Atualização Falhou. Tente novamente.");
            }
        } catch (SQLException e) {
            showError("Erro ao Atualizar no Banco de Dados: " + e.getMessage());
        }
    }

    private void handleDelete() {
        int confirmation = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este animal?", "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM animal WHERE codigo = ?";
            try (PreparedStatement pstmt = dbManager.prepareStatement(query)) {
                pstmt.setInt(1, codigo);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    showInfo("Animal Excluído com Sucesso!");
                    dispose(); // Close the window after deletion
                } else {
                    showError("Exclusão Falhou. Tente novamente.");
                }
            } catch (SQLException e) {
                showError("Erro ao Excluir do Banco de Dados: " + e.getMessage());
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}
