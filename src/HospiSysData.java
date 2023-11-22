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

    // Check if string contains only letters function
    // used to determine if encrypt is needed
    private static boolean isOnlyLetters(String s) {
        for (int i = 0; i < s.length(); i++) {
            if(!Character.isLetter(s.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    // Function to separate letters from other characters
    private static String[] segregateRecordSegment(String recordSegment) {
        ArrayList<String> segregatedRecordSegment = new ArrayList<String>();
        String segment = "";

        for (int i = 0; i < recordSegment.length(); i++) {
            // current char is  a letter, added to current segment
            if (Character.isLetter(recordSegment.charAt(i))) {
                segment += recordSegment.charAt(i);

                // if final char is a letter, segment is added then loop terminated
                if(i == recordSegment.length() - 1) {
                    segregatedRecordSegment.add(segment);
                    break;
                }
            } else { // non-letter found so segment and symbol added before segment resets
                if (!segment.equals("")) { // prevents adding empty segment
                    segregatedRecordSegment.add(segment);
                    segment = "";
                }
                segregatedRecordSegment.add(String.valueOf(recordSegment.charAt(i)));
            }
        }

        // although using '.length' is common, apparently '0' is more efficient with the JVM
        return segregatedRecordSegment.toArray(new String[0]);
    }

    // Encrypt record method
    // Playfair encrypts patient data ready for storing IF their account is authorised
    // make private and not static
    public static String[] encryptRecord(String[] record, String username, String password) throws FileNotFoundException {
        String[] encryptedRecord = new String[record.length];
        String key = HospiSysAdmin.requestSystemKey(username, password);
        if (key == null) {
            return null;
        }

        // calls method to segregate letter groups from symbols
        for (int i = 0; i < record.length; i++) {
            String[] segregatedRecord = segregateRecordSegment(record[i]);
            String encryptedRecordSegment = "";
            for(int j = 0; j < segregatedRecord.length; j++) {
                if (isOnlyLetters(segregatedRecord[j])) {
                    encryptedRecordSegment += Playfair.encrypt(segregatedRecord[j], key);
                } else {
                    encryptedRecordSegment += segregatedRecord[j];
                }
            }
            encryptedRecord[i] = encryptedRecordSegment;
        }

        System.out.println(Arrays.toString(encryptedRecord));
        System.out.println(Arrays.toString(decryptRecord(encryptedRecord, username, password)));

        return encryptedRecord;
    }

    // Decrypt record method
    // Playfair decrypts patient data ready for viewing IF their account is authorised
    public static String[] decryptRecord(String[] record, String username, String password) throws FileNotFoundException {
        String[] decryptedRecord = new String[record.length];
        String key = HospiSysAdmin.requestSystemKey(username, password);
        if (key == null) {
            return null;
        }
        // calls method to segregate letter groups from symbols
        for (int i = 0; i < record.length; i++) {
            String[] segregatedRecord = segregateRecordSegment(record[i]);
            String decryptedRecordSegment = "";
            for(int j = 0; j < segregatedRecord.length; j++) {
                if (isOnlyLetters(segregatedRecord[j])) {
                    decryptedRecordSegment += Playfair.decrypt(segregatedRecord[j], key);
                } else {
                    decryptedRecordSegment += segregatedRecord[j];
                }
            }
            decryptedRecord[i] = decryptedRecordSegment;
        }
        
        return decryptedRecord;

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

            encryptRecord(record, "a", "a"); // temporary use for testing

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
        write(id);

        for (int i = 0; i < record.length - 1; i++) {
            write(record[i] + "-");
        }
        // Add final field and new line
        write(record[record.length - 1] + "\n");

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
        write(hash);
        JOptionPane.showMessageDialog(null, "User created successfully.");
    }

    public void write(String s) throws IOException {
        Files.write(file.toPath(), s.getBytes(), StandardOpenOption.APPEND);
    }

    public void encryptedWrite(String s, String key) throws IOException {
        write(Playfair.encrypt(s, key) + "\n");
    }
}
