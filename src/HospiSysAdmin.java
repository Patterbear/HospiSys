package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class HospiSysAdmin {

    // Key attribute
    // used to encrypt/decrypt user data
    private static String adminKey = "adminkeyexample";


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
        newUserButton.addActionListener(e -> {
            try {
                newUser();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        frame.add(newUserButton);

        frame.setVisible(true);
    }


    // New user screen
    // allows user to create an account
    // allows assignment of admin status
    private static void newUser() throws IOException {
        // Frame setup
        JFrame frame = new JFrame("HospiSys - Create new user");
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(640, 275);
        frame.setIconImage(new ImageIcon("img/logo.png").getImage());

        // logo
        JLabel logo = new JLabel(new ImageIcon(ImageIO.read(new File("img/logo.png")).getScaledInstance(200, 150, Image.SCALE_FAST)));

        // label
        JLabel label = new JLabel("Create admin account:");
        //label.setFont(font);

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
        viewPassword.addActionListener(e -> passwordEntry.setEchoChar((char)0));
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
        viewRepeatPassword.addActionListener(e -> repeatPasswordEntry.setEchoChar((char)0));
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
        //saveButton.setFont(font);
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
        //gbc.gridheight = 1;
        frame.getContentPane().add(saveButton, gbc);

        frame.setVisible(true);
    }


}
