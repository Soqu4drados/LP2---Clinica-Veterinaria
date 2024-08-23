package clinicaMenu;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            new LoginWindow().setVisible(true);
        });
        
        /*
        SwingUtilities.invokeLater(() -> {
            new MainApplicationWindow().setVisible(true);
        });
*/

    }
}
