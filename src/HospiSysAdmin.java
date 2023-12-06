package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class HospiSysAdmin {

    // Verify admin status function
    // checks whether a given username hash is in the admin hsd file
    public static boolean verifyAdmin(String usernameHash) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("dat/admin.hsd"));
        String record;

        usernameHash = usernameHash.replace("\\", "");

        while (scanner.hasNextLine()) {
            record = scanner.nextLine();
            if (record.equals(usernameHash)) {
                scanner.close();
                return true;
            }
        }

        scanner.close();
        return false;
    }

    // Admin interface access method
    // permits entry to verified admins
    public static void accessAdminInterface(String username, String password) throws FileNotFoundException {
        if(verifyAdmin(Playfair.encrypt(username, password))) {
            adminInterface(username, password);
        }
    }

    // Admin interface method
    // GUI that allows the user to add new users
    private static void adminInterface(String username, String password) {

        // Frame setup
        JFrame frame = HospiSys.buildScreen("Admin Interface", 725, 350, true);
        frame.setLayout(new GridLayout(0, 1));

        JPanel buttonsPanel = new JPanel();

        // new user button
        JButton newUserButton = new JButton("Create New User");
        newUserButton.addActionListener(e -> {
            try {
                newUser();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton staffInterfaceButton = new JButton("Staff Interface");
        staffInterfaceButton.addActionListener(e -> {
            frame.dispose();
            try {
                HospiSys.menu(username, password);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton systemKeyButton = new JButton("System Key");
        systemKeyButton.addActionListener(e -> {
            try {
                systemKeyConfig();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        buttonsPanel.add(newUserButton);
        buttonsPanel.add(staffInterfaceButton);
        buttonsPanel.add(systemKeyButton);

        JButton logoutButton = new JButton("Log Out");
        logoutButton.addActionListener(e -> {
            frame.dispose();
            try {
                HospiSys.start();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        frame.getContentPane().add(new JLabel("System Status: Online"));
        frame.getContentPane().add(buttonsPanel);
        frame.getContentPane().add(logoutButton);



        frame.setVisible(true);
    }


    // New user screen
    // allows user to create an account
    // allows assignment of admin status
    private static void newUser() throws IOException {
        // Frame setup
        JFrame frame = HospiSys.buildScreen("Create New User", 640, 275, false);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // logo
        JLabel logo = new JLabel(HospiSys.logoImage);

        // label
        JLabel label = new JLabel("Create new account:");

        // entries section panel
        JPanel entriesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints entriesGbc = new GridBagConstraints();

        // Username entry
        TextField usernameEntry = new TextField(20);
        entriesGbc.gridx = 0;
        entriesGbc.gridy = 0;
        entriesPanel.add(new JLabel("Username:"), entriesGbc);
        entriesGbc.gridx = 1;
        entriesGbc.anchor = GridBagConstraints.WEST;
        entriesPanel.add(usernameEntry, entriesGbc);

        // Password entry
        TextField passwordEntry = new TextField(20);
        passwordEntry.setEchoChar('*');
        JButton viewPassword = new JButton("Show");
        viewPassword.addActionListener(e -> {
            if (passwordEntry.getEchoChar() == '*') {
                viewPassword.setText("Hide");
                passwordEntry.setEchoChar((char)0);
            } else {
                viewPassword.setText("Show");
                passwordEntry.setEchoChar('*');
            }
        });

        entriesGbc.gridx = 0;
        entriesGbc.gridy = 1;
        entriesGbc.anchor = GridBagConstraints.CENTER;
        entriesPanel.add(new JLabel("Password:"), entriesGbc);
        entriesGbc.gridx = 1;
        entriesGbc.anchor = GridBagConstraints.WEST;
        entriesPanel.add(passwordEntry, entriesGbc);
        entriesGbc.gridx = 2;
        entriesPanel.add(viewPassword, entriesGbc);

        // Repeat password entry
        TextField repeatPasswordEntry = new TextField(20);
        repeatPasswordEntry.setEchoChar('*');
        JButton viewRepeatPassword = new JButton("Show");
        viewRepeatPassword.addActionListener(e -> {
            if (repeatPasswordEntry.getEchoChar() == '*') {
                repeatPasswordEntry.setEchoChar((char)0);
                viewRepeatPassword.setText("Hide");
            } else {
                viewRepeatPassword.setText("Show");
                repeatPasswordEntry.setEchoChar('*');
            }
        });
        entriesGbc.gridx = 0;
        entriesGbc.gridy = 2;
        entriesGbc.anchor = GridBagConstraints.CENTER;
        entriesPanel.add(new JLabel("Repeat Password:"), entriesGbc);
        entriesGbc.gridx = 1;
        entriesGbc.anchor = GridBagConstraints.WEST;
        entriesPanel.add(repeatPasswordEntry, entriesGbc);
        entriesGbc.gridx = 2;
        entriesPanel.add(viewRepeatPassword, entriesGbc);

        // Admin status panel
        JPanel makeAdminPanel = new JPanel();
        makeAdminPanel.add(new JLabel("Make admin?"));
        JCheckBox adminStatus = new JCheckBox();
        makeAdminPanel.add(adminStatus);


        // save button
        JButton saveButton = new JButton("Save");
        saveButton.setFont(HospiSys.font);
        saveButton.addActionListener(e -> {
            if (passwordEntry.getText().equals(repeatPasswordEntry.getText())) {
                try {
                    new HospiSysData("dat/users.hsd").writeUser(usernameEntry.getText(), passwordEntry.getText());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                if (adminStatus.isSelected()) {
                    try {
                        new HospiSysData("dat/admin.hsd").encryptedWrite(usernameEntry.getText(), passwordEntry.getText());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                frame.dispose();

            } else {
                JOptionPane.showMessageDialog(null, "Passwords do not match.");
            }

        });

        // add logo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        frame.getContentPane().add(logo, gbc);

        // add label
        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 3;
        frame.getContentPane().add(label, gbc);

        // add entries panel
        gbc.gridy = 1;
        gbc.gridheight = 3;
        gbc.gridwidth = 3;
        frame.getContentPane().add(entriesPanel, gbc);

        // add admin status section
        gbc.gridy = 4;
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        frame.getContentPane().add(makeAdminPanel, gbc);

        // add save button
        gbc.gridy = 5;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        frame.getContentPane().add(saveButton, gbc);

        frame.setVisible(true);
    }


    // Give system key function
    // returns system key if user is authorised
    public static String requestSystemKey(String username, String password) throws FileNotFoundException {
        if(new HospiSysData("dat/users.hsd").verifyUser(username, password)) {
            return getSystemKey();
        } else {
            return "";
        }

    }


    // Get system key method
    // retrieves key from file
    // COMMENT: If this system was commercially deployed, this file would be stored securely on the admin server
    private static String getSystemKey() throws FileNotFoundException {
        Scanner s = new Scanner(new File("dat/syskey.txt"));

        return s.nextLine();
    }

    // Set system key method
    // changes key and re-encrypts patient data
    private static void setSystemKey(String newKey) throws IOException {
        PrintWriter pw = new PrintWriter("dat/syskey.txt");
        pw.println(newKey);
        pw.close();
    }

    // Screen for system key changing
    // allows user to change system key
    private static void editSystemKey(JFrame parent) {
        JFrame frame = HospiSys.buildScreen("Edit System Key", 400, 175, false);
        frame.setLayout(new GridLayout(0, 1));

        TextField keyEntry = new TextField(20);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                setSystemKey(keyEntry.getText());
                frame.dispose();
                parent.dispose();
                systemKeyConfig();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });


        frame.getContentPane().add(new JLabel("New System Key:"));
        frame.getContentPane().add(keyEntry);
        frame.getContentPane().add(saveButton);


        frame.setVisible(true);
    }


    // System key config window method
    // allows user to view or edit the system key
    private static void systemKeyConfig() throws FileNotFoundException {
        JFrame frame = HospiSys.buildScreen("System Key", 400, 175, false);
        frame.setLayout(new GridLayout(0, 1));
        frame.setResizable(false);

        JLabel label = new JLabel("System Key");
        label.setBorder(new EmptyBorder(0,150,0,0));

        TextField systemKey = new TextField(20);
        systemKey.setEditable(false);
        systemKey.setEchoChar('*');
        systemKey.setText(getSystemKey());

        JPanel buttons = new JPanel();

        JButton back = new JButton("Back");
        back.addActionListener(e -> frame.dispose());

        JButton edit = new JButton("Edit");
        edit.addActionListener(e -> editSystemKey(frame));

        JButton view = new JButton("View");
        view.addActionListener(e -> {
            if (view.getText().equals("View")) {
                view.setText("Hide");
                systemKey.setEchoChar((char)0);
            } else {
                view.setText("View");
                systemKey.setEchoChar('*');
            }
        });

        buttons.add(back);
        buttons.add(edit);
        buttons.add(view);

        frame.getContentPane().add(label);
        frame.getContentPane().add(systemKey);
        frame.getContentPane().add(buttons);

        frame.setVisible(true);
    }
}
