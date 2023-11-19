package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

// System setup class
public class HospiSysSetup {

    private static Font font = new Font(Font.DIALOG, Font.BOLD, 24);;

    // Setup admin screen
    // TODO: update to simply call 'createUser' in HospiSysAdmin
    private static void setupAdmin(String path) throws IOException {
        // Frame setup
        JFrame frame = new JFrame("HospiSys - Setup Admin");
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
        label.setFont(font);

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
        saveButton.setFont(font);
        saveButton.addActionListener(e -> {
            if (passwordEntry.getText().equals(repeatPasswordEntry.getText())) {
                try {
                    new HospiSysData(path + "/HospiSys/dat/users.hsd").writeUser(usernameEntry.getText(), passwordEntry.getText());
                    new HospiSysData(path + "/HospiSys/dat/admin.hsd").encryptedWrite(usernameEntry.getText(), passwordEntry.getText());

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
        Path folderPath = Path.of(path + "/HospiSys");
        if(Files.exists(folderPath)) {

            // pop up gives the user option to delete existing installation
            int choice = JOptionPane.showConfirmDialog(null,
                    "HospiSys installation already exists. Delete?",
                    "Installation found",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(choice == JOptionPane.YES_OPTION) {
                deleteInstallation(folderPath);
            } else {
                return;
            }
        }

        new File(path + "/HospiSys").mkdirs();
        new File(path + "/HospiSys/dat").mkdirs();
        new File(path + "/HospiSys/img").mkdirs();
    }

    // Delete existing installation function
    // deletes an existing installation if found
    private static void deleteInstallation(Path folderPath) throws IOException {
        Files.walk(folderPath).sorted(Comparator.reverseOrder()).forEach(path -> {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Create data files method
    private static void createHSDs(String path) throws IOException {
        Files.createFile(Paths.get(path + "/HospiSys/dat/admin.hsd"));
        Files.createFile(Paths.get(path + "/HospiSys/dat/patients.hsd"));
        Files.createFile(Paths.get(path + "/HospiSys/dat/users.hsd"));
    }

    // Install directory selection screen
    private static void selectDirectory() throws IOException {
        // JFrame initialisation and configurations
        JFrame frame = new JFrame("HospiSys - Select Install Location");
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 260);
        frame.setResizable(false);
        frame.setIconImage(new ImageIcon("img/logo.png").getImage());

        // logo
        JLabel logo = new JLabel(new ImageIcon(ImageIO.read(new File("img/logo.png")).getScaledInstance(200, 150, Image.SCALE_FAST)));

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
        Font nextButtonFont = new Font(nextButton.getFont().getName(),nextButton.getFont().getStyle(),24);

        nextButton.setFont(nextButtonFont);
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
    private static void start() throws IOException {

        // JFrame initialisation and configurations
        JFrame frame = new JFrame("HospiSys - Setup");
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 200);
        frame.setResizable(false);
        frame.setIconImage(new ImageIcon("img/logo.png").getImage());

        // logo
        JLabel logo = new JLabel(new ImageIcon(ImageIO.read(new File("img/logo.png")).getScaledInstance(200, 150, Image.SCALE_FAST)));

        JLabel label = new JLabel("HospiSys Setup");

        JPanel buttonsPanel = new JPanel();
        JButton begin = new JButton("Begin");
        JButton close = new JButton("Exit");

        begin.setFont(font);
        close.setFont(font);
        label.setFont(font);

        begin.addActionListener(e -> {
            frame.dispose();
            try {
                selectDirectory();
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


    // Main method
    public static void main(String[] args) throws IOException {
        start();
    }
}
