package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import net.miginfocom.swing.MigLayout;

// System setup class
public class HospiSysSetup {

    public static Font font = new Font(Font.DIALOG, Font.BOLD, 24);;

    // Setup admin screen
    // TODO: update to simply call 'createUser' in HospiSysAdmin
    private static void setupAdmin(String path) throws IOException {
        // Frame setup
        JFrame frame = new JFrame("HospiSys - Setup Admin");
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
        viewPassword.addActionListener(e -> passwordEntry.setEchoChar((char)0));
        passwordPanel.add(new JLabel("Password: "));
        passwordPanel.add(passwordEntry);
        passwordPanel.add(viewPassword);

        // Repeat password entry
        JPanel repeatPasswordPanel = new JPanel();
        TextField repeatPasswordEntry = new TextField(20);
        repeatPasswordEntry.setEchoChar('*');
        JButton viewRepeatPassword = new JButton("Show");
        viewRepeatPassword.addActionListener(e -> repeatPasswordEntry.setEchoChar((char)0));
        passwordPanel.add(new JLabel("Repeat password: "));
        passwordPanel.add(repeatPasswordEntry);
        passwordPanel.add(viewRepeatPassword);

        JButton saveButton = new JButton("Save");
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

        frame.getContentPane().add(usernamePanel);
        frame.getContentPane().add(passwordPanel);
        frame.getContentPane().add(repeatPasswordPanel);
        frame.getContentPane().add(saveButton);

        frame.setVisible(true);
    }

    // Create folders function
    private static void createFolders(String path) {
        new File(path + "/HospiSys").mkdirs();
        new File(path + "/HospiSys/dat").mkdirs();
        new File(path + "/HospiSys/img").mkdirs();
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
        frame.getContentPane().setLayout(new MigLayout());
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

        // panel to display progress updates
        JPanel statusPanel = new JPanel(new GridLayout(0, 1));

        JButton nextButton = new JButton("Next");
        Font nextButtonFont = new Font(nextButton.getFont().getName(),nextButton.getFont().getStyle(),24);

        nextButton.setFont(nextButtonFont);
        nextButton.addActionListener(e -> {
            createFolders(jfc.getSelectedFile().getPath());
            statusPanel.add(new JLabel("- Folders created."));

            try {
                createHSDs(jfc.getSelectedFile().getPath());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            statusPanel.add(new JLabel("- Database files created."));

            statusPanel.revalidate();
            statusPanel.repaint();

            // change button function and text
            nextButton.setText("Done");
            nextButton.removeActionListener(nextButton.getActionListeners()[0]);
            nextButton.addActionListener(e1 -> {
                try {
                    frame.dispose();
                    setupAdmin(jfc.getSelectedFile().getPath());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });


        });


        frame.getContentPane().add(logo);
        frame.getContentPane().add(chooseFolderPanel, "wrap");
        frame.getContentPane().add(statusPanel);
        frame.getContentPane().add(nextButton, "span, align right");

        frame.setVisible(true);



    }

    // Start method
    // opens setup initialisation screen
    private static void start() throws IOException {

        // JFrame initialisation and configurations
        JFrame frame = new JFrame("HospiSys - Setup");
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new MigLayout());
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
        buttonsPanel.add(begin);
        buttonsPanel.add(close);

        frame.getContentPane().add(logo, "align center, span 3 3, wrap");
        frame.getContentPane().add(label, "align center, span 2, wrap");
        frame.getContentPane().add(close, "align left");
        frame.getContentPane().add(begin, "align right");

        frame.setVisible(true);
    }

    // Main method
    public static void main(String[] args) throws IOException {
        start();
    }
}
