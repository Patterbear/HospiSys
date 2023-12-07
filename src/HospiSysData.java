package src;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
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

    // Patient record deletion method
    // overwrites patient data with backslash values indicating it can be written over
    public void deletePatient(int id) throws IOException {
        for(int i = 1; i < patientLabels.length; i++) {
            editPatient(id, i, "\\");
        }
    }

    // Patient detail edit function
    // replaces a chosen field with new data
    public void editPatient(int id, int fieldNumber, String newData, JButton... searchButton) throws IOException {
        List<String> contents = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
        String[] record = HospiSysData.decryptRecord(contents.get(id).split("-"), "a", "a");
        record[fieldNumber] = newData;

        record = HospiSysData.encryptRecord(record, "a", "a");

        String recordString = Integer.toString(id);

        for (int i = 1; i < record.length; i++) {
            recordString += "-" + record[i];
        }
        contents.set(id, recordString);

        Files.write(file.toPath(), contents, StandardCharsets.UTF_8);
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
        return encryptedRecord;
    }


    // Format decrypted record function
    // makes fields originally in all caps more readable
    // EXAMPLE: 'ADDRESS@EMAIL.COM' -> 'address@email.com'
    private static String[] formatDecryptedRecord(String[] record) {
        // field indexes that don't need formatting
        int[] skip = {3, 4, 7, 8};


        // capitalises the first letters of fields (also after space)
        for (int i = 1; i < record.length; i++) {
            if(Arrays.binarySearch(skip, i) < 0) {
                String[] field = record[i].split(" ");
                String formattedField = "";

                for (int j = 0; j < field.length; j++) {
                    formattedField += field[j].substring(0, 1) + field[j].substring(1).toLowerCase() + " ";
                }
                record[i] = formattedField.substring(0, formattedField.length() - 1);

            }

            // email address to lower case
            record[8] = record[8].toLowerCase();

        }


        return record;
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
            decryptedRecord[i] = decryptedRecordSegment.replace("\\I", "J").replace("I\\", "J").replace("X\\", "").replace("Z\\", "");
        }
        
        return formatDecryptedRecord(decryptedRecord);

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
    public String[][] search(String category, String term, String username, String password) throws FileNotFoundException {
        int field = Arrays.asList(patientLabels).indexOf(category);
        List<List<String>> resultList = new ArrayList<List<String>>();

        String[] segregatedTerm = segregateRecordSegment(term);
        String encryptedTerm = "";
        for(int i = 0; i < segregatedTerm.length; i++) {
            if (isOnlyLetters(segregatedTerm[i])) {
                encryptedTerm += Playfair.encrypt(segregatedTerm[i], HospiSysAdmin.requestSystemKey(username, password));
            } else {
                encryptedTerm += segregatedTerm[i];
            }
        }

        term = encryptedTerm;

        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String[] record = formatRecord(scanner.nextLine());

            // ignores 'deleted' records
            if(record[1].equals("\\")) {
                continue;
            }

            if(term.equals("*")) {
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
        Scanner scanner = new Scanner(new File("dat/users.hsd"));
        String user;
        String[] result = new String[1];

        while (scanner.hasNextLine()) {

            user = scanner.nextLine();
            if (user.split("-")[0].equals(usernameHash)) {
                result = user.replace("\\", "").split("-");
                break;
            }
        }

        scanner.close();

        return result;
    }

    // Verify user function
    // verifies whether the given username and password is valid
    // checks using Playfair encryption using given password as key
    public boolean verifyUser(String username, String password) throws FileNotFoundException {
        String usernameHash = Playfair.encrypt(username, password).replace("\\", "");
        String passwordHash = Playfair.encrypt(password, password).replace("\\", "");
        String[] user = readUser(usernameHash);

        if(usernameHash.equals(user[0]) && passwordHash.equals(user[1])) {
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

    // Patient data de-encryption function
    // decrypts all data in preparation for re-encryption
    public static String[][] decryptPatientData(String username, String password) throws IOException {
        String[] data = Files.readAllLines(new File("dat/patients.hsd").toPath()).toArray(new String[0]);
        String[][] decryptedData = new String[data.length][patientLabels.length];

        // verify admin status
        if(HospiSysAdmin.verifyAdmin(Playfair.encrypt(username, password))) {
            for (int i = 0; i < data.length; i++) {
                String[] decryptedRecord = HospiSysData.decryptRecord(data[i].split("-"), username, password);
                for(int j = 0; j < patientLabels.length; j++) {
                    decryptedData[i][j] = decryptedRecord[j];
                }
            }
        }

        return decryptedData;
    }

    // Patient data re-encryption function
    // re-encrypts data and write to file
    public static void reencryptPatientData(String[][] data, String username, String password) throws IOException {
        String[] encryptedData = new String[data.length];

        // verify admin status
        if(HospiSysAdmin.verifyAdmin(Playfair.encrypt(username, password))) {
            // Re-encrypt data and write to file
            PrintWriter pw = new PrintWriter("dat/patients.hsd");
            for (int i = 0; i < data.length; i++) {
                pw.println(String.join("-", encryptRecord(data[i], username, password)));
            }
            pw.close();
        }
    }
}
