
package GUI;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import model.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Login - TrackWell");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setJMenuBar(createMenuBar());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(125, 168, 221));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logo = new ImageIcon(getClass().getResource("logo.png"));
            Image scaledImage = logo.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH); // size it to fit!
            logo = new ImageIcon(scaledImage);

            logoLabel.setIcon(logo);
        } catch (Exception e) {
            logoLabel.setText("TrackWell");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(logoLabel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(125, 168, 221));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(125, 168, 221));
        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");

        loginButton.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                try {
                    String email = usernameField.getText();
                    String password = new String(passwordField.getPassword());

                    if (email.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter both username and password.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/health_tracker", "root", "111111");

                    PreparedStatement stmt = con.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?");
                    stmt.setString(1, email);
                    stmt.setString(2, password);

                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        int userId = rs.getInt("user_id");
                        String userType = rs.getString("user_type");

                        switch (userType.toUpperCase()) {
                            case "ADMIN":
                                AdminDashboard Ad = new AdminDashboard();
                                Ad.setVisible(true);
                                break;

                            case "TRAINEE":
                                String traineeName = rs.getString("name");

                                PreparedStatement infoStmt = con.prepareStatement("SELECT height, weight, age FROM trainees WHERE user_id = ?");
                                infoStmt.setInt(1, userId);
                                ResultSet infoRs = infoStmt.executeQuery();

                                if (infoRs.next()) {
                                    int height = infoRs.getInt("height");
                                    int weight = infoRs.getInt("weight");
                                    int age = infoRs.getInt("age");

                                    Trainee trainee = new Trainee(traineeName, email, password, height, weight, age);
                                    double bmi = trainee.calculateBMI();

                                    JOptionPane.showMessageDialog(null, "Login successful! Opening dashboard...");
                                    Dashboard dashboard = new Dashboard(email, userId, height, weight, bmi, age);
                                    dashboard.setVisible(true);
                                    dispose(); // Close login window
                                } else {
                                    JOptionPane.showMessageDialog(null, "Trainee data not found.");
                                }
                                break;

                            default:
                                JOptionPane.showMessageDialog(null, "Unknown user type.");
                        }

                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        JLabel signupLabel = new JLabel("New user? ");
        signupLabel.setForeground(Color.BLACK);

        JLabel signupLink = new JLabel("Sign Up");
        signupLink.setForeground(Color.BLUE);
        signupLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLink.addMouseListener(new MouseAdapter() {
            @Override

            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "Redirect to Sign Up Frame");
            }
        });

        JPanel signupPanel = new JPanel();
        signupPanel.setBackground(new Color(125, 168, 221));
        signupPanel.add(signupLabel);
        signupPanel.add(signupLink);

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(signupPanel);

        add(mainPanel);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenuItem aboutItem = new JMenuItem("About us");
        aboutItem.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "TrackWell is your health tracking buddy!");
            }
        });

        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Contact support@trackwell.com");
            }
        });

        menuBar.add(aboutItem);
        menuBar.add(helpItem);
        return menuBar;
    }

}
