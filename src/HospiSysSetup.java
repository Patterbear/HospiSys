package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// System setup class
public class HospiSysSetup {

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
        frame.getContentPane().setLayout(new GridLayout(0, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setResizable(false);
        frame.setIconImage(new ImageIcon("img/logo.png").getImage());

        // logo
        JLabel logo = new JLabel(new ImageIcon(ImageIO.read(new File("img/logo.png")).getScaledInstance(200, 200, Image.SCALE_FAST)));

        // Folder selection section
        JPanel chooseFolderPanel = new JPanel();
        TextField directoryEntry = new TextField(System.getProperty("user.home"), 30);
        JButton changeDirectoryButton = new JButton("Change");
        JFileChooser jfc = new JFileChooser(directoryEntry.getText());
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        changeDirectoryButton.addActionListener(e -> {
            jfc.showOpenDialog(null);
            directoryEntry.setText(jfc.getSelectedFile().getPath());
        });

        chooseFolderPanel.add(new JLabel("Install Folder:"));
        chooseFolderPanel.add(directoryEntry);
        chooseFolderPanel.add(changeDirectoryButton);

        JPanel statusPanel = new JPanel(new GridLayout(0, 1));

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            createFolders(jfc.getSelectedFile().getPath());
            statusPanel.add(new JLabel("Folders created."));

            try {
                createHSDs(jfc.getSelectedFile().getPath());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            statusPanel.add(new JLabel("Database files created."));

            statusPanel.revalidate();
            statusPanel.repaint();

            // change button text and function
            nextButton.setText("Finish");
            nextButton.removeActionListener(nextButton.getActionListeners()[0]);
            nextButton.addActionListener(e1 -> frame.dispose());
        });


        frame.getContentPane().add(logo);
        frame.getContentPane().add(chooseFolderPanel);
        frame.getContentPane().add(statusPanel);
        frame.getContentPane().add(nextButton);

        frame.setVisible(true);



    }

    // Start method
    // opens setup initialisation screen
    private static void start() throws IOException {
        // menu window JFrame initialisation and configurations
        JFrame frame = new JFrame("HospiSys - Setup");
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new GridLayout(0, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setResizable(false);
        frame.setIconImage(new ImageIcon("img/logo.png").getImage());

        // logo
        JLabel logo = new JLabel(new ImageIcon(ImageIO.read(new File("img/logo.png")).getScaledInstance(200, 200, Image.SCALE_FAST)));

        JPanel buttonsPanel = new JPanel();
        JButton begin = new JButton("Begin Setup");
        JButton close = new JButton("Exit");
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

        frame.getContentPane().add(logo);
        frame.getContentPane().add(buttonsPanel);

        frame.setVisible(true);
    }

    // Main method
    public static void main(String[] args) throws IOException {
        start();
    }
}
