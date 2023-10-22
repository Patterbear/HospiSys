package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HospiSysData {

    private final File file;

    HospiSysData(String path) {
        this.file = new File(path);
    }

    public String readAll() throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String readout = "";

        while (scanner.hasNextLine()) {
            readout += scanner.nextLine() + "\n";
        }

        return readout;
    }

    public String[] get(int id) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String record = "";

        while (scanner.hasNextLine()) {

            record = scanner.nextLine();

            if (record.substring(0, 1).equals(Integer.toString(id))) {
                break;
            }
        }

        return record.split("-");
    }
}
