package src;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;


// System setup class
public class HospiSysSetup {


    // Setup System Key screen
    public static void setupSystemKey(String path) {
        JFrame frame = HospiSys.buildScreen("Create System Key", 725, 150, false);
        frame.setResizable(false);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Screen title label
        JLabel title = new JLabel("Please create a system key");
        title.setFont(HospiSys.font);

        // Information label
        JLabel info = new JLabel("Please make a secure physical copy of this key." +
                " If the key file is deleted or tampered with, the data will not decrypt.");


        // Key section
        JPanel keyPanel = new JPanel();

        // Key entry
        TextField keyEntry = new TextField(20);
        keyPanel.add(keyEntry);

        // Save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                frame.dispose();

                // Write new key to file
                PrintWriter pw = new PrintWriter("dat/syskey.txt");
                pw.println(keyEntry.getText());
                pw.close();

                setupAdmin(path);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        keyPanel.add(saveButton);


        // add title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        frame.getContentPane().add(title, gbc);

        // add text
        gbc.gridy = 1;
        frame.getContentPane().add(info, gbc);

        // add key panel
        gbc.gridy = 2;
        frame.getContentPane().add(keyPanel, gbc);


        frame.setVisible(true);
    }

    // Setup admin screen
    private static void setupAdmin(String path) throws IOException {
        // Frame setup
        JFrame frame = HospiSys.buildScreen("Setup Admin", 640, 275, false);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // logo
        JLabel logo = new JLabel(HospiSys.logoImage);

        // label
        JLabel label = new JLabel("Create admin account:");
        label.setFont(HospiSys.font);

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

        // save button
        JButton saveButton = new JButton("Save");
        saveButton.setFont(HospiSys.font);
        saveButton.addActionListener(e -> {
            if (passwordEntry.getText().equals(repeatPasswordEntry.getText())) {
                try {
                    new HospiSysData(path + "/dat/users.hsd").writeUser(usernameEntry.getText(), passwordEntry.getText());
                    new HospiSysData(path + "/dat/admin.hsd").encryptedWrite(usernameEntry.getText(), passwordEntry.getText());

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                frame.dispose();
                try {
                    HospiSys.start();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
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

        // add username panel
        gbc.gridy = 1;
        gbc.gridheight = 3;
        gbc.gridwidth = 3;
        frame.getContentPane().add(entriesPanel, gbc);

        // add save button
        gbc.gridy = 4;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        frame.getContentPane().add(saveButton, gbc);

        frame.setVisible(true);
    }

    // Create folders function
    private static void createFolders(String path) throws IOException {
        // Path folderPath = Path.of(path + "/HospiSys");

        Path datPath = Path.of(path + "/dat");
        Path imgPath = Path.of(path + "/img");


        if(Files.exists(datPath)) {
            String messageExtra = "";
            if(Files.exists(imgPath)) {
                messageExtra += "Incomplete ";
            }

            // pop up gives the user option to delete existing installation
            int choice = JOptionPane.showConfirmDialog(null,
                    messageExtra + "HospiSys installation found. Delete?",
                    messageExtra + "Installation found",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(choice == JOptionPane.YES_OPTION) {
                deleteInstallation(new File(path).toPath());
            } else {
                return;
            }
        }

        new File(path + "/dat").mkdirs();
        new File(path + "/img").mkdirs();
        createHSDs(path);
    }

    // Delete existing installation function
    // deletes an existing installation if found
    private static void deleteInstallation(Path folderPath) throws IOException {

        // delete dat folder and files
        Path datPath = new File(folderPath + "/dat").toPath();

        Files.walk(datPath).sorted(Comparator.reverseOrder()).forEach(path -> {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // delete patient images
        // TODO: THIS
    }

    // Create data files method
    private static void createHSDs(String path) throws IOException {
        Files.createFile(Paths.get(path + "/dat/admin.hsd"));
        Files.createFile(Paths.get(path + "/dat/patients.hsd"));
        Files.createFile(Paths.get(path + "/dat/users.hsd"));


        setupSystemKey(path);
    }

    // Install directory selection screen
    // unused for now as installation will occur in current directory
    private static void selectDirectory() throws IOException {
        // JFrame initialisation and configurations
        JFrame frame = HospiSys.buildScreen("Select Install Location", 650, 260, true);
        frame.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // logo
        JLabel logo = new JLabel(HospiSys.logoImage);

        // Folder selection section
        JPanel chooseFolderPanel = new JPanel();
        TextField directoryEntry = new TextField(System.getProperty("user.home"), 20);
        JButton changeDirectoryButton = new JButton("Change");
        JFileChooser jfc = new JFileChooser(directoryEntry.getText());
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setSelectedFile(new File(System.getProperty("user.home")));

        changeDirectoryButton.addActionListener(e -> {
            jfc.showOpenDialog(null);
            directoryEntry.setText(jfc.getSelectedFile().getPath());
        });

        chooseFolderPanel.add(new JLabel("Install Folder:"));
        chooseFolderPanel.add(directoryEntry);
        chooseFolderPanel.add(changeDirectoryButton);

        JButton nextButton = new JButton("Next");

        nextButton.setFont(HospiSys.font);
        nextButton.addActionListener(e -> {
            try {
                createFolders(jfc.getSelectedFile().getPath());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                createHSDs(jfc.getSelectedFile().getPath());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            try {
                frame.dispose();
                setupAdmin(jfc.getSelectedFile().getPath());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // add logo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        frame.getContentPane().add(logo, gbc);

        // add label
        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 3;
        frame.getContentPane().add(new JLabel("Choose install location:"), gbc);

        // add choose folder panel
        gbc.gridy = 1;
        frame.getContentPane().add(chooseFolderPanel, gbc);

        // add next button
        gbc.gridy = 2;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        frame.getContentPane().add(nextButton, gbc);

        frame.setVisible(true);



    }

    // Start method
    // opens setup initialisation screen
    public static void start() {

        // JFrame initialisation and configurations
        JFrame frame = HospiSys.buildScreen("Setup", 450, 200, true);
        frame.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // logo
        JLabel logo = new JLabel(HospiSys.logoImage);

        JLabel label = new JLabel("HospiSys Setup");

        JPanel buttonsPanel = new JPanel();
        JButton begin = new JButton("Begin");
        JButton close = new JButton("Exit");

        begin.setFont(HospiSys.font);
        close.setFont(HospiSys.font);
        label.setFont(HospiSys.font);

        begin.addActionListener(e -> {
            frame.dispose();
            try {
                String currentPath = System.getProperty("user.dir");
                createFolders(currentPath);
                //createHSDs(currentPath);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        close.addActionListener(e -> frame.dispose());
        buttonsPanel.add(close);
        buttonsPanel.add(begin);

        // add logo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        frame.getContentPane().add(logo, gbc);

        // add screen label
        gbc.gridheight = 1;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        frame.getContentPane().add(label, gbc);

        // add buttons panel
        gbc.gridy = 1;
        frame.getContentPane().add(buttonsPanel, gbc);

        frame.setVisible(true);
    }
}
