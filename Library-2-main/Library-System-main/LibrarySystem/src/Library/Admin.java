package Library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.table.TableColumnModel;


public class Admin {

	public void admin() {
	    JFrame frame = new JFrame("Admin Panel");
	    frame.setSize(400, 500);
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.setLocationRelativeTo(null);
	    frame.setLayout(new BorderLayout(10, 10));

	    // Title Panel
	    JPanel titlePanel = new JPanel();
	    titlePanel.setBackground(new Color(0, 123, 255)); //Bckground
	    JLabel titleLabel = new JLabel("Library System(Admin)");
	    titleLabel.setFont(new Font("Algerian", Font.BOLD, 24));
	    titleLabel.setForeground(Color.WHITE); //white text 
	    titlePanel.add(titleLabel);

	    // Clock Panel
	    JPanel clockPanel = new JPanel();
	    clockPanel.setBackground(new Color(200, 230, 255)); // background
	    JLabel clockLabel = new JLabel();
	    clockLabel.setFont(new Font("Arial", Font.BOLD, 16));
	    updateClock(clockLabel); // Initialize clock display
	    startClock(clockLabel); // Start updating the clock
	    clockPanel.add(clockLabel);

	    // Combine Title and Clock in Header Panel
	    JPanel headerPanel = new JPanel();
	    headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS)); // Vertical stacking
	    headerPanel.add(titlePanel);
	    headerPanel.add(clockPanel);

	    // Main panel for buttons
	    JPanel mainPanel = new JPanel();
	    mainPanel.setLayout(new GridLayout(4, 1, 10, 10)); // Adjusted for 4 buttons
	    mainPanel.setBackground(new Color(230, 230, 255)); // Light blue background

	    // Font for buttons
	    Font buttonFont = new Font("Arial", Font.BOLD, 14);

	    // Buttons
	    JButton addBookButton = new JButton("Add Book");
	    JButton removeBookButton = new JButton("Remove Book");
	    JButton viewInventoryButton = new JButton("View Inventory");
	    JButton logoutButton = new JButton("Logout");

	    // Style buttons
	    styleButton(addBookButton, buttonFont);
	    styleButton(removeBookButton, buttonFont);
	    styleButton(viewInventoryButton, buttonFont);
	    styleButton(logoutButton, buttonFont);

	    // Add buttons to the panel
	    mainPanel.add(addBookButton);
	    mainPanel.add(removeBookButton);
	    mainPanel.add(viewInventoryButton);
	    mainPanel.add(logoutButton);

	    // Add panels to the frame
	    frame.add(headerPanel, BorderLayout.NORTH); // Header (Title + Clock) at the top
	    frame.add(mainPanel, BorderLayout.CENTER);
        // Action Listeners
        addBookButton.addActionListener(e -> {
            JTextField bookNameField = new JTextField();
            JTextField authorField = new JTextField();
            JTextField yearField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
            panel.add(new JLabel("Book Name:"));
            panel.add(bookNameField);
            panel.add(new JLabel("Author:"));
            panel.add(authorField);
            panel.add(new JLabel("Year of Publication:"));
            panel.add(yearField);
            	
         // Create the JOptionPane and JDialog
            JOptionPane result = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = result.createDialog(frame, "Enter Book Details");
            dialog.setSize(400, 180); // Set custom size
            dialog.setVisible(true);

            // Get the user's choice from the JOptionPane
            Object selectedValue = result.getValue();
            if (selectedValue != null && (int) selectedValue == JOptionPane.OK_OPTION) {
                // Retrieve input values
                String bookName = bookNameField.getText().trim();
                String author = authorField.getText().trim();
                String year = yearField.getText().trim();

                if (!bookName.isEmpty() && !author.isEmpty() && !year.isEmpty()) {
                    try {
                        Integer.parseInt(year); // Validate year as a number
                        ArrayList<String[]> books = BookDatabase.readBooksFromFile();
                        boolean exists = books.stream().anyMatch(book -> book[0].equalsIgnoreCase(bookName));
                        if (exists) {
                            JOptionPane.showMessageDialog(frame, "Book already exists in the database.");
                        } else {
                            books.add(new String[]{bookName, author, year, "Available"});
                            BookDatabase.writeBooksToFile(books);
                            JOptionPane.showMessageDialog(frame, "Book added successfully!");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Year must be a valid number.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "All fields are required.");
                }
            }

        });

        removeBookButton.addActionListener(e -> {
            ArrayList<String[]> books = BookDatabase.readBooksFromFile();
            ArrayList<String> booksForRemoval = new ArrayList<>();

            // Add books with stock > 0 to the list for removal
            for (String[] book : books) {
                if (Integer.parseInt(book[4]) > 0) {  // Stock > 0 (even if borrowed)
                    booksForRemoval.add(book[0]); // Add book title to the list (not stock)
                }
            }

            if (booksForRemoval.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No books are available for removal.");
                return;
            }

            // Create a panel for search and selection (without stock information for users)
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            JLabel searchLabel = new JLabel("Search Book:");
            JTextField searchField = new JTextField();
            JList<String> bookList = new JList<>(booksForRemoval.toArray(new String[0]));
            JScrollPane scrollPane = new JScrollPane(bookList);

            panel.add(searchLabel, BorderLayout.NORTH);
            panel.add(searchField, BorderLayout.CENTER);
            panel.add(scrollPane, BorderLayout.SOUTH);

            // Filter books as the user types
            searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { filterList(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { filterList(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { filterList(); }

                private void filterList() {
                    String searchText = searchField.getText().toLowerCase();
                    DefaultListModel<String> filteredModel = new DefaultListModel<>();
                    for (String book : booksForRemoval) {
                        if (book.toLowerCase().contains(searchText)) {
                            filteredModel.addElement(book);
                        }
                    }
                    bookList.setModel(filteredModel);
                }
            });

            // Create the dialog for book selection
            JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = optionPane.createDialog(frame, "Select Book to Remove");
            dialog.setSize(400, 300); // Custom dialog size
            dialog.setVisible(true);

            // Get the user's choice
            Object selectedValue = optionPane.getValue();
            if (selectedValue != null && (int) selectedValue == JOptionPane.OK_OPTION) {
                String selectedBook = bookList.getSelectedValue();
                if (selectedBook == null) {
                    JOptionPane.showMessageDialog(frame, "No book selected.");
                    return;
                }

                // Remove the selected book from the list if it has stock > 0
                boolean isRemoved = books.removeIf(book -> book[0].equalsIgnoreCase(selectedBook.trim()) && Integer.parseInt(book[4]) > 0);
                if (isRemoved) {
                    // Save the updated books list to file after removal
                    BookDatabase.writeBooksToFile(books);
                    JOptionPane.showMessageDialog(frame, "Book removed: " + selectedBook);
                } else {
                    JOptionPane.showMessageDialog(frame, "Something went wrong while removing the book.");
                }
            }
        });




        viewInventoryButton.addActionListener(e -> {
            ArrayList<String[]> books = BookDatabase.readBooksFromFile();

            // Sort books alphabetically by title
            books.sort((a, b) -> a[0].compareToIgnoreCase(b[0]));

            // Define column names and initialize data array
            String[] columnNames = {"Title", "Author", "Year", "Borrowers", "Stock"};
            String[][] data = new String[books.size()][5];

            // Populate the data array
            for (int i = 0; i < books.size(); i++) {
                String[] book = books.get(i);
                data[i][0] = book[0]; // Title
                data[i][1] = book[1]; // Author
                data[i][2] = book[2]; // Year
                data[i][3] = "Click to View"; // Placeholder for Borrowers
                data[i][4] = book[4]; // Stock
            }

            // Create an editable JTable
            JTable table = new JTable(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column != 3; // Make Borrowers column non-editable
                }
            };

            table.setFont(new Font("Arial", Font.PLAIN, 14));
            table.setRowHeight(25);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

            // Adjust column widths
            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(400); // Title
            columnModel.getColumn(1).setPreferredWidth(200); // Author
            columnModel.getColumn(2).setPreferredWidth(70);  // Year
            columnModel.getColumn(3).setPreferredWidth(150); // Borrowers
            columnModel.getColumn(4).setPreferredWidth(70);  // Stock

            // Add scroll pane for the table
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(800, 300));

            // Add mouse listener to the table
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    int row = table.rowAtPoint(event.getPoint());
                    int column = table.columnAtPoint(event.getPoint());

                    if (column == 3) { // If Borrowers column is clicked
                        String title = (String) table.getValueAt(row, 0); // Get book title
                        ArrayList<String[]> borrowers = BookDatabase.getBorrowersForBook(title);

                        // Display borrowers in a new dialog with a JTable
                        if (borrowers != null && !borrowers.isEmpty()) {
                            // Create a table model for borrowers
                            String[] borrowerColumnNames = {"Username", "Date"};
                            String[][] borrowerData = new String[borrowers.size()][2];
                            for (int i = 0; i < borrowers.size(); i++) {
                                borrowerData[i][0] = borrowers.get(i)[0]; // Username
                                borrowerData[i][1] = borrowers.get(i)[1]; // Date
                            }

                            JTable borrowerTable = new JTable(borrowerData, borrowerColumnNames);
                            borrowerTable.setFont(new Font("Arial", Font.PLAIN, 14));
                            borrowerTable.setRowHeight(25);

                            // Add scroll pane for the borrower table
                            JScrollPane borrowerScrollPane = new JScrollPane(borrowerTable);
                            borrowerScrollPane.setPreferredSize(new Dimension(400, 200));

                            // Show the borrowers in a new dialog
                            JDialog borrowerDialog = new JDialog(frame, "Borrowers for: " + title, true);
                            borrowerDialog.setSize(450, 250);
                            borrowerDialog.setLocationRelativeTo(frame);
                            borrowerDialog.add(borrowerScrollPane);
                            borrowerDialog.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(frame, "No borrowers for this book.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            });

            // Save button to update book inventory
            JButton saveButton = new JButton("Save Changes");
            saveButton.setFont(new Font("Arial", Font.BOLD, 14));
            saveButton.addActionListener(saveEvent -> {
                try {
                    // Update books list with the edited data
                    for (int i = 0; i < books.size(); i++) {
                        books.get(i)[0] = (String) table.getValueAt(i, 0); // Title
                        books.get(i)[1] = (String) table.getValueAt(i, 1); // Author
                        books.get(i)[2] = (String) table.getValueAt(i, 2); // Year
                        books.get(i)[4] = (String) table.getValueAt(i, 4); // Stock
                    }

                    // Save updated data to the file
                    BookDatabase.writeBooksToFile(books);
                    JOptionPane.showMessageDialog(frame, "Changes saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Failed to save changes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Panel for the table and save button
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(saveButton, BorderLayout.SOUTH);

            // Show inventory in a modal dialog
            JDialog dialog = new JDialog(frame, "Edit Book Inventory", true);
            dialog.setSize(900, 400);
            dialog.setLocationRelativeTo(frame);
            dialog.add(panel);
            dialog.setVisible(true);
        });












        logoutButton.addActionListener(e -> {
            frame.dispose();
            new Login().login();
        });

        frame.setVisible(true);
    }

    private void styleButton(JButton button, Font font) {
        button.setFont(font);
        button.setBackground(new Color(0, 123, 255)); // Blue color
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(250, 40));
    }

    private void updateClock(JLabel clockLabel) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a");
        clockLabel.setText(format.format(new Date()));
    }

    private void startClock(JLabel clockLabel) {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> updateClock(clockLabel));
            }
        }, 0, 1000);
    }
}
