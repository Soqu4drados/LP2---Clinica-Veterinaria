/*
package ClinicaMenu;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {

    public MainMenu() {
        // Set up the frame
        setTitle("Main Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create buttons
        JButton btnAnimais = new JButton("Animais");
        JButton btnVeteterinarios = new JButton("Veterinários");
        JButton btnAgenda = new JButton("Agendas");
        
        // Create panel and add buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        panel.add(btnAnimais);
        panel.add(btnVeteterinarios);
        panel.add(btnAgenda);
        
        // Add panel to the frame
        add(panel);
        
        // Add action listeners for the buttons
        btnAnimais.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Animais().setVisible(true);
            }
        });
        
        btnVeteterinarios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Veterinarios().setVisible(true);
            }
        });
        
        btnAgenda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Agenda().setVisible(true);
            }
        });
    }
}
*/