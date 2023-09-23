package src;

import javax.swing.*;
import java.awt.*;

public class HospiSys {

    private static void start() {
        JFrame frame = new JFrame("HospiSys - Login");

        frame.getContentPane().setLayout(new GridLayout(0, 1));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        JPanel usernamePanel = new JPanel();
        JPanel passwordPanel = new JPanel();
        JPanel loginButtonPanel = new JPanel();

        TextField usernameEntry = new TextField(10);
        TextField passwordEntry = new TextField(10);

        usernamePanel.add(new JLabel("Username:"));
        usernamePanel.add(usernameEntry);

        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(passwordEntry);



        JButton loginButton = new JButton("Login");
        loginButtonPanel.add(loginButton);



        frame.getContentPane().add(usernamePanel);
        frame.getContentPane().add(passwordPanel);
        frame.getContentPane().add(loginButtonPanel);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        System.out.println("HospiSys is running...");

        start();
    }
}
