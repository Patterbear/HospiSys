package src;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class HospiSysAdmin {

    // Verify admin status function
    // checks whether a given username hash is in the admin hsd file
    public static boolean verifyAdmin(String usernameHash) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("dat/admin.hsd"));
        String record;

        usernameHash = usernameHash.replace("\\", "");

        while (scanner.hasNextLine()) {
            record = scanner.nextLine().replace("\\", "");
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

        JPanel systemStatusPanel = new JPanel();
        systemStatusPanel.add(new JLabel("System status: Online"));

        // Panel for buttons related to users
        JPanel usersPanel = new JPanel();
        usersPanel.add(new JLabel("Users"));

        // Panel for system buttons
        JPanel systemPanel = new JPanel();
        systemPanel.add(new JLabel("System"));

        // new user button
        JButton newUserButton = new JButton("Create New User");
        newUserButton.addActionListener(e -> {
            try {
                newUser();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // edit/delete user button
        JButton editDeleteUserButton = new JButton("Edit/Delete User");
        editDeleteUserButton.setEnabled(false);

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
                systemKeyConfig(username, password);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton setupButton = new JButton("Reinstall System");
        setupButton.addActionListener(e -> {
            frame.dispose();
            try {
                HospiSysSetup.start();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        usersPanel.add(newUserButton);
        usersPanel.add(editDeleteUserButton);
        usersPanel.add(staffInterfaceButton);
        systemPanel.add(systemKeyButton);
        systemPanel.add(setupButton);

        JButton logoutButton = new JButton("Log Out");
        logoutButton.addActionListener(e -> {
            frame.dispose();
            try {
                HospiSys.start();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        frame.getContentPane().add(systemStatusPanel);
        frame.getContentPane().add(usersPanel);
        frame.getContentPane().add(systemPanel);
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
                if(HospiSysData.usernameSuitable(usernameEntry.getText())) {
                    if(HospiSysData.passwordSuitable(passwordEntry.getText())) {
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
                        JOptionPane.showMessageDialog(null, "Passwords must be at least 8 characters long and contain at least 1 number or symbol.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Username must be at least 4 characters long and contain at least 1 number or symbol.");

                }

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

        String key = s.nextLine();
        s.close();

        return key;
    }

    // Set system key method
    // changes key and re-encrypts patient data
    private static void setSystemKey(String newKey, String username, String password) throws IOException {

        // Decrypt patient data ready for system key change
        String[][] decryptedData = HospiSysData.decryptPatientData(username, password);

        // Write new key to file
        PrintWriter pw = new PrintWriter("dat/syskey.txt");
        pw.println(newKey);
        pw.close();

        // Re-encrypt data
        HospiSysData.reencryptPatientData(decryptedData, username, password);

    }

    // System Key generation function
    // generates a random system key
    // used for initial setup and key regeneration
    public static String generateSystemKey() {
        char[] letters = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        String key = "";

        for (int i = 0; i < 26; i++) {
            Random r = new Random(); // new 'Random' object initialised with each iteration for extra randomness
            key += letters[r.nextInt(26)];
        }

        return key;
    }

    // Screen for system key changing
    // allows user to change system key
    private static void editSystemKey(JFrame parent, String username, String password) {
        JFrame frame = HospiSys.buildScreen("Edit System Key", 500, 140, false);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        frame.setResizable(false);

        JPanel keyPanel = new JPanel();
        keyPanel.add(new JLabel("New System Key:"));

        TextField keyEntry = new TextField(20);
        keyEntry.setText(generateSystemKey());
        keyEntry.setEditable(false);
        keyPanel.add(keyEntry);

        JButton regenButton = new JButton("Regenerate");
        regenButton.addActionListener(e -> keyEntry.setText(generateSystemKey()));
        keyPanel.add(regenButton);

        JButton saveButton = new JButton("Save");
        saveButton.setFont(HospiSys.font);
        saveButton.addActionListener(e -> {
            try {
                setSystemKey(keyEntry.getText(), username, password);
                frame.dispose();
                parent.dispose();
                systemKeyConfig(username, password);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        frame.getContentPane().add(keyPanel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.getContentPane().add(saveButton, gbc);

        frame.setVisible(true);
    }


    // System key config window method
    // allows user to view or edit the system key
    private static void systemKeyConfig(String username, String password) throws FileNotFoundException {
        JFrame frame = HospiSys.buildScreen("System Key", 400, 125, false);
        frame.setLayout(new GridLayout(0, 1));
        frame.setResizable(false);

        JPanel keyPanel = new JPanel();
        keyPanel.add(new JLabel("System Key:"));

        TextField systemKey = new TextField(20);
        systemKey.setEditable(false);
        systemKey.setEchoChar('*');
        systemKey.setText(getSystemKey());

        keyPanel.add(systemKey);

        JPanel buttons = new JPanel();

        JButton back = new JButton("Back");
        back.addActionListener(e -> frame.dispose());

        JButton edit = new JButton("Regenerate");
        edit.addActionListener(e -> editSystemKey(frame, username, password));

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

        frame.getContentPane().add(keyPanel);
        frame.getContentPane().add(buttons);

        frame.setVisible(true);
    }
}
