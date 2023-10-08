package src;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;


// Main class
public class HospiSys {

    // 'Start' method
    // creates the initial login screen
    private static void start() {

        // login window JFrame initialisation and configurations
        JFrame frame = new JFrame("HospiSys - Login");
        frame.getContentPane().setLayout(new GridLayout(0, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        // frame panels
        JPanel usernamePanel = new JPanel();
        JPanel passwordPanel = new JPanel();
        JPanel loginButtonPanel = new JPanel();

        // labels and entry  boxes
        TextField usernameEntry = new TextField(10);
        TextField passwordEntry = new TextField(10);

        usernamePanel.add(new JLabel("Username:"));
        usernamePanel.add(usernameEntry);

        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(passwordEntry);


        // login button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login(usernameEntry.getText(), passwordEntry.getText(), frame));

        loginButtonPanel.add(loginButton);


        // add panels to frame
        frame.getContentPane().add(usernamePanel);
        frame.getContentPane().add(passwordPanel);
        frame.getContentPane().add(loginButtonPanel);

        frame.setVisible(true);
    }

    // login function
    // verifies login details and opens menu
    private static void login(String user, String password, JFrame frame) {
        System.out.println(user + password);
        frame.dispose();
        menu(user);
    }

    // Menu function
    // will open menu screen
    private static void menu(String user) {

        // menu window JFrame initialisation and configurations
        JFrame frame = new JFrame("HospiSys - Menu");
        frame.getContentPane().setLayout(new GridLayout(0, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        // frame panels
        JPanel patientPanel = new JPanel();
        JPanel staffPanel = new JPanel();
        JPanel logoutButtonPanel = new JPanel();

        // buttons
        JButton patientSearch = new JButton("Search");
        patientSearch.addActionListener(e -> search("Patient"));

        JButton addNewPatient = new JButton("Add New");
        addNewPatient.addActionListener(e -> addNew("Patient"));

        JButton staffSearch = new JButton("Search");
        staffSearch.addActionListener(e -> search("Staff"));

        JButton addNewStaff = new JButton("Add New");
        addNewStaff.addActionListener(e -> addNew("Staff"));

        JButton logout = new JButton("Log Out");
        logout.addActionListener(e -> {
            frame.dispose();
            start();
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
        frame.getContentPane().add(patientPanel);
        frame.getContentPane().add(staffPanel);
        frame.getContentPane().add(logoutButtonPanel);

        frame.setVisible(true);

    }

    private static void search(String category) {
        System.out.println(category);
    }

    private static void addNew(String category) {
        playfairEncrypt("Benjamin says hellos", "William");
        }


    // Remove char array duplicates function
    private static char[] removeDuplicates(char[] charArray) {

        String result = "";

        for (int i = 0; i < charArray.length; i++) {
            if (!result.contains(String.valueOf(charArray[i]))) {
                result += charArray[i];
            }
        }

        return result.toCharArray();
    }

    // Generate letters grid function
    private static char[][] generateGrid(char[] key) {
        key = removeDuplicates(key);
        String letters = "ABCDEFGHIKLMNOPQRSTUVWXYZ";

        for (int i = 0; i < key.length; i++) {
            letters = letters.replace(Character.toString(key[i]), "");
        }

        char[] lettersArray = (new String(key) + letters).toCharArray();

        char[][] lettersGrid = new char[5][5];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                lettersGrid[i][j] = lettersArray[(i * 5) + j];
            }
        }

        return lettersGrid;
    }


    // Letters grid search function
    // returns coordinates of given character in grid
    private static int[] searchGrid(char[][] grid, char c) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < 5; j++) {
                if(grid[i][j] == c) {
                    return new int[] {i, j};
                }
            }
        }
        return null;
    }


    // Print letters grid function
    private static void printGrid(char[][] grid) {
        String output = "";
        for (int i = 0; i < grid.length; i++) {
            output += Arrays.toString(grid[i]) + "\n";
        }

        System.out.println(output.substring(0, output.length() - 1)); // removes final new line
    }


    // Playfair string formatting function
    private static String playfairFormatString(String s) {
        if(s.length() % 2 != 0) {s+=" ";};
        String output = "";

        for (int i = 1; i < s.length(); i += 2) {
            output += s.charAt(i - 1);
            output += s.charAt(i);
            output += " ";
        }

        return output.substring(0, output.length() - 1);

    }


    // Playfair encryption method
    // returns cipher text created from given message and key
    private static String playfairEncrypt(String s, String k) {

        // format key
        char[] key = k.toUpperCase().replace("J", "I").toCharArray();

        // format message
        String message = s.toUpperCase().replace(" ", "").replace("J", "I").toUpperCase();
        System.out.println(message);

        // handle duplicates
        char[] messageChars = message.toCharArray();
        String messageNoDuplicates = Character.toString(messageChars[0]);

        for(int i = 1; i < message.length(); i++) {
            if ((i + 1) % 2 == 0 && messageChars[i] == messageChars[i - 1]) {
                messageNoDuplicates += "X";
            }

            messageNoDuplicates += messageChars[i];
        }

        // add 'Z' to end if odd number of letters
        if (messageNoDuplicates.length() % 2 != 0) {
            messageNoDuplicates += "Z";
        }

        System.out.println(playfairFormatString(messageNoDuplicates));

        // generate grid and update message char array
        char[][] lettersGrid = generateGrid(key);
        messageChars = messageNoDuplicates.toCharArray();

        // apply playfair rules
        String result = "";

        for (int i = 1; i < messageChars.length; i+=2) {
            int[] first = searchGrid(lettersGrid, messageChars[i - 1]);
            int[] second = searchGrid(lettersGrid, messageChars[i]);

            // same row, shift right
            if (first[0] == second[0]) {
                if (first[1] == 4) {
                    result += lettersGrid[first[0]][0]; // wraps around
                } else {
                    result += lettersGrid[first[0]][first[1] + 1];
                }

                if (second[1] == 4) {
                    result += lettersGrid[second[0]][0]; // wraps around
                } else {
                    result += lettersGrid[second[0]][second[1] + 1];
                }
            }

            // same column,shift down
            else if(first[1] == second[1]) {
                if (first[0] == 4) {
                    result += lettersGrid[0][first[1]]; // wraps around
                } else {
                    result += lettersGrid[first[0] + 1][first[1]];
                }

                if (second[0] == 4) {
                    result += lettersGrid[0][second[1]]; // wraps around
                } else {
                    result += lettersGrid[second[0] + 1][second[1]];
                }
            } else {
                // form rectangle and swap the letters with the ones on the end
                result += lettersGrid[first[0]][second[1]];
                result += lettersGrid[second[0]][first[1]];
            }
        }


        printGrid(lettersGrid);
        System.out.println(playfairFormatString(result));

        return key.toString();
    }


    // Main method
    public static void main(String[] args) {
        System.out.println("HospiSys is running...");

        start();
    }
}
