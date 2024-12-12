package Library;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Login {
    private static final HashMap<String, String> credentials = new HashMap<>();

    static {
        // Initialize credentials
        credentials.put("admin", ""); // Placeholder password
        credentials.put("user", "");   // Placeholder password
    }

    public void login() {
        JFrame frame = new JFrame("Library System Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        // Load the background image
        ImageIcon backgroundIcon = new ImageIcon("loginbackground.jpg");
        Image backgroundImg = backgroundIcon.getImage().getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH);
        JLabel backgroundLabel = new JLabel(new ImageIcon(backgroundImg));
        backgroundLabel.setLayout(new GridBagLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 0, 128, 150)); // Semi-transparent blue
        JLabel headerLabel = new JLabel("Library System");
        headerLabel.setFont(new Font("Algerian", Font.BOLD, 24));
        headerLabel.setForeground(Color.white);
        headerPanel.add(headerLabel);

        // Login Components
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.white);
        JTextField userText = new JTextField(20);
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.white);
        JPasswordField passText = new JPasswordField(20);
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JButton loginButton = new JButton("Login");
        JLabel messageLabel = new JLabel("");

        // Layout setup
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add Header
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        backgroundLabel.add(headerPanel, gbc);

        // Add user Label and Field
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        backgroundLabel.add(userLabel, gbc);
        gbc.gridx = 1;
        backgroundLabel.add(userText, gbc);

        // Add Password Label and Field
        gbc.gridy = 2;
        gbc.gridx = 0;
        backgroundLabel.add(passLabel, gbc);
        gbc.gridx = 1;
        backgroundLabel.add(passText, gbc);

        // Add Login Button
        gbc.gridy = 3;
        gbc.gridx = 1;
        backgroundLabel.add(loginButton, gbc);

        // Add Message Label
        gbc.gridy = 4;
        gbc.gridx = 1;
        backgroundLabel.add(messageLabel, gbc);

        frame.add(backgroundLabel);

        // Login button action listener
        loginButton.addActionListener(e -> {
            String username = userText.getText().trim();
            String password = new String(passText.getPassword());

            if (credentials.containsKey(username) && credentials.get(username).equals(password)) {
                frame.dispose();
                if (username.equals("admin")) {
                    new Admin().admin();
                } else {
                    new User(username).user();
                }
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Invalid username or password. Try again.");
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Login().login();
    }
}
