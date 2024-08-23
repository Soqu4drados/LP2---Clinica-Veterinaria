package Clinica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySql implements IDB{
    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/clinicavet";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "aluno";
    
    @Override
    public Connection conectar() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            System.out.println("Connected to MySQL database!");
            return conn;

          
        } catch (SQLException e) {
            System.err.println("Connection to MySQL failed! Error: " + e.getMessage());
            return null;
        }
    }

	@Override
	public void desconectar(Connection c) {
		if (c != null) {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
}
