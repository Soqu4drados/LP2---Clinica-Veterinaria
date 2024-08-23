package clinicaMenu;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class MySql implements IDB{
    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3307/clinicavet";
                                            
    private static String USERNAME = "root";
    private static String PASSWORD = "";
    
    @Override
    public Connection conectar() {
    	Connection conn = null;
        try {
            conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("Connected to MySQL database!");
        } catch (SQLException e) {
            System.err.println("Connection to MySQL failed! Error: " + e.getMessage());
        }
        return conn;
    }
    
    public Connection conectar(String username, String password) {
    	Connection conn = null;
        try {
            conn = DriverManager.getConnection(JDBC_URL, username, password);
            System.out.println("Connected to MySQL database!");
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(null, "Connection to MySQL failed! Error: " 
                     + e.getMessage(), "Database Connection Error", JOptionPane.ERROR_MESSAGE);
             System.exit(9);
        }
        return conn;
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
