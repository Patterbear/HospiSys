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
import java.util.Arrays;


// Main class
public class HospiSys {

    public static Image logoImage = new ImageIcon("img/logo.png").getImage();

    public static Font font = new Font(Font.DIALOG, Font.BOLD, 24);


    // Build screen function
    // creates a JFrame with some standard configurations
    public static JFrame buildScreen(String title, int width, int height, boolean exit) {
        JFrame frame = new JFrame("HospiSys - " + title);
        frame.setSize(width, height);
        frame.setIconImage(logoImage);
        frame.setResizable(false);
        if(exit) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else {
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

        return frame;
    }

    // 'Start' method
    // creates the initial login screen
    public static void start() throws IOException {
        // login window JFrame initialisation and configurations
        JFrame frame = buildScreen("Login", 700, 350, true);
        frame.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // logo
        JLabel logo = new JLabel(new ImageIcon(logoImage.getScaledInstance(200, 150, Image.SCALE_FAST)));

        // currently unused
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
        viewPassword.addActionListener(e -> {
            if (passwordEntry.getEchoChar() == '*') {
                passwordEntry.setEchoChar((char)0);
                viewPassword.setText("Hide");
            } else {
                passwordEntry.setEchoChar('*');
                viewPassword.setText("Show");
            }
        });
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

        // bottom buttons panel
        JPanel bottomButtonsPanel = new JPanel();

        // exit button
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(font);
        exitButton.addActionListener(e -> frame.dispose());
        bottomButtonsPanel.add(exitButton);


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
        bottomButtonsPanel.add(loginButton);

        // add logo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        frame.getContentPane().add(logo, gbc);

        // add entries panel
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.gridwidth = 3;
        frame.getContentPane().add(entriesPanel, gbc);

        // add login and exit buttons
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = 3;
        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        frame.getContentPane().add(bottomButtonsPanel, gbc);

        // temporary
        usernameEntry.setText("a");
        passwordEntry.setText("a");

        frame.setVisible(true);

    }


    // login function
    // verifies login details and opens menu
    private static void login(String user, String password, JFrame frame) throws IOException {

        HospiSysData hsd = new HospiSysData("dat/users.hsd");

        if(hsd.verifyUser(user, password)) {
            frame.dispose();
            if(HospiSysAdmin.verifyAdmin(Playfair.encrypt(user, password))) {
                HospiSysAdmin.accessAdminInterface(user, password);
            } else {
                menu(user, password);
            }

        } else {
            JOptionPane.showMessageDialog(null, "Incorrect Login");
        }

    }

    // Menu function
    // opens menu screen
    public static void menu(String username, String password) throws IOException {

        // menu window JFrame initialisation and configurations
        JFrame frame = buildScreen("Menu", 300, 300, true);
        frame.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // logo
        JLabel logo = new JLabel(new ImageIcon(logoImage.getScaledInstance(200, 150, Image.SCALE_FAST)));

        // buttons
        JButton patientSearch = new JButton("Search Patients");
        patientSearch.addActionListener(e -> {
            try {
                search(username, password);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton addNewPatient = new JButton("New Patient");
        addNewPatient.addActionListener(e -> {
            try {
                addNew(username, password);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton logout = new JButton("Log Out");
        logout.setFont(font);
        logout.addActionListener(e -> {
            frame.dispose();
            try {
                start();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // add logo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.getContentPane().add(logo, gbc);

        // add search button
        gbc.insets = new Insets(0, 0, 10, 10);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.getContentPane().add(patientSearch, gbc);

        // add new patient button
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.gridx = 1;
        frame.getContentPane().add(addNewPatient, gbc);

        // add log out button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        frame.getContentPane().add(logout, gbc);

        frame.setVisible(true);

    }

    // Search patients screen
    // allows user to view records matching selected criteria
    private static void search(String username, String password) throws IOException {
        // JFrame setup;
        JFrame frame = buildScreen("Search", 700, 400, false);
        frame.getContentPane().setLayout(new GridLayout(0, 1));

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
                String[][] results = hsd.search(HospiSysData.patientLabels[criteriaDropdown.getSelectedIndex()], searchBar.getText(), username, password);
                for (int i = 0; i < results.length; i++) {
                    JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

                    results[i] = HospiSysData.decryptRecord(results[i], username, password);

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
                            patientProfile(results[finalI], username, password, searchButton);
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
    private static void addNew(String username, String password) throws IOException {
        String[] labels = HospiSysData.patientLabels;

        HospiSysData hsd = new HospiSysData("dat/patients.hsd");

        // JFrame initialisation and configurations
        JFrame frame = buildScreen("New Patient", 480, 630, false);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // current profile photo
        JLabel profilePhoto = new JLabel(new ImageIcon(ImageIO.read(new File("img/0.png")).getScaledInstance(200, 200, Image.SCALE_FAST)));
        gbc.gridwidth = 2;
        frame.getContentPane().add(profilePhoto, gbc);

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

        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridy = 1;
        frame.getContentPane().add(uploadButton, gbc);

        // labels and corresponding text entries
        TextField[] textFields = new TextField[labels.length - 1]; // id is auto generated so is excluded

        // reset necessary constraints
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 0, 0);

        for (int i = 0; i < textFields.length; i++) {
            TextField field = new TextField(20);

            textFields[i] = field;

            gbc.gridy = i + 2;
            gbc.gridx = 0;
            frame.getContentPane().add(new JLabel(" " + labels[i + 1] + ": "), gbc);

            gbc.gridx = 1;
            frame.getContentPane().add(field, gbc);
        }

        JButton saveButton = new JButton("Save");
        saveButton.setFont(font);
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
                details = HospiSysData.encryptRecord(details, username, password);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }

            try {
                hsd.writeRecord(details);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            frame.dispose();
            JOptionPane.showMessageDialog(null, "Patient created successfully.");
            try {
                patientProfile(HospiSysData.decryptRecord(hsd.retrieve(hsd.nextId() - 1), username, password), username, password); // opens newly created patient profile
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // add save button below
        gbc.gridx = 0;
        gbc.gridy = labels.length + 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 0, 0);
        frame.getContentPane().add(saveButton, gbc);

        frame.setVisible(true);

    }


    // Edit patient screen
    // allows user to edit patient details
    private static void editPatient(int id, String username, String password, JFrame parent, HospiSysData hsd, JButton... searchButton) {
        JFrame frame = buildScreen("Edit Data", 400, 210, false);
        frame.setLayout(new GridLayout(0, 1));

        // fields to de displayed in dropdown (id excluded)
        String[] fields = Arrays.copyOfRange(HospiSysData.patientLabels, 1, HospiSysData.patientLabels.length);

        JLabel label = new JLabel("Choose field to edit");
        label.setFont(font);
        label.setBorder(new EmptyBorder(0,40,0,0));

        JPanel fieldEditPanel = new JPanel();
        TextField dataEntry = new TextField(20);
        JComboBox fieldDropdown = new JComboBox(fields);

        fieldEditPanel.add(dataEntry);
        fieldEditPanel.add(fieldDropdown);

        // buttons
        JPanel buttonsPanel = new JPanel();
        JButton back = new JButton("Back");
        back.setFont(font);
        back.addActionListener(e -> frame.dispose());

        JButton save = new JButton("Save");
        save.setFont(font);
        save.addActionListener(e -> {
            try {
                String newData = dataEntry.getText();
                new HospiSysData("dat/patients.hsd").editPatient(id, fieldDropdown.getSelectedIndex() + 1, newData);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            frame.dispose();
            parent.dispose();
            try {
                patientProfile(HospiSysData.decryptRecord(hsd.retrieve(id), username, password), username, password);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        buttonsPanel.add(back);
        buttonsPanel.add(save);

        frame.getContentPane().add(label);
        frame.getContentPane().add(fieldEditPanel);
        frame.getContentPane().add(buttonsPanel);

        frame.setVisible(true);
    }


    // Patient profile screen
    private static void patientProfile(String[] patient, String username, String password, JButton... searchButton) throws IOException {
        JFrame frame = buildScreen(patient[1] + " " + patient[2] + " (" + patient[3] + ")", 800, 325, false);
        frame.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        String[] labels = HospiSysData.patientLabels;

        // Profile picture
        JLabel profilePhoto = new JLabel(new ImageIcon(ImageIO.read(new File("img/" + patient[0] +".png")).getScaledInstance(200, 200, Image.SCALE_FAST)));

        // add profile photo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 13;
        gbc.insets = new Insets(0, 10, 0, 10);

        frame.getContentPane().add(profilePhoto, gbc);

        // patient labels and details immediately to the right of image
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        for (int i = 0; i < patient.length; i++) {
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.WEST;
            frame.getContentPane().add(new JLabel(labels[i] + ": "), gbc);
            gbc.gridx++;
            frame.getContentPane().add(new JLabel(patient[i]), gbc);
            gbc.gridx--;

        }

        // button panel
        JPanel buttons = new JPanel();

        // delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(font);
        deleteButton.addActionListener(e -> {
            // pop up gives the user option to delete patient
            int choice = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete this patient?",
                    "Delete Patient",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(choice == JOptionPane.YES_OPTION) {
                HospiSysData hsd = new HospiSysData("dat/patients.hsd");
                try {
                    hsd.deletePatient(Integer.parseInt(patient[0]), HospiSysAdmin.requestSystemKey(username, password));

                    // refreshes results on search screen
                    if(searchButton.length == 1) {
                        searchButton[0].doClick();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                frame.dispose();
            }
        });

        // edit button
        JButton editButton = new JButton("Edit");
        editButton.setFont(font);
        editButton.addActionListener(e -> editPatient(Integer.parseInt(patient[0]), username, password, frame, new HospiSysData("dat/patients.hsd"), searchButton));

        // done button
        JButton doneButton = new JButton("Done");
        doneButton.setFont(font);
        doneButton.addActionListener(e -> frame.dispose());


        // adding buttons
        buttons.add(deleteButton);
        buttons.add(editButton);
        buttons.add(doneButton);
        gbc.gridx = 2;
        gbc.gridy++;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(5, 5, 0, 5);
        frame.getContentPane().add(buttons, gbc);


        frame.setVisible(true);

    }


    // Main method
    public static void main(String[] args) throws IOException {
        System.out.println("HospiSys is running...");

        start();
    }

}
