package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static src.HospiSysAdmin.accessAdminInterface;
import static src.HospiSysAdmin.verifyAdmin;

// Main class
public class HospiSys {


    public static Image logoImage = new ImageIcon("img/logo.png").getImage();

    public static Font font = new Font(Font.DIALOG, Font.BOLD, 24);


    // 'Start' method
    // creates the initial login screen
    public static void start() throws IOException {
        // login window JFrame initialisation and configurations
        JFrame frame = new JFrame("HospiSys - Login");
        frame.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 350);
        frame.setResizable(false);
        frame.setIconImage(logoImage);

        // logo
        JLabel logo = new JLabel(new ImageIcon(logoImage.getScaledInstance(200, 150, Image.SCALE_FAST)));

        JLabel label = new JLabel("HospiSys v1.0");
        label.setFont(font);

        // entries section panel
        JPanel entriesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints entriesGbc = new GridBagConstraints();

        // Username entry
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(font);
        TextField usernameEntry = new TextField(20);
        usernameEntry.setFont(font);
        entriesGbc.gridx = 0;
        entriesGbc.gridy = 0;
        entriesPanel.add(usernameLabel, entriesGbc);
        entriesGbc.gridx = 1;
        entriesGbc.anchor = GridBagConstraints.WEST;
        entriesPanel.add(usernameEntry, entriesGbc);

        // Password entry
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(font);
        TextField passwordEntry = new TextField(20);
        passwordEntry.setFont(font);
        passwordEntry.setEchoChar('*');
        JButton viewPassword = new JButton("Show");
        //viewPassword.setFont(font);
        viewPassword.addActionListener(e -> passwordEntry.setEchoChar((char)0));
        entriesGbc.gridx = 0;
        entriesGbc.gridy = 1;
        entriesGbc.anchor = GridBagConstraints.CENTER;
        entriesPanel.add(passwordLabel, entriesGbc);
        entriesGbc.gridx = 1;
        entriesGbc.anchor = GridBagConstraints.WEST;
        entriesPanel.add(passwordEntry, entriesGbc);
        entriesGbc.gridx = 2;
        entriesGbc.insets = new Insets(10, 10, 10, 10);
        entriesPanel.add(viewPassword, entriesGbc);

        // login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(font);
        loginButton.addActionListener(e -> {
            try {
                login(usernameEntry.getText(), passwordEntry.getText(), frame);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // add logo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        frame.getContentPane().add(logo, gbc);

        // add username panel
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.gridwidth = 3;
        frame.getContentPane().add(entriesPanel, gbc);

        // add login button
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = 3;
        gbc.gridx = 2;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        frame.getContentPane().add(loginButton, gbc);

        frame.setVisible(true);

    }


    // login function
    // verifies login details and opens menu
    private static void login(String user, String password, JFrame frame) throws IOException {
        // temporary access
        if(user.equals("") && password.equals("")) {
            frame.dispose();
            menu();
            return;
        }

        HospiSysData hsd = new HospiSysData("dat/users.hsd");

        if(hsd.verifyUser(user, password)) {
            frame.dispose();
            if(verifyAdmin(Playfair.encrypt(user, password))) {
                accessAdminInterface(Playfair.encrypt(user, password));
            } else {
                menu();
            }

        } else {
            JOptionPane.showMessageDialog(null, "Incorrect Login");
            // hsd.writeUser(user, password);
        }

    }

    // Menu function
    // opens menu screen
    public static void menu() throws IOException {

        // menu window JFrame initialisation and configurations
        JFrame frame = new JFrame("HospiSys - Menu");
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new GridLayout(0, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setResizable(false);
        frame.setIconImage(logoImage);

        // logo
        JLabel logo = new JLabel(new ImageIcon(logoImage.getScaledInstance(200, 200, Image.SCALE_FAST)));


        // frame panels
        JPanel patientPanel = new JPanel();
        JPanel logoutButtonPanel = new JPanel();

        // buttons
        JButton patientSearch = new JButton("Search");
        patientSearch.addActionListener(e -> {
            try {
                search();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton addNewPatient = new JButton("Add New");
        addNewPatient.addActionListener(e -> {
            try {
                addNew();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

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

        logoutButtonPanel.add(logout);

        // add panels to frame
        frame.getContentPane().add(logo);
        frame.getContentPane().add(patientPanel);
        frame.getContentPane().add(logoutButtonPanel);

        frame.setVisible(true);

    }

    // Search patients screen
    // allows user to view records matching selected criteria
    private static void search() throws IOException {
        // JFrame setup
        JFrame frame = new JFrame("HospiSys - Search");
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new GridLayout(0, 1));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 400);
        frame.setResizable(false);
        frame.setIconImage(logoImage);

        // search bar and criteria dropdown
        JPanel searchPanel = new JPanel();
        TextField searchBar = new TextField(20);
        JComboBox criteriaDropdown = new JComboBox(HospiSysData.patientLabels);
        JButton searchButton = new JButton("Search");

        searchPanel.add(searchBar);
        searchPanel.add(criteriaDropdown);
        searchPanel.add(searchButton);

        // results panel
        JPanel resultsPanel = new JPanel(new GridLayout(0, 1));
        JScrollPane resultsScrollPane = new JScrollPane(resultsPanel);

        HospiSysData hsd = new HospiSysData("dat/patients.hsd");

        searchButton.addActionListener(e -> {
            resultsPanel.removeAll();
            try {
                String[][] results = hsd.search(HospiSysData.patientLabels[criteriaDropdown.getSelectedIndex()], searchBar.getText());
                for (int i = 0; i < results.length; i++) {
                    JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

                    // attributes for preview
                    resultPanel.add(new JLabel(results[i][0]));
                    resultPanel.add(new JLabel(results[i][1]));
                    resultPanel.add(new JLabel(results[i][2]));
                    resultPanel.add(new JLabel(results[i][4]));
                    resultPanel.add(new JLabel(results[i][5]));

                    JButton viewButton = new JButton("View");
                    int finalI = i;
                    viewButton.addActionListener(e1 -> {
                        try {
                            patientProfile(results[finalI]);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    resultPanel.add(viewButton);

                    resultsPanel.add(resultPanel);
                }
                resultsPanel.revalidate();
                resultsPanel.repaint();

                if (results.length == 0) {
                    JOptionPane.showMessageDialog(null, "No results found.");
                }


            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        frame.getContentPane().add(searchPanel);
        frame.getContentPane().add(resultsScrollPane);

        frame.setVisible(true);


    }

    // Add record screen
    private static void addNew() throws IOException {
        String[] labels = HospiSysData.patientLabels;

        HospiSysData hsd = new HospiSysData("dat/patients.hsd");

        // JFrame initialisation and configurations
        JFrame frame = new JFrame("HospiSys - New Patient");
        frame.setLocationRelativeTo(null);
        //frame.getContentPane().setLayout(new GridLayout(0, 1));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(480, 560);
        frame.setResizable(false);
        frame.setIconImage(logoImage);

        // current profile photo
        JPanel profilePhotoPanel = new JPanel();
        JLabel profilePhoto = new JLabel(new ImageIcon(ImageIO.read(new File("img/0.png")).getScaledInstance(200, 200, Image.SCALE_FAST)));

        // profile photo selection
        JButton uploadButton = new JButton("Upload");
        JFileChooser jfc = new JFileChooser();
        jfc.setSelectedFile(new File("img/0.png"));
        jfc.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "bmp", "webmp", "gif"));

        uploadButton.addActionListener(e -> {
            jfc.showOpenDialog(null);
            try {
                profilePhoto.setIcon(new ImageIcon(ImageIO.read(jfc.getSelectedFile()).getScaledInstance(200, 200, Image.SCALE_FAST)));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        profilePhotoPanel.add(profilePhoto);
        profilePhotoPanel.add(uploadButton);
        frame.getContentPane().add(profilePhotoPanel, BorderLayout.PAGE_START);

        TextField[] textFields = new TextField[labels.length - 1]; // id is auto generated so is excluded
        JPanel mainPanel = new JPanel(new GridLayout(0, 1));

        for (int i = 0; i < textFields.length; i++) {
            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new BorderLayout());
            TextField field = new TextField(20);

            textFields[i] = field;

            labelPanel.add(new JLabel(" " + labels[i + 1] + ": "), BorderLayout.LINE_START);
            labelPanel.add(field, BorderLayout.CENTER);

            mainPanel.add(labelPanel, BorderLayout.CENTER);
        }

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            // saving uploaded profile image
            try {
                Files.copy(Paths.get(jfc.getSelectedFile().getPath()), Paths.get("img/" + Integer.toString(hsd.nextId()) + ".png"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }


            // saving details
            String[] details = new String[textFields.length];
            for (int i = 0; i < textFields.length; i++) {

                // handles empty input
                if (textFields[i].getText().equals("")) {
                    details[i] = "N/A";
                } else {
                    details[i] = textFields[i].getText();
                }
            }

            try {
                hsd.writeRecord(details);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            frame.dispose();
            JOptionPane.showMessageDialog(null, "Patient created successfully.");
            try {
                patientProfile(hsd.retrieve(hsd.nextId() - 1)); // opens newly created patient profile
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        mainPanel.add(saveButton, BorderLayout.CENTER);
        frame.getContentPane().add(mainPanel);

        frame.setVisible(true);

    }

    // Patient profile screen
    private static void patientProfile(String[] patient) throws IOException {

        JFrame frame = new JFrame("HospiSys - " + patient[1] + " " + patient[2] + " (" + patient[3] + ")");
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(725, 350);
        frame.setIconImage(logoImage);

        GridBagConstraints gbc = new GridBagConstraints();

        String[] labels = HospiSysData.patientLabels;

        // Profile picture
        JLabel profilePhoto = new JLabel(new ImageIcon(ImageIO.read(new File("img/" + patient[0] +".png")).getScaledInstance(200, 200, Image.SCALE_FAST)));
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
            patientDetailsPanel.add(new JLabel("    " + labels[i] + ": " + patient[i]));
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


    // Main method
    public static void main(String[] args) throws IOException {
        System.out.println("HospiSys is running...");

        start();
    }
}
