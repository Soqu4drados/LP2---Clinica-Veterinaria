package clinicaMenu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MainApplicationWindow extends JFrame {

    private JTextField searchStatement;
    private JComboBox<String> dbTables;
    private JComboBox<String> dbRows;
    private JButton btnInsert;
    private JButton btnSearch;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private final Connection dbManager;

    public MainApplicationWindow(Connection conn) {
        //dbManager = new MySql().conectar(); // Initialize database connection
        this.dbManager = conn;
        
        // Set up the frame
        setTitle("Database Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        searchStatement = new JTextField(20);
        btnInsert = new JButton("Inserir");
        btnSearch = new JButton("Pesquisar");
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        dbTables = new JComboBox<>(new String[]{"", "Animal", "Agendamento", "Tutor", "Veterinario"});
        dbRows = new JComboBox<>();

        // Create layout
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Tabela:"));
        topPanel.add(dbTables);
        topPanel.add(new JLabel("Pesquisar:"));
        topPanel.add(searchStatement);
        topPanel.add(dbRows);
        topPanel.add(btnSearch);
        topPanel.add(btnInsert);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        // Add action listeners
        btnInsert.addActionListener(e -> insertData());
        btnSearch.addActionListener(e -> {
            try {
                searchDatabase();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        dbTables.addActionListener(e -> updateRowOptions());
        
        resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        int row = resultTable.rowAtPoint(evt.getPoint());
        int column = resultTable.columnAtPoint(evt.getPoint());

        if (row >= 0 && column >= 0) {
            // Extract row data
            Vector<?> rowData = tableModel.getDataVector().elementAt(row);

            // Determine table type
            String tableType = (String) dbTables.getSelectedItem();

            // Open respective editor with row data
            openEditor(tableType, rowData);
        }
    }
});

    }

    private void insertData() {
        Object[] items = {"Agenda", "Animal", "Veterinario", "Tutor"};
        Object selectedValue = JOptionPane.showInputDialog(this,
                "Escolha o que você quer adicionar:", "Tabela:", JOptionPane.INFORMATION_MESSAGE,
                null, items, items[0]);

        if (selectedValue != null) {
            switch (selectedValue.toString()) {
                case "Agenda":
                    new Agenda(dbManager).setVisible(true);
                    break;
                case "Veterinario":
                    new Veterinario(dbManager).setVisible(true);
                    break;
                case "Animal":
                    new Animal(dbManager).setVisible(true);
                    break;
                case "Tutor":
                    new Tutor(dbManager).setVisible(true);
                    break;
            }
        }
    }

    private void searchDatabase() throws SQLException {
        String selectedTable = (String) dbTables.getSelectedItem();
        if (selectedTable == null || selectedTable.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione uma tabela!", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String searchQuery = buildSearchQuery(selectedTable);
        if (searchQuery == null) return; // Invalid table selected

        try (Statement stmt = dbManager.createStatement();
             ResultSet rs = stmt.executeQuery(searchQuery)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Clear existing data
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            // Set column names
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }
            tableModel.setColumnIdentifiers(columnNames);

            // Add rows
            while (rs.next()) {
                Vector<Object> rowData = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.add(rs.getObject(i));
                }
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
                // Create a stack trace string
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    String stackTrace = sw.toString();

    // Display a detailed error message including stack trace
    JOptionPane.showMessageDialog(this, 
        "Failed to retrieve data.\n\n" + stackTrace, 
        "Error", 
        JOptionPane.ERROR_MESSAGE);
        }
    }

    private String buildSearchQuery(String table) {
        String selectClause;
        switch (table) {
            case "Agendamento":
                selectClause = "codigoAgendamento as Agendamento, dataagendamento as Data, horaagendamento as Hora, " +
                        "veterinario as Veterinario, animal as Logradouro";
                break;
            case "Veterinario":
                selectClause = "registro as Registro, cpf as CPF, nome as Nome, " +
                        "datadenasc as 'Data De Nascimento', logradouro as Logradouro";
                break;
            case "Animal":
                selectClause = "cpfTutor as 'CPF do Tutor', codigo as Codigo, nomeAnimal as Nome, " +
                        "datadenasc as 'Data De Nascimento', raca as Raça";
                break;
            case "Tutor":
                selectClause = "nome as Nome, cpf as CPF, datadenasc as 'Data De Nascimento', logradouro as Logradouro";
                break;
            default:
                return null;
        }

        String whereClause = buildWhereClause(table);

        return "SELECT " + selectClause + " FROM " + table + whereClause + ";";
    }

    private String buildWhereClause(String table) {
        String row = (String) dbRows.getSelectedItem();
        if (row == null || row.isEmpty()) return "";

        row = getCodigoSQL(table, row);

        if (searchStatement.getText().isBlank()) {
            return "";
        }

        List<String> stringFields = List.of("nomeAnimal", "raca", "nome", "logradouro");

        if (stringFields.contains(row)) {
            return " WHERE " + row + " LIKE '%" + searchStatement.getText() + "%'";
        } else {
            return " WHERE " + row + " = '" + searchStatement.getText() + "'";
        }
    }

    private void updateRowOptions() {
        String selectedTable = (String) dbTables.getSelectedItem();
        if (selectedTable == null || selectedTable.isEmpty()) {
            dbRows.removeAllItems();
            return;
        }

        dbRows.removeAllItems();
        dbRows.addItem(""); // Add default empty item

        switch (selectedTable) {
            case "Animal":
                addRowOptions("CPF do Tutor", "Codigo", "Nome", "Raça", "Data de Nascimento");
                break;
            case "Veterinario":
                addRowOptions("Registro", "CPF", "Nome", "Data de Nascimento");
                break;
            case "Tutor":
                addRowOptions("CPF", "Nome", "Data de Nascimento", "Logradouro");
                break;
            case "Agendamento":
                addRowOptions("Codigo", "Data", "Hora", "Veterinario", "Animal");
                break;
            default:
                dbRows.addItem("Aqui tá vazio :)");
                JOptionPane.showMessageDialog(this, "Select a Table to search", "Invalid Table", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addRowOptions(String... options) {
        for (String option : options) {
            dbRows.addItem(option);
        }
    }

    private String getCodigoSQL(String table, String row) {
        switch (table) {
            case "Animal":
                return switch (row) {
                    case "CPF do Tutor" -> "cpfTutor";
                    case "Codigo" -> "codigo";
                    case "Nome" -> "nomeAnimal";
                    case "Raça" -> "raca";
                    case "Data de Nascimento" -> "datadenasc";
                    default -> "";
                };
            case "Veterinario":
                return switch (row) {
                    case "Registro" -> "registro";
                    case "CPF" -> "cpf";
                    case "Nome" -> "nome";
                    case "Data de Nascimento" -> "datadenasc";
                    default -> "";
                };
            case "Agendamento":
                return switch (row) {
                    case "Codigo" -> "codigoAgendamento";
                    case "Data" -> "dataAgendamento";
                    case "Hora" -> "horaAgendamento";
                    case "Veterinario" -> "veterinario";
                    case "Animal" -> "animal";
                    default -> "";
                };
            case "Tutor":
                return switch (row) {
                    case "CPF" -> "cpf";
                    case "Nome" -> "nome";
                    case "Data de Nascimento" -> "datadenasc";
                    case "Logradouro" -> "logradouro";
                    default -> "";
                };
            default:
                return "";
        }
    }
    
private void openEditor(String tableType, Vector<?> rowData) {
    switch (tableType) {
        case "Animal":
            openAnimalEditor(rowData);
            break;
        case "Veterinario":
            openVeterinarioEditor(rowData);
            break;
        case "Tutor":
            openTutorEditor(rowData);
            break;
        case "Agendamento":
            openAgendaEditor(rowData);
            break;
        default:
            JOptionPane.showMessageDialog(this, "Please select a valid table.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
    }
}

private void openAnimalEditor(Vector<?> rowData) {
    try {
        String cpfTutor = rowData.get(0).toString();
        int codigo = Integer.parseInt(rowData.get(1).toString());
        String nomeAnimal = rowData.get(2).toString();
        String raca = rowData.get(4).toString();

        // Convert the date from String to java.sql.Date
        String dateString = rowData.get(3).toString();
        java.sql.Date datadenasc = null;
        if (dateString != null && !dateString.isEmpty()) {
            // Assuming date is in "yyyy-MM-dd" format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = sdf.parse(dateString);
            datadenasc = new java.sql.Date(utilDate.getTime());
        }

        new Animal(dbManager, cpfTutor, codigo, nomeAnimal, raca, datadenasc);
    } catch (ParseException | NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Error parsing data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


private void openVeterinarioEditor(Vector<?> rowData) {
    try {
        // Retrieve and convert data
        String registro = rowData.get(0).toString();
        String cpf = rowData.get(1).toString();
        String nome = rowData.get(2).toString();

        // Convert the date from String to java.sql.Date
        String dateString = rowData.get(3).toString();
        java.sql.Date dataDeNasc = null;
        if (dateString != null && !dateString.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = sdf.parse(dateString);
            dataDeNasc = new java.sql.Date(utilDate.getTime());
        }

        String logradouro = rowData.get(4).toString();

        new Veterinario(dbManager, registro, cpf, nome, dataDeNasc, logradouro);
    } catch (ParseException e) {
        JOptionPane.showMessageDialog(this, "Error parsing data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


private void openTutorEditor(Vector<?> rowData) {
    try {
        // Retrieve and convert data
        String cpf = rowData.get(1).toString();
        String nome = rowData.get(0).toString();

        // Convert the date from String to java.sql.Date
        String dateString = rowData.get(2).toString();
        java.sql.Date datadenasc = null;
        if (dateString != null && !dateString.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = sdf.parse(dateString);
            datadenasc = new java.sql.Date(utilDate.getTime());
        }

        String logradouro = rowData.get(3).toString();

        new Tutor(dbManager, cpf, nome, datadenasc, logradouro);
    } catch (ParseException e) {
        JOptionPane.showMessageDialog(this, "Error parsing data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


private void openAgendaEditor(Vector<?> rowData) {
    try {
        int codigoAgendamento = Integer.parseInt(rowData.get(0).toString());

        // Convert the date from String to java.sql.Date
        String dateString = rowData.get(1).toString();
        java.sql.Date dataAgendamento = null;
        if (dateString != null && !dateString.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = sdf.parse(dateString);
            dataAgendamento = new java.sql.Date(utilDate.getTime());
        }

        // Convert the time from String to java.sql.Time
        String timeString = rowData.get(2).toString();
        java.sql.Time horaAgendamento = null;
        if (timeString != null && !timeString.isEmpty()) {
            horaAgendamento = java.sql.Time.valueOf(timeString);
        }

        String veterinario = rowData.get(3).toString();
        int animal = Integer.parseInt(rowData.get(4).toString());

        new Agenda(dbManager, codigoAgendamento, dataAgendamento, horaAgendamento, veterinario, animal);
    } catch (ParseException | IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, "Error parsing data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}



}
