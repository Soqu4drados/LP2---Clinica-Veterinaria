package Clinica;
import java.sql.Connection;
import java.sql.SQLException;

public class App {
	public static void main(String[] args) throws SQLException, NumberFormatException {
		//Connection c = Utils.conectar();
		//Utils.menu();
		//Utils.desconectar(c);
		
		IDB db = new MySql();
		Connection c = db.conectar();	
		DBManager.menu();
		
		
		db.desconectar(c);
		


	}
}
