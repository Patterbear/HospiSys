package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Main class
public class HospiSys {

    // 'Start' method
    // creates the initial login screen
    private static void start() {

        // login window JFrame initialisation and configurations
        JFrame frame = new JFrame("HospiSys - Login");
        frame.getContentPane().setLayout(new GridLayout(0, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        // frame panels
        JPanel usernamePanel = new JPanel();
        JPanel passwordPanel = new JPanel();
        JPanel loginButtonPanel = new JPanel();

        // labels and entry  boxes
        TextField usernameEntry = new TextField(10);
        TextField passwordEntry = new TextField(10);

        usernamePanel.add(new JLabel("Username:"));
        usernamePanel.add(usernameEntry);

        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(passwordEntry);


        // login button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login(usernameEntry.getText(), passwordEntry.getText(), frame);
            }
        });

        loginButtonPanel.add(loginButton);


        // add panels to frame
        frame.getContentPane().add(usernamePanel);
        frame.getContentPane().add(passwordPanel);
        frame.getContentPane().add(loginButtonPanel);

        frame.setVisible(true);
    }

    // login function
    // verifies login details and opens menu
    private static void login(String user, String password, JFrame frame) {
        System.out.println(user + password);

        if(true) {
            frame.dispose();
            menu(user);
        }
    }

    // Menu function
    // will open menu screen
    private static void menu(String user) {
        System.out.println("Logged in");
    }

    // main method
    public static void main(String[] args) {
        System.out.println("HospiSys is running...");

        start();
    }
}
