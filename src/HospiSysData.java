package src;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class HospiSysData {

    private final File file;

    public static String[] patientLabels = {
            "HospiSys ID",
            "Forename",
            "Surname",
            "NHS Number",
            "Date of Birth",
            "Address",
            "GP",
            "Telephone",
            "Email",
            "Location",
            "Conditions",
            "Medication",
            "Notes"
    };

    HospiSysData(String path) {
        this.file = new File(path);
    }

    public int nextId() throws IOException {
        return (int)Files.lines(file.toPath()).count();
    }


    // Encrypt record method
    // Playfair encrypts patient data ready for storing
    // make private and not static
    public static String[] encryptRecord(String[] record) {
        String[] encryptedRecord = new String[record.length];
        int[] ignore = {0, 3, 4, 5, 6, 7, 9, 10, 11};


        for (int i = 0; i < record.length; i++) {
            if(Arrays.binarySearch(ignore, i) < 0) {
                encryptedRecord[i] = Playfair.encrypt(record[i], "systemkey"); // temp key
            }

        }

        return encryptedRecord;
    }

    public String readAll() throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String readout = "";

        while (scanner.hasNextLine()) {
            readout += scanner.nextLine() + "\n";
        }

        return readout;
    }

    private String[] formatRecord(String record) {
        return record.split("-");
    }

    public String[] retrieve(int id) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String record = "";

        while (scanner.hasNextLine()) {

            record = scanner.nextLine();

            if (record.substring(0, 1).equals(Integer.toString(id))) {
                break;
            }
        }

        return formatRecord(record);
    }

    // Record search function
    // returns a list of records matching the given search parameters
    public String[][] search(String category, String term) throws FileNotFoundException {
        int field = Arrays.asList(patientLabels).indexOf(category);
        List<List<String>> resultList = new ArrayList<List<String>>();

        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String[] record = formatRecord(scanner.nextLine());

            if(term.equals("")) {
                resultList.add(List.of(record));
            }

            if(record[field].equals(term)) {
                resultList.add(List.of(record));
            }
        }

        // convert list into 2D string array
        String[][] results = new String[resultList.size()][];
        for (int i = 0; i < resultList.size(); i++) {
            List<String> result = resultList.get(i);
            results[i] = result.toArray(new String[result.size()]);
        }

        return results;

    }

    public void writeRecord(String[] record) throws IOException {
        // Generate and append id
        String id = Integer.toString(this.nextId()) + "-";
        Files.write(file.toPath(), id.getBytes(), StandardOpenOption.APPEND);

        for (int i = 0; i < record.length - 1; i++) {
            Files.write(file.toPath(), (record[i] + "-").getBytes(), StandardOpenOption.APPEND);
        }
        // Add final field and new line
        Files.write(file.toPath(), (record[record.length - 1] + "\n").getBytes(), StandardOpenOption.APPEND);

    }

    // Read user function
    // returns a user record if given username hash exists in file
    private String[] readUser(String usernameHash) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String user;
        String[] result = new String[1];

        while (scanner.hasNextLine()) {

            user = scanner.nextLine();
            if (user.split("-")[0].equals(usernameHash)) {
                result = user.split("-");
                break;
            }
        }

        return result;
    }

    // Verify user function
    // verifies whether the given username and password is valid
    // checks using Playfair encryption using given password as key
    public boolean verifyUser(String username, String password) throws FileNotFoundException {
        String[] user = readUser(Playfair.encrypt(username, password));

        if(Playfair.encrypt(username, password).equals(user[0]) && Playfair.encrypt(password, password).equals(user[1])) {
            return true;
        }
        return false;
    }

    // Write user function
    // encrypts and adds username and password to file
    public void writeUser(String username, String password) throws IOException {
        String hash = Playfair.encrypt(username, password) + "-" + Playfair.encrypt(password, password) + "\n";
        Files.write(file.toPath(), hash.getBytes(), StandardOpenOption.APPEND);
        JOptionPane.showMessageDialog(null, "User created successfully.");
    }
}
