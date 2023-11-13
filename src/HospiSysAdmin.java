package src;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HospiSysAdmin {

    // Key attribute
    // used to encrypt/decrypt user data
    private String adminKey = "adminkeyexample";


    // Verify admin status function
    // checks whether a given username hash is in the admin hsd file
    public static boolean verifyAdmin(String usernameHash) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("dat/admin.hsd"));
        String record;

        while (scanner.hasNextLine()) {
            record = scanner.nextLine();
            if (record.equals(usernameHash)) {
                return true;
            }
        }
        return false;
    }

    // Admin interface access method
    // permits entry to verified admins
    public static void accessAdminInterface(String usernameHash) throws FileNotFoundException {
        if(verifyAdmin(usernameHash)) {
            adminInterface();
        }
    }

    // Admin interface method
    // GUI that allows the user to add new users
    private static void adminInterface() {

        // Frame setup
        JFrame frame = new JFrame("HospiSys - Admin Interface");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(725, 350);
        frame.setIconImage(new ImageIcon("img/logo.png").getImage());

        // new user button
        JButton newUserButton = new JButton("Create New User");
        newUserButton.addActionListener(e -> newUser());

        frame.add(newUserButton);

        frame.setVisible(true);
    }

    // New user screen
    // allows user to create an account
    // allows assignment of admin status
    private static void newUser() {

        // Frame setup
        JFrame frame = new JFrame("HospiSys - Add User");
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(0, 1));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(480, 450);
        frame.setIconImage(new ImageIcon("img/logo.png").getImage());

        // Username entry
        JPanel usernamePanel = new JPanel();
        TextField usernameEntry = new TextField(20);
        usernamePanel.add(new JLabel("Username: "));
        usernamePanel.add(usernameEntry);

        // Password entry
        JPanel passwordPanel = new JPanel();
        TextField passwordEntry = new TextField(20);
        passwordEntry.setEchoChar('*');
        JButton viewPassword = new JButton("Show");
        passwordPanel.add(new JLabel("Password: "));
        passwordPanel.add(passwordEntry);
        passwordPanel.add(viewPassword);

        // Repeat password entry
        JPanel repeatPasswordPanel = new JPanel();
        TextField repeatPasswordEntry = new TextField(20);
        repeatPasswordEntry.setEchoChar('*');
        JButton viewRepeatPassword = new JButton("Show");
        passwordPanel.add(new JLabel("Repeat password: "));
        passwordPanel.add(repeatPasswordEntry);
        passwordPanel.add(viewRepeatPassword);

        // Admin status panel
        JPanel makeAdminPanel = new JPanel();
        makeAdminPanel.add(new JLabel("Make admin?"));
        JCheckBox adminStatus = new JCheckBox();
        makeAdminPanel.add(adminStatus);

        JButton saveButton = new JButton("Save");

        frame.getContentPane().add(usernamePanel);
        frame.getContentPane().add(passwordPanel);
        frame.getContentPane().add(repeatPasswordPanel);
        frame.getContentPane().add(makeAdminPanel);
        frame.getContentPane().add(saveButton);

        frame.setVisible(true);
    }


}
