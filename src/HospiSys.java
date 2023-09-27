package src;

import javax.swing.*;
import java.awt.*;

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
        loginButton.addActionListener(e -> login(usernameEntry.getText(), passwordEntry.getText(), frame));

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

        // menu window JFrame initialisation and configurations
        JFrame frame = new JFrame("HospiSys - Menu");
        frame.getContentPane().setLayout(new GridLayout(0, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        // frame panels
        JPanel patientPanel = new JPanel();
        JPanel staffPanel = new JPanel();
        JPanel logoutButtonPanel = new JPanel();

        // buttons
        JButton patientSearch = new JButton("Search");
        patientSearch.addActionListener(e -> search("Patient"));

        JButton addNewPatient = new JButton("Add New");
        addNewPatient.addActionListener(e -> addNew("Patient"));

        JButton staffSearch = new JButton("Search");
        staffSearch.addActionListener(e -> search("Staff"));

        JButton addNewStaff = new JButton("Add New");
        addNewStaff.addActionListener(e -> addNew("Staff"));

        JButton logout = new JButton("Log Out");
        logout.addActionListener(e -> {
            frame.dispose();
            start();
        });

        // add components to panels
        patientPanel.add(new JLabel("Patients"));
        patientPanel.add(patientSearch);
        patientPanel.add(addNewPatient);

        staffPanel.add(new JLabel("Staff"));
        staffPanel.add(staffSearch);
        staffPanel.add(addNewStaff);

        logoutButtonPanel.add(logout);

        // add panels to frame
        frame.getContentPane().add(patientPanel);
        frame.getContentPane().add(staffPanel);
        frame.getContentPane().add(logoutButtonPanel);

        frame.setVisible(true);

    }

    private static void search(String category) {
        System.out.println(category);
    }

    private static void addNew(String category) {
        System.out.println(category);
    }


    // main method
    public static void main(String[] args) {
        System.out.println("HospiSys is running...");

        start();
    }
}
