package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

// Main class
public class HospiSys {

    // 'Start' method
    // creates the initial login screen
    static void start() throws IOException {

        // login window JFrame initialisation and configurations
        JFrame frame = new JFrame("HospiSys - Login");
        frame.getContentPane().setLayout(new GridLayout(0, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setResizable(false);
        frame.setIconImage(new ImageIcon("img/logo.png").getImage());

        // logo
        JLabel logo = new JLabel(new ImageIcon(ImageIO.read(new File("img/logo.png")).getScaledInstance(200, 200, Image.SCALE_FAST)));


        // frame panels
        JPanel usernamePanel = new JPanel();
        JPanel passwordPanel = new JPanel();
        JPanel loginButtonPanel = new JPanel();

        // labels and entry  boxes
        TextField usernameEntry = new TextField(10);
        TextField passwordEntry = new TextField(10);
        passwordEntry.setEchoChar('*');

        usernamePanel.add(new JLabel("Username:"));
        usernamePanel.add(usernameEntry);

        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(passwordEntry);


        // login button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            try {
                login(usernameEntry.getText(), passwordEntry.getText(), frame);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        loginButtonPanel.add(loginButton);


        // add panels to frame
        frame.getContentPane().add(logo);
        frame.getContentPane().add(usernamePanel);
        frame.getContentPane().add(passwordPanel);
        frame.getContentPane().add(loginButtonPanel);

        frame.setVisible(true);
    }

    // login function
    // verifies login details and opens menu
    private static void login(String user, String password, JFrame frame) throws IOException {
        frame.dispose();
        if(!user.equals("") && !password.equals("")) {
            String msg = Playfair.encrypt(user, password);
            System.out.println(Playfair.decrypt(msg, password));

        }
        menu(user);
    }

    // Menu function
    // will open menu screen
    private static void menu(String user) throws IOException {

        // menu window JFrame initialisation and configurations
        JFrame frame = new JFrame("HospiSys - Menu");
        frame.getContentPane().setLayout(new GridLayout(0, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setResizable(false);
        frame.setIconImage(new ImageIcon("img/logo.png").getImage());

        // logo
        JLabel logo = new JLabel(new ImageIcon(ImageIO.read(new File("img/logo.png")).getScaledInstance(200, 200, Image.SCALE_FAST)));


        // frame panels
        JPanel patientPanel = new JPanel();
        JPanel staffPanel = new JPanel();
        JPanel logoutButtonPanel = new JPanel();

        // buttons
        JButton patientSearch = new JButton("Search");
        patientSearch.addActionListener(e -> {
            try {
                search("Patient");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton addNewPatient = new JButton("Add New");
        addNewPatient.addActionListener(e -> addNew("Patient"));

        JButton staffSearch = new JButton("Search");
        staffSearch.addActionListener(e -> {
            try {
                search("Staff");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton addNewStaff = new JButton("Add New");
        addNewStaff.addActionListener(e -> addNew("Staff"));

        JButton logout = new JButton("Log Out");
        logout.addActionListener(e -> {
            frame.dispose();
            try {
                start();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
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
        frame.getContentPane().add(logo);
        frame.getContentPane().add(patientPanel);
        frame.getContentPane().add(staffPanel);
        frame.getContentPane().add(logoutButtonPanel);

        frame.setVisible(true);

    }

    private static void search(String category) throws IOException {
        patientProfile(0);
        patientProfile(1);
    }

    private static void addNew(String category) {
        Playfair.encrypt("Benjamin says hellos", "William");
    }

    // Patient profile screen
    private static void patientProfile(int id) throws IOException {

        // Load patient details
        HospiSysData hsd = new HospiSysData("dat/patients.hsd");
        String[] patientDetails = hsd.get(id);

        System.out.println(Arrays.toString(HospiSysData.encryptRecord(patientDetails)));

        JFrame frame = new JFrame("HospiSys - " + patientDetails[1] + " " + patientDetails[2] + " (" + patientDetails[3] + ")");
        frame.getContentPane().setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 350);
        frame.setIconImage(new ImageIcon("img/logo.png").getImage());

        GridBagConstraints gbc = new GridBagConstraints();

        String[] labels = {
                "HospiSys ID",
                "Forename",
                "Surname",
                "NHS Number",
                "Address",
                "GP",
                "Telephone",
                "Email",
                "Location",
                "Conditions",
                "Medication",
                "Notes"
        };

        // Profile picture
        JLabel profilePhoto = new JLabel(new ImageIcon(ImageIO.read(new File("img/" + patientDetails[0] +".png")).getScaledInstance(200, 200, Image.SCALE_FAST)));
        profilePhoto.setBorder(new EmptyBorder(20,0,0,0));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        frame.getContentPane().add(profilePhoto, gbc);

        // Patient details
        JPanel patientDetailsPanel = new JPanel(new GridLayout(labels.length, 0));
        patientDetailsPanel.setBorder(new EmptyBorder(10,0,0,0));

        for (int i = 0; i < labels.length; i++) {
            patientDetailsPanel.add(new JLabel("    " + labels[i] + ": " + patientDetails[i]));
        }

        gbc.gridx = 1;
        gbc.gridheight = 3;
        gbc.gridwidth = 2;
        frame.getContentPane().add(patientDetailsPanel, gbc);

        // Edit and Delete Buttons
        JPanel buttonsPanel = new JPanel();

        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");


        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;

        frame.getContentPane().add(buttonsPanel, gbc);

        // Done button
        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(e -> frame.dispose());


        gbc.gridy = 4;

        frame.getContentPane().add(doneButton, gbc);


        frame.setVisible(true);
    }

    private static void staffProfile(int id) {
        JFrame frame = new JFrame("HospiSys - Example Patient");
        frame.getContentPane().setLayout(new GridLayout(0, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        String[] exampleDetails = {
                "Example",
                "Staff",
                "Example Role",
                "Example Hospital",
                "Example Ward",
                "example@nhs.uk"
        };

        frame.setVisible(true);
    }


    // Main method
    public static void main(String[] args) throws IOException {
        System.out.println("HospiSys is running...");

        start();
    }
}
