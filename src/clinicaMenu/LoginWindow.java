package clinicaMenu;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LoginWindow extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private final int w=300;
    private final int h=150;


    public LoginWindow() {
        // Set up the frame
        setTitle("Login Window");
        setSize(w, h);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create components
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        
        // Create panel and add components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());  // Empty cell
        panel.add(loginButton);
        
        // Add panel to the frame
        add(panel);
        
        // Add action listener for the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (!username.isEmpty()) {
            if(true){
            new MainApplicationWindow(new MySql().conectar(username, password)).setVisible(true);
            this.setVisible(false);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.");
        }
    }
}
