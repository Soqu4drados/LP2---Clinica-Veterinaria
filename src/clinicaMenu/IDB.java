package clinicaMenu;
import java.sql.Connection;

public interface IDB {
	
	public Connection conectar();
	public void desconectar(Connection c);
	
}
