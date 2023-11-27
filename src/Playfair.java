package src;

import java.util.Arrays;

// Playfair class
// contains various methods used to conduct Playfair encryption
public class Playfair {

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

    private static String removeJs(String s) {
        return s.replace("J", "I\\");
    }


    // Playfair string formatting function
    // inserts spaces between character pairs
    private static String formatString(String s) {
        if(s.length() % 2 != 0) {s+=" ";};
        String output = "";

        for (int i = 1; i < s.length(); i += 2) {
            output += s.charAt(i - 1);
            output += s.charAt(i);
            output += " ";
        }

        return output.substring(0, output.length() - 1);
    }


    // Playfair message formatting function
    private static char[] formatMessage(String message) {
        char[] messageChars = message.toUpperCase().replace(" ", "").toCharArray();

        String messageNoDuplicates = Character.toString(messageChars[0]);

        for(int i = 1; i < messageChars.length; i++) {
            if ((i + 1) % 2 == 0 && messageChars[i] == messageChars[i - 1]) {
                //messageNoDuplicates += "X";
                messageNoDuplicates += "X\\" + messageChars[i] + "X\\"; // double backslash indicates the character is a placeholder
            } else {
                messageNoDuplicates += messageChars[i];
            }

        }

        // add 'Z' to end if odd number of letters
        if (messageNoDuplicates.length() % 2 != 0) {
            //messageNoDuplicates += "Z";
            messageNoDuplicates += "Z\\"; // double backslash indicates the character is a placeholder
        }

        return removeJs(messageNoDuplicates).toCharArray();

    }


    // Playfair encryption method
    // returns cipher text created from given message and key
    public static String encrypt(String message, String key) {

        // format key
        char[] keyChars = key.toUpperCase().replace("J", "I").toCharArray();
        // swap '\' for '/' as '\' is used to indicate removable characters
        message = message.replace("\\", "/");

        // generate grid and update message char array
        char[][] lettersGrid = generateGrid(keyChars);
        char[] messageChars = formatMessage(message);

        // apply playfair rules
        String result = "";

        for (int i = 1; i < messageChars.length; i+=2) {
            int[] first = searchGrid(lettersGrid, messageChars[i - 1]);

            // skips over character if it's a '\' followed by 'I'
            if (messageChars[i] == '\\' && messageChars[i - 1] == 'I') {
                result += "\\";
                i++;
            }

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

            // ensures '\' is skipped over in loop as it is just indicator that character can be removed during decrypt
            if(messageChars[i] == 'X' || messageChars[i] == 'Z') {
                if(messageChars[i + 1] == '\\') {
                    result += '\\';
                    i++;
                }
            }

            if(messageChars[i] == 'I' && i < messageChars.length - 1) {
                if (messageChars[i + 1] == '\\') {
                    result += '\\';
                    i++;
                }
            }

        }
        return result;
    }


    // Playfair decrypt method
    // decrypts cipher text using given key
    public static String decrypt(String message, String key) {

        // format key
        char[] keyChars = key.toUpperCase().replace("J", "I").toCharArray();

        // generate grid
        char[][] lettersGrid = generateGrid(keyChars);
        char[] messageChars = message.toCharArray();

        String result = "";

        for (int i = 1; i < messageChars.length; i+=2) {
            int[] first = searchGrid(lettersGrid, messageChars[i - 1]);
            int[] second = searchGrid(lettersGrid, messageChars[i]);

            // same row, shift left
            if (first[0] == second[0]) {
                if (first[1] == 0) {
                    result += lettersGrid[first[0]][4]; // wraps around
                } else {
                    result += lettersGrid[first[0]][first[1] - 1];
                }

                if (second[1] == 0) {
                    result += lettersGrid[second[0]][4]; // wraps around
                } else {
                    result += lettersGrid[second[0]][second[1] - 1];
                }
            }

            // same column,shift up
            else if(first[1] == second[1]) {
                if (first[0] == 0) {
                    result += lettersGrid[4][first[1]]; // wraps around
                } else {
                    result += lettersGrid[first[0] - 1][first[1]];
                }

                if (second[0] == 0) {
                    result += lettersGrid[4][second[1]]; // wraps around
                } else {
                    result += lettersGrid[second[0] - 1][second[1]];
                }
            } else {
                // form rectangle and swap the letters with the ones on the end
                result += lettersGrid[first[0]][second[1]];
                result += lettersGrid[second[0]][first[1]];
            }
        }
        return result;
    }
}
