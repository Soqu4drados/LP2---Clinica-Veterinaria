package clinicaMenu;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.SpinnerDateModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Agenda extends JFrame {
    private JDateChooser dateChooser;
    private JSpinner hourSpinner;
    private JComboBox<String> veterinarioComboBox;
    private JComboBox<String> animalComboBox;
    private Map<String, String> veterinarioMap = new HashMap<>();
    private Map<String, Integer> animalMap = new HashMap<>();
    private JButton submitButton;
    private JButton updateButton;
    private JButton deleteButton;
    private Connection dbManager;
    private int codigo;

    public Agenda(Connection dbManager) {
        this(dbManager, 0, null, null, null, 0);
    }

    public Agenda(Connection dbManager, int codigoAgendamento, Date dataagendamento, Time horaagendamento, String veterinario, int animal) {
        this.dbManager = dbManager;
        this.codigo = codigoAgendamento;

        setTitle(codigoAgendamento == 0 ? "Inserir Agenda" : "Atualizar/Excluir Agenda");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 10, 10));
        setLocationRelativeTo(null);

        initializeComponents(dataagendamento, horaagendamento);
        populateVeterinarioOptions();
        populateAnimalOptions();

        if (codigoAgendamento == 0) {
            add(submitButton);
        } else {
            add(updateButton);
            add(deleteButton);
        }

        addActionListeners();
        setVisible(true);
    }

    private void initializeComponents(Date dataagendamento, Time horaagendamento) {
        JLabel dateLabel = new JLabel("Data:");
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        if (dataagendamento != null) {
            dateChooser.setDate(dataagendamento);
        }

        JLabel hourLabel = new JLabel("Hora:");
        hourSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor hourEditor = new JSpinner.DateEditor(hourSpinner, "HH:mm");
        hourSpinner.setEditor(hourEditor);
        if (horaagendamento != null) {
            hourSpinner.setValue(horaagendamento);
        }

        JLabel veterinarioLabel = new JLabel("Veterinário:");
        veterinarioComboBox = new JComboBox<>();

        JLabel animalLabel = new JLabel("Animal:");
        animalComboBox = new JComboBox<>();

        submitButton = new JButton("Inserir");
        updateButton = new JButton("Alterar");
        deleteButton = new JButton("Excluir");

        add(dateLabel);
        add(dateChooser);
        add(hourLabel);
        add(hourSpinner);
        add(veterinarioLabel);
        add(veterinarioComboBox);
        add(animalLabel);
        add(animalComboBox);
    }

    private void populateVeterinarioOptions() {
        String query = "SELECT nome, registro FROM veterinario";
        try (Statement stmt = dbManager.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            veterinarioMap.clear();
            while (rs.next()) {
                String nome = rs.getString("nome");
                String registro = rs.getString("registro");
                model.addElement(nome);
                veterinarioMap.put(nome, registro);
            }
            veterinarioComboBox.setModel(model);
        } catch (SQLException e) {
            showError("Erro ao Pesquisar em Veterinario: " + e.getMessage());
        }
    }

    private void populateAnimalOptions() {
        String query = "SELECT nomeAnimal, codigo FROM animal";
        try (Statement stmt = dbManager.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
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
            showError("Erro ao Pesquisar em Animal: " + e.getMessage());
        }
    }

    private void addActionListeners() {
        submitButton.addActionListener(e -> handleSubmit());
        updateButton.addActionListener(e -> handleUpdate());
        deleteButton.addActionListener(e -> handleDelete());
    }

    private void handleSubmit() {
        java.util.Date selectedDate = dateChooser.getDate();
        if (selectedDate == null) {
            showWarning("Selecione uma data.");
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
        java.util.Date selectedTime = (java.util.Date) hourSpinner.getValue();
        java.sql.Time sqlTime = new java.sql.Time(selectedTime.getTime());

        String selectedVeterinario = (String) veterinarioComboBox.getSelectedItem();
        String selectedAnimal = (String) animalComboBox.getSelectedItem();

        if (selectedVeterinario == null || selectedAnimal == null) {
            showWarning("Preencha todas as colunas.");
            return;
        }

        try {
            String veterinarioCodigo = veterinarioMap.get(selectedVeterinario);
            Integer animalCodigo = animalMap.get(selectedAnimal);

            String insertQuery = "INSERT INTO agendamento (dataagendamento, horaagendamento, veterinario, animal) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = dbManager.prepareStatement(insertQuery)) {
                pstmt.setDate(1, sqlDate);
                pstmt.setTime(2, sqlTime);
                pstmt.setString(3, veterinarioCodigo);
                pstmt.setInt(4, animalCodigo);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    showInfo("Agendamento Inserido com Sucesso!");
                } else {
                    showError("Inserção Falhou. Tente novamente.");
                }
            }
        } catch (SQLException e) {
            showError("Erro ao Inserir no Banco de Dados: " + e.getMessage());
        }
    }

    private void handleUpdate() {
        java.util.Date selectedDate = dateChooser.getDate();
        if (selectedDate == null) {
            showWarning("Selecione uma data.");
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
        java.util.Date selectedTime = (java.util.Date) hourSpinner.getValue();
        java.sql.Time sqlTime = new java.sql.Time(selectedTime.getTime());

        String selectedVeterinario = (String) veterinarioComboBox.getSelectedItem();
        String selectedAnimal = (String) animalComboBox.getSelectedItem();

        if (selectedVeterinario == null || selectedAnimal == null) {
            showWarning("Preencha todas as colunas.");
            return;
        }

        try {
            String veterinarioCodigo = veterinarioMap.get(selectedVeterinario);
            Integer animalCodigo = animalMap.get(selectedAnimal);

            String updateQuery = "UPDATE agendamento SET dataagendamento = ?, horaagendamento = ?, veterinario = ?, animal = ? WHERE codigoAgendamento = ?";
            try (PreparedStatement pstmt = dbManager.prepareStatement(updateQuery)) {
                pstmt.setDate(1, sqlDate);
                pstmt.setTime(2, sqlTime);
                pstmt.setString(3, veterinarioCodigo);
                pstmt.setInt(4, animalCodigo);
                pstmt.setInt(5, codigo);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    showInfo("Agendamento Atualizado com Sucesso!");
                } else {
                    showError("Atualização Falhou. Tente novamente.");
                }
            }
        } catch (SQLException e) {
            showError("Erro ao Atualizar no Banco de Dados: " + e.getMessage());
        }
    }

    private void handleDelete() {
        int confirmation = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este agendamento?", "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            java.util.Date selectedDate = dateChooser.getDate();
            java.util.Date selectedTime = (java.util.Date) hourSpinner.getValue();
            if (selectedDate == null || selectedTime == null) {
                showWarning("Data e hora são necessários para a exclusão.");
                return;
            }

            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
            java.sql.Time sqlTime = new java.sql.Time(selectedTime.getTime());

            String deleteQuery = "DELETE FROM agendamento WHERE dataagendamento = ? AND horaagendamento = ?";
            try (PreparedStatement pstmt = dbManager.prepareStatement(deleteQuery)) {
                pstmt.setDate(1, sqlDate);
                pstmt.setTime(2, sqlTime);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    showInfo("Agendamento Excluído com Sucesso!");
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
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }
}
