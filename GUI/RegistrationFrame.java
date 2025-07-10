
package GUI;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegistrationFrame extends JFrame {

    private JTextField nameField, emailField, ageField, heightField, weightField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, cancelButton;

    public RegistrationFrame() {
        setTitle("Sign up - TrackWell");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(125, 168, 221));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create Your Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        addLabel(panel, "Name:", gbc, 1);
        nameField = new JTextField();
        addField(panel, nameField, gbc, 1);

        addLabel(panel, "Email:", gbc, 2);
        emailField = new JTextField();
        addField(panel, emailField, gbc, 2);

        addLabel(panel, "Password:", gbc, 3);
        passwordField = new JPasswordField();
        addField(panel, passwordField, gbc, 3);

        addLabel(panel, "Confirm Password:", gbc, 4);
        confirmPasswordField = new JPasswordField();
        addField(panel, confirmPasswordField, gbc, 4);

        addLabel(panel, "Age:", gbc, 5);
        ageField = new JTextField();
        addField(panel, ageField, gbc, 5);

        addLabel(panel, "Height (cm):", gbc, 6);
        heightField = new JTextField();
        addField(panel, heightField, gbc, 6);

        addLabel(panel, "Weight (kg):", gbc, 7);
        weightField = new JTextField();
        addField(panel, weightField, gbc, 7);

        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");

        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(registerButton, gbc);
        gbc.gridx = 1;
        panel.add(cancelButton, gbc);

        add(panel);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();
                    String password = new String(passwordField.getPassword());
                    String confirmPassword = new String(confirmPasswordField.getPassword());
                    int age = Integer.parseInt(ageField.getText().trim());
                    int height = Integer.parseInt(heightField.getText().trim());
                    int weight = Integer.parseInt(weightField.getText().trim());

                    if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please fill in all required fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (!email.contains("@") || !email.endsWith(".com")) {
                        JOptionPane.showMessageDialog(null, "Invalid email. Must contain '@' and end with '.com'", "Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (!password.equals(confirmPassword)) {
                        JOptionPane.showMessageDialog(null, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/health_tracker", "root", "111111");

                    String insertUser = "INSERT INTO users (name, email, password, user_type) VALUES (?, ?, ?, 'TRAINEE')";
                    PreparedStatement userStmt = con.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
                    userStmt.setString(1, name);
                    userStmt.setString(2, email);
                    userStmt.setString(3, password);
                    int affectedRows = userStmt.executeUpdate();

                    if (affectedRows == 0) {
                        throw new SQLException("User registration failed. No rows affected.");
                    }

                    ResultSet generatedKeys = userStmt.getGeneratedKeys();
                    int userId = -1;
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve user_id.");
                    }

                    String insertTrainee = "INSERT INTO trainees (user_id, height, weight, age) VALUES (?, ?, ?, ?)";
                    PreparedStatement traineeStmt = con.prepareStatement(insertTrainee);
                    traineeStmt.setInt(1, userId);
                    traineeStmt.setInt(2, height);
                    traineeStmt.setInt(3, weight);
                    traineeStmt.setInt(4, age);
                    traineeStmt.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Registration successful! Redirecting to welcome page...");
                    dispose();
                    new WelcomePage();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(null, "Age, height, and weight must be numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Something went wrong: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");
                ageField.setText("");
                heightField.setText("");
                weightField.setText("");
                dispose();

            }
        });
    }

    private void addLabel(JPanel panel, String text, GridBagConstraints gbc, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel(text), gbc);
    }

    private void addField(JPanel panel, JComponent field, GridBagConstraints gbc, int y) {
        gbc.gridx = 1;
        gbc.gridy = y;
        panel.add(field, gbc);
    }
}
