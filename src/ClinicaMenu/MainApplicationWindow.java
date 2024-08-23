package ClinicaMenu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
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
	private final Connection dbManager = new MySql().conectar();

	public MainApplicationWindow() {
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
		dbTables = new JComboBox<String>();
		dbRows = new JComboBox<String>();

		// Set The Table Name to The Combo Box
		dbTables.addItem("");
		dbTables.addItem("Animal");
		dbTables.addItem("Agendamento");
		dbTables.addItem("Tutor");
		dbTables.addItem("Veterinario");

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
		btnInsert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				insertData();
			}
		});

		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					searchDatabase();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});

		dbTables.addActionListener(new ActionListener() {
			@SuppressWarnings("static-access")
			@Override
			public void actionPerformed(ActionEvent e) {
				// Clear the items combo box
				dbRows.removeAllItems();

				// Get selected category
				String selectedCategory = (String) dbTables.getSelectedItem();

				// Update items based on table category
				// Animal - cpfTutor, codigo, nomeAnimal, raca, datadenasc;
				// Veterinario - registro, cpf, nome, datadenasc;
				// Tutor - CPF, nome, datadenasc, logradouro
				// Agendamento - codigoAgendamento, dataAgendamento, horaAgendamento,

				if (selectedCategory.equals("Animal")) {
					dbRows.addItem("");
					dbRows.addItem("CPF do Tutor");
					dbRows.addItem("Codigo");
					dbRows.addItem("Nome");
					dbRows.addItem("Raça");
					dbRows.addItem("Data de Nascimento");
				} else if (selectedCategory.equals("Veterinario")) {
					dbRows.addItem("");
					dbRows.addItem("Registro");
					dbRows.addItem("CPF");
					dbRows.addItem("Nome");
					dbRows.addItem("Data de Nascimento");
				} else if (selectedCategory.equals("Tutor")) {
					dbRows.addItem("");
					dbRows.addItem("CPF");
					dbRows.addItem("Nome");
					dbRows.addItem("Data de Nascimento");
					dbRows.addItem("Logradouro");
				} else if (selectedCategory.equals("Agendamento")) {
					dbRows.addItem("");
					dbRows.addItem("Codigo");
					dbRows.addItem("Data");
					dbRows.addItem("Hora");
					dbRows.addItem("Veterinario");
					dbRows.addItem("Animal");
				} else {
					dbRows.removeAllItems();
					dbRows.addItem("");
					dbRows.addItem("Aqui ta vaziou :)");
					new JOptionPane().showMessageDialog(null, "Select a Table to search", "Invalid Table",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
	}

	private void insertData() {
		Object[] itens = { "Agenda", "Animal","Veterinario","Tutor" };
	      Object selectedValue = JOptionPane.showInputDialog(null,
	    		  "Escolha o que você quer adicionar: ", "Tabela:",JOptionPane.INFORMATION_MESSAGE, null,
	                  itens, itens [0]); 
	      switch(selectedValue.toString()) {
	      case "Agenda":
	    	  new InsertAgenda(dbManager).setVisible(true);
	    	  break;
	      case "Veterinario":
	    	  new InsertVeterinario(dbManager).setVisible(true);
	    	  break;
	      case "Animal":
	    	  new InsertAnimal(dbManager).setVisible(true);
	    	  break;
	      case "Tutor":
	    	  new InsertTutor(dbManager).setVisible(true);
	    	  break;
	    	  
	      }
	      
	}
	
	private void searchDatabase() throws SQLException {
    	
    	//Basic Select
    	if (dbManager == null || dbManager.isClosed()) {
    	    System.out.println("Connection is not properly initialized or is closed.");
    	}
    	if(dbTables.getSelectedItem()=="") {
    		JOptionPane.showMessageDialog(this, "Selecione uma tabela!", "Error", JOptionPane.INFORMATION_MESSAGE);
    		return;
    	}
        String searchQuery = "SELECT ";  
        switch (dbTables.getSelectedItem().toString()) {
        case "Agendamento":
        	searchQuery += "codigoAgendamento as \"Agendamento\", dataagendamento as \"Data\", horaagendamento as \"Hora\", "
	    	  		+ "nome as \"Veterinario\", animal as Logradouro ";	
	    	  break;
	      case "Veterinario":
	    	  searchQuery += "registro as \"Resgistro\", cpf as \"CPF\", nome as \"Nome\", "
	    	  		+ "datadenasc as \"Data De Nascimento\", logradouro as Logradouro ";
	    	  break;
	      case "Animal":
	    	  searchQuery += "cpfTutor as \"CPF do Tutor\", codigo as \"Codigo\", nomeAnimal as \"Nome\", "
		    	  		+ "datadenasc as \"Data De Nascimento\", raca as \"Raça\"";
	    	  break;
	      case "Tutor":
	    	  searchQuery += "nome as \"Nome\", cpf as \"CPF\","
		    	  		+ "datadenasc as \"Data De Nascimento\", logradouro as Logradouro ";
	    	  break;
	    	  
	      }
        
        
        searchQuery += "from " + dbTables.getSelectedItem().toString();
        
        //select row to be searched
        String row = dbRows.getSelectedItem().toString();
        
        row = getCodigoSQL(row);
        ArrayList<String> Strings = new ArrayList<String>();
    	Strings.add("nomeAnimal");
    	Strings.add("raca");
    	Strings.add("nome");
    	Strings.add("logradouro");
        
        //Add a where clause
        if(searchStatement.getText().toString().isBlank()) {
        	searchQuery += "";	
        } else if(Strings.contains(row)) {
        	searchQuery += " where " + row + " like \"%" + searchStatement.getText().toString() + "%\"";
        }
        else {
        	searchQuery += " where " + row + " = \"" + searchStatement.getText().toString() + "\"";
        }
        
        if(dbTables.getSelectedItem()=="Animal") {
        	
        	
        } else if (dbTables.getSelectedItem()=="Agendamento") {
        	searchQuery += " JOIN Animal on animal.codigo=agendamento.animal join Veterinario on veterinario.registro=agendamento.veterinario";        	
        }
        
        searchQuery +=";";
        
        
        
        
        
        System.out.println(searchQuery);
        
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
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
	
	private String getCodigoSQL(String row) {
		 if(dbTables.getSelectedItem().toString().equals("Animal")){
	        	switch (row) {
	        	case "CPF do Tutor":
	        		row = "cpfTutor";
	        		break;
	        	case "Codigo":
	        		row = "codigo";
	        		break;
	        	case "Nome":
	        		row = "nomeAnimal";
	        		break;
	        	case "Raça":
	        		row = "raca";
	        		break;
	        	case "Data de Nascimento":
	        		row = "datadenasc";
	        		break;
	        	default:
	        		row = " ";
	        		break;
	        	}
	        } else if(dbTables.getSelectedItem().toString().equals("Veterinario")){
	        	switch (row) {
	        	case "Registro":
	        		row = "registro";
	        		break;
	        	case "CPF":
	        		row = "cpf";
	        		break;
	        	case "Nome":
	        		row = "nome";
	        		break;
	        	case "Data de Nascimento":
	        		row = "datadenasc";
	        		break;
	        	default:
	        		row = " ";
	        		break;
	        	}
	        } else if(dbTables.getSelectedItem().toString().equals("Agendamento")){
	        	switch (row) {
	        	case "Codigo":
	        		row = "codigoAgendamento";
	        		break;
	        	case "Data":
	        		row = "dataAgendamento";
	        		break;
	        	case "Hora":
	        		row = "horaAgendamento";
	        		break;
	        	case "Veterinario":
	        		row = "veterinario";
	        		break;
	        	case "Animal":
	        		row = "animal";
	        		break;
	        	default:
	        		row = " ";
	        		break;
	        	}			
	        } else if(dbTables.getSelectedItem().toString().equals("Tutor")){
	        	switch (row) {
	        	case "CPF":
	        		row = "cpf";
	        		break;
	        	case "Nome":
	        		row = "nome";
	        		break;
	        	case "Data de Nascimento":
	        		row = "datadenasc";
	        		break;
	        	case "Logradouro":
	        		row = "logradouro";
	        		break;
	        	default:
	        		row = " ";
	        		break;
	        	}
	        }
		 return row;
	        //System.out.println(row);
	}

}
