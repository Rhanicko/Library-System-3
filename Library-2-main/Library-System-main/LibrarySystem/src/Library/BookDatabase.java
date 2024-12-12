package Library;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class BookDatabase {

    private static final String FILE_PATH = "books.txt";
    private static final String BORROWERS_FILE_PATH = "borrowers.txt"; // File for borrowers

    static {
        initializeFile(FILE_PATH);
        initializeFile(BORROWERS_FILE_PATH);
    }

    private static void initializeFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error initializing file: " + filePath + " - " + e.getMessage());
            }
        }
    }

    public static ArrayList<String[]> readBooksFromFile() {
        ArrayList<String[]> books = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|"); // Split by the pipe without assuming spaces
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim(); // Remove any extra spaces
                }
                if (parts.length == 5) { // Title, Author, Year, Status, Stock
                    books.add(parts);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the book database: " + e.getMessage());
        }
        return books;
    }

    public static void writeBooksToFile(ArrayList<String[]> books) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String[] book : books) {
                bw.write(String.join(" | ", book));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to the book database: " + e.getMessage());
        }
    }

    public static ArrayList<String[]> getBorrowersForBook(String bookTitle) {
        ArrayList<String[]> borrowers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("borrowers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length == 3 && parts[0].equalsIgnoreCase(bookTitle)) {
                    borrowers.add(new String[]{parts[1], parts[2]}); // Add username and date
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return borrowers;
    }



    public static void addBorrower(String title, String borrower) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BORROWERS_FILE_PATH, true))) {
            bw.write(title.toLowerCase().trim() + " | " + borrower.trim());
            bw.newLine();
            System.out.println("Added borrower: Title = " + title + ", Borrower = " + borrower); // Debug message
        } catch (IOException e) {
            System.out.println("Error writing to the borrowers file: " + e.getMessage());
        }
    }

    public static HashMap<String, ArrayList<String>> getAllBorrowers() {
        HashMap<String, ArrayList<String>> borrowersMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(BORROWERS_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim(); // Remove any extra spaces
                }
                if (parts.length == 2) {
                    borrowersMap.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the borrowers file: " + e.getMessage());
        }
        return borrowersMap;
    }
}
