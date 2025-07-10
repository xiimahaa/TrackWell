
package GUI;



import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class Dashboard extends JFrame {

    private final int userId;
    private final String userName;
    private DefaultTableModel subscriptionTableModel;
    private JTable subscriptionTable;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JLabel heightLabel, weightLabel, bmiLabel, ageLabel;
    private DefaultListModel<String> activityListModel;
    private JComboBox<String> planTypeCombo;
    private JComboBox<String> paymentMethodCombo;
    private DefaultTableModel activityTableModel;

    public Dashboard(String userName, int userId, double height, double weight, double bmi, int age) {
        this.userId = userId;
        this.userName = userName;

        setTitle("Health Tracker - Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(180, getHeight()));
        sidebar.setBackground(new Color(50, 63, 85));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("TrackWell", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton metricsBtn = createNavButton("Metrics");
        JButton activitiesBtn = createNavButton("Activities");
        JButton notificationsBtn = createNavButton("Notifications");
        JButton subscriptionBtn = createNavButton("Subscription");

        JButton logoutBtn = new JButton("Log out");
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(Color.RED);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(title);
        sidebar.add(Box.createVerticalStrut(40));
        sidebar.add(metricsBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(activitiesBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(notificationsBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(subscriptionBtn);
        sidebar.add(Box.createVerticalGlue());

        String displayName = userName.contains("@") ? userName.substring(0, userName.indexOf("@")) : userName;
        JLabel userLabel = new JLabel("Welcome " + displayName + " !");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(userLabel);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(logoutBtn);

        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.add(buildMetricsPanel(height, weight, bmi, age), "Metrics");
        contentPanel.add(buildActivitiesPanel(), "Activities");
        contentPanel.add(buildNotificationsPanel(), "Notifications");
        contentPanel.add(buildSubscriptionPanel(), "Subscription");

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        metricsBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "Metrics");
            }
        });
        activitiesBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                loadActivities();

                cardLayout.show(contentPanel, "Activities");
            }
        });
        notificationsBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "Notifications");
            }
        });
        subscriptionBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "Subscription");
            }
        });
        logoutBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                {
                    int confirm = JOptionPane.showConfirmDialog(Dashboard.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        dispose();
                    }
                }
            }
        });

        setVisible(true);
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(150, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return btn;
    }

    private JLabel createMetric(String title, String value) {
        JLabel label = new JLabel("<html><b>" + title + ":</b> " + value + "</html>", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        return label;
    }

    private JPanel buildMetricsPanel(double height, double weight, double bmi, int age) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel header = new JLabel("Dashboard - Metrics", SwingConstants.LEFT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));
        panel.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        grid.setBackground(Color.WHITE);

        heightLabel = createMetric("Height", height + " cm");
        weightLabel = createMetric("Weight", weight + " kg");
        bmiLabel = createMetric("BMI", String.format("%.1f", bmi));
        ageLabel = createMetric("Age", age + " years");

        grid.add(heightLabel);
        grid.add(weightLabel);
        grid.add(bmiLabel);
        grid.add(ageLabel);

        JButton updateBtn = new JButton("Update Metrics");
        updateBtn.setBackground(Color.BLACK);
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false);

        updateBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                updateBtn.setBackground(new Color(70, 130, 180));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                updateBtn.setBackground(Color.BLACK);
            }
        });

        panel.add(updateBtn, BorderLayout.SOUTH);

        updateBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                openMetricsDialog();
            }
        });

        panel.add(grid, BorderLayout.CENTER);
        panel.add(updateBtn, BorderLayout.SOUTH);
        return panel;
    }

    private void openMetricsDialog() {
        JDialog dialog = new JDialog(this, "Update Your Metrics", true);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(new Color(125, 168, 221));
        setResizable(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel heightLbl = new JLabel("Height (cm):");
        JLabel weightLbl = new JLabel("Weight (kg):");
        JLabel ageLbl = new JLabel("Age:");

        heightLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        weightLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ageLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextField heightField = new JTextField(10);
        JTextField weightField = new JTextField(10);
        JTextField ageField = new JTextField(10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(heightLbl, gbc);
        gbc.gridx = 1;
        dialog.add(heightField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(weightLbl, gbc);
        gbc.gridx = 1;
        dialog.add(weightField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(ageLbl, gbc);
        gbc.gridx = 1;
        dialog.add(ageField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(125, 168, 221));

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.setBackground(new Color(34, 139, 34));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);

        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(buttonPanel, gbc);

        saveBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                try {
                    double h = Double.parseDouble(heightField.getText());
                    double w = Double.parseDouble(weightField.getText());
                    int a = Integer.parseInt(ageField.getText());
                    double newBMI = w / Math.pow(h / 100.0, 2);

                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/health_tracker", "root", "111111");
                    PreparedStatement stmt = con.prepareStatement("UPDATE trainees SET height = ?, weight = ?, age = ? WHERE user_id = ?");
                    stmt.setDouble(1, h);
                    stmt.setDouble(2, w);
                    stmt.setInt(3, a);
                    stmt.setInt(4, userId);
                    stmt.executeUpdate();

                    heightLabel.setText("Height : " + h + " cm");
                    weightLabel.setText("Weight : " + w + " kg");
                    ageLabel.setText("Age : " + a + " years");
                    bmiLabel.setText("BMI : " + String.format("%.1f", newBMI));

                    dialog.dispose();
                    JOptionPane.showMessageDialog(null, "Metrics updated successfully!", "Update", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        dialog.setVisible(true);
    }

    private JPanel buildActivitiesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel header = new JLabel("My Activities", SwingConstants.LEFT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));
        panel.add(header, BorderLayout.NORTH);

        activityListModel = new DefaultListModel<>();

        String[] columns = {"Type", "Duration", "Calories", "Distance", "Date"};
        activityTableModel = new DefaultTableModel(columns, 0);
        JTable activityTable = new JTable(activityTableModel);
        activityTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        activityTable.setRowHeight(25);
        panel.add(new JScrollPane(activityTable), BorderLayout.CENTER);

        JPanel rightContainer = new JPanel();
        rightContainer.setLayout(new BorderLayout());
        rightContainer.setBackground(Color.WHITE);
        rightContainer.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);

        JButton addActivityBtn = new JButton("Add Activity");
        addActivityBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addActivityBtn.setBackground(new Color(45, 145, 80));
        addActivityBtn.setForeground(Color.WHITE);
        addActivityBtn.setFocusPainted(false);

        JButton cancelActivityBtn = new JButton("Cancel Activity");
        cancelActivityBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelActivityBtn.setBackground(new Color(255, 69, 0));
        cancelActivityBtn.setForeground(Color.WHITE);
        cancelActivityBtn.setFocusPainted(false);
        cancelActivityBtn.setEnabled(false);

        buttonPanel.add(addActivityBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(cancelActivityBtn);

        JPanel activityTypePanel = new JPanel();
        activityTypePanel.setLayout(new GridLayout(5, 1, 10, 10));
        activityTypePanel.setVisible(false);
        activityTypePanel.setBackground(Color.LIGHT_GRAY);
        activityTypePanel.setBorder(BorderFactory.createTitledBorder("Choose Activity"));

        String[] types = {"Running", "Cycling", "Swimming", "Yoga", "Weight Training"};
        for (String type : types) {
            JButton typeBtn = new JButton(type);
            typeBtn.setBackground(new Color(70, 130, 180));
            typeBtn.setForeground(Color.WHITE);
            typeBtn.setFocusPainted(false);
            typeBtn.addActionListener(new ActionListener() {
                @Override

                public void actionPerformed(ActionEvent e) {
                    openActivityForm(type);
                    activityListModel.addElement(type);
                    cancelActivityBtn.setEnabled(true);
                    activityTypePanel.setVisible(false);
                }
            });
            activityTypePanel.add(typeBtn);
        }

        rightContainer.add(buttonPanel, BorderLayout.NORTH);
        rightContainer.add(activityTypePanel, BorderLayout.CENTER);

        panel.add(rightContainer, BorderLayout.EAST);

        addActivityBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                activityTypePanel.setVisible(!activityTypePanel.isVisible());
            }
        });

        cancelActivityBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedRow = activityTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String type = activityTableModel.getValueAt(selectedRow, 0).toString().toUpperCase();
                        String dateStr = activityTableModel.getValueAt(selectedRow, 4).toString();
                        Timestamp timestamp = Timestamp.valueOf(dateStr);

                        Activity AC = new Activity();
                        AC.setUserId(userId);
                        AC.setActivityType(type);
                        AC.setDateRecorded(timestamp);
                        int deletedId = AC.deleteActivity(AC.getUserId(), AC.getActivityType(), AC.getDateRecorded());

                        if (deletedId != 0) {

                            switch (AC.getActivityType()) {
                                case "RUNNING": {
                                    Running R = new Running();
                                    R.deleteRunningActivity(deletedId);
                                    JOptionPane.showMessageDialog(panel, "Activity deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                    loadActivities();
                                    break;

                                }
                                case "CYCLING": {
                                    Cycling c = new Cycling();
                                    c.deleteCyclingActivity(deletedId);
                                    JOptionPane.showMessageDialog(panel, "Activity deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                    loadActivities();
                                    break;

                                }
                                case "YOGA": {
                                    Yoga y = new Yoga();
                                    y.deleteYogaActivity(deletedId);
                                    JOptionPane.showMessageDialog(panel, "Activity deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                                    loadActivities();
                                    break;
                                }
                                case "SWIMMING": {
                                    Swimming s = new Swimming();
                                    s.deleteSwimmingActivity(deletedId);
                                    JOptionPane.showMessageDialog(panel, "Activity deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                                    loadActivities();
                                    break;

                                }
                                case "WEIGHT_TRAINING": {
                                    WeightTraining w = new WeightTraining();
                                    w.deleteWeightTrainingActivity(deletedId);
                                    JOptionPane.showMessageDialog(panel, "Activity deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                                    loadActivities();
                                    break;

                                }

                            }
                        } else {
                            JOptionPane.showMessageDialog(panel, "Failed to delete activity.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(panel, "Please select an activity to cancel.", "No Activity Selected", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.getStackTrace();
                }
            }
        });
        return panel;
    }

    private void loadActivities() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/health_tracker", "root", "111111");
            PreparedStatement stmt = con.prepareStatement("SELECT user_id, activity_type, duration, calories_burned, distance_covered, date_recorded FROM activities WHERE user_id = ?");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            activityListModel.clear();
            activityTableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                    rs.getString("activity_type"),
                    rs.getString("duration") + " mins",
                    rs.getString("calories_burned") + " kcal",
                    rs.getString("distance_covered") + " km",
                    rs.getString("date_recorded")
                };
                activityTableModel.addRow(row);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openActivityForm(String type) {
        switch (type) {
            case "Running": {

                JDialog dialog = new JDialog((Frame) null, "Log in Running", true);
                dialog.setSize(400, 280);
                dialog.setResizable(false);
                dialog.setLocationRelativeTo(null);
                dialog.getContentPane().setBackground(new Color(50, 63, 85));
                dialog.setLayout(new BorderLayout(10, 10));

                JLabel titleLabel = new JLabel("Log in Running", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
                titleLabel.setForeground(Color.WHITE);
                titleLabel.setOpaque(true);
                titleLabel.setBackground(new Color(50, 63, 85));
                titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
                dialog.add(titleLabel, BorderLayout.NORTH);

                JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
                inputPanel.setBackground(new Color(50, 63, 85));
                inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
                Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

                JTextField durationField = new JTextField();
                JTextField paceField = new JTextField();
                JTextField distanceField = new JTextField();
                JSpinner intensitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

                JLabel durationLabel = new JLabel("Duration (minutes):");
                durationLabel.setFont(labelFont);
                durationLabel.setForeground(Color.WHITE);
                durationField.setFont(fieldFont);

                JLabel distanceLabel = new JLabel("Distance (km):");
                distanceLabel.setFont(labelFont);
                distanceLabel.setForeground(Color.WHITE);
                distanceField.setFont(fieldFont);

                JLabel paceLabel = new JLabel("pace:");
                paceLabel.setFont(labelFont);
                paceLabel.setForeground(Color.WHITE);
                paceLabel.setFont(fieldFont);

                JLabel intensityLabel = new JLabel("Intensity (1–10):");
                intensityLabel.setFont(labelFont);
                intensityLabel.setForeground(Color.WHITE);
                intensitySpinner.setFont(fieldFont);

                inputPanel.add(durationLabel);
                inputPanel.add(durationField);
                inputPanel.add(distanceLabel);
                inputPanel.add(distanceField);
                inputPanel.add(paceLabel);
                inputPanel.add(paceField);
                inputPanel.add(intensityLabel);
                inputPanel.add(intensitySpinner);

                dialog.add(inputPanel, BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel();
                buttonPanel.setBackground(new Color(50, 63, 85));

                JButton submitBtn = new JButton("Submit");
                submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
                submitBtn.setFocusPainted(false);
                submitBtn.setBackground(new Color(45, 145, 80));
                submitBtn.setForeground(Color.WHITE);

                JButton cancelBtn = new JButton("Cancel");
                cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
                cancelBtn.setFocusPainted(false);
                cancelBtn.setBackground(new Color(200, 50, 50));
                cancelBtn.setForeground(Color.WHITE);

                buttonPanel.add(submitBtn);
                buttonPanel.add(cancelBtn);

                dialog.add(buttonPanel, BorderLayout.SOUTH);

                submitBtn.addActionListener(new ActionListener() {
                    @Override

                    public void actionPerformed(ActionEvent e) {
                        Activity A = new Activity();
                        String duration = durationField.getText().trim();
                        String distance = distanceField.getText().trim();
                        String pace = paceField.getText().trim();
                        int intensity = (int) intensitySpinner.getValue();

                        try {
                            int durationValue = Integer.parseInt(duration);
                            double distanceValue = Double.parseDouble(distance);
                            double paceValue = Double.parseDouble(pace);
                            Running R = new Running(userId, durationValue, distanceValue, intensity, paceValue);
                            int ID = R.logActivityWithType("RUNNING");
                            R.setActivityId(ID);
                            R.logRunningActivity();
                            loadActivities();
                            dialog.dispose();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for both duration and distance.",
                                    "Input Error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });

                cancelBtn.addActionListener(new ActionListener() {
                    @Override

                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });

                dialog.setVisible(true);
                break;
            }
            case "Cycling": {
                JDialog dialog = new JDialog((Frame) null, "Log in Cycling", true);
                dialog.setSize(400, 320);
                dialog.setResizable(false);
                dialog.setLocationRelativeTo(null);
                dialog.getContentPane().setBackground(new Color(50, 63, 85));
                dialog.setLayout(new BorderLayout(10, 10));

                JLabel titleLabel = new JLabel("Log in Cycling", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
                titleLabel.setForeground(Color.WHITE);
                titleLabel.setOpaque(true);
                titleLabel.setBackground(new Color(50, 63, 85));
                titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
                dialog.add(titleLabel, BorderLayout.NORTH);

                JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
                inputPanel.setBackground(new Color(50, 63, 85));
                inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                JTextField durationField = new JTextField();
                JTextField distanceField = new JTextField();
                JSpinner intensitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
                JComboBox<String> bikeTypeComboBox = new JComboBox<>(new String[]{"Road", "Mountain", "Stationary"});

                Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
                Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

                JLabel durationLabel = new JLabel("Duration (minutes):");
                durationLabel.setFont(labelFont);
                durationLabel.setForeground(Color.WHITE);
                durationField.setFont(fieldFont);

                JLabel distanceLabel = new JLabel("Distance (km):");
                distanceLabel.setFont(labelFont);
                distanceLabel.setForeground(Color.WHITE);
                distanceField.setFont(fieldFont);

                JLabel intensityLabel = new JLabel("Intensity (1–10):");
                intensityLabel.setFont(labelFont);
                intensityLabel.setForeground(Color.WHITE);
                intensitySpinner.setFont(fieldFont);

                JLabel bikeTypeLabel = new JLabel("Bike Type:");
                bikeTypeLabel.setFont(labelFont);
                bikeTypeLabel.setForeground(Color.WHITE);
                bikeTypeComboBox.setFont(fieldFont);

                inputPanel.add(durationLabel);
                inputPanel.add(durationField);
                inputPanel.add(distanceLabel);
                inputPanel.add(distanceField);
                inputPanel.add(intensityLabel);
                inputPanel.add(intensitySpinner);
                inputPanel.add(bikeTypeLabel);
                inputPanel.add(bikeTypeComboBox);

                dialog.add(inputPanel, BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel();
                buttonPanel.setBackground(new Color(50, 63, 85));

                JButton submitBtn = new JButton("Submit");
                submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
                submitBtn.setFocusPainted(false);
                submitBtn.setBackground(new Color(45, 145, 80));
                submitBtn.setForeground(Color.WHITE);

                JButton cancelBtn = new JButton("Cancel");
                cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
                cancelBtn.setFocusPainted(false);
                cancelBtn.setBackground(new Color(200, 50, 50));
                cancelBtn.setForeground(Color.WHITE);

                buttonPanel.add(submitBtn);
                buttonPanel.add(cancelBtn);

                dialog.add(buttonPanel, BorderLayout.SOUTH);

                submitBtn.addActionListener(new ActionListener() {
                    @Override

                    public void actionPerformed(ActionEvent e) {
                        String duration = durationField.getText().trim();
                        String distance = distanceField.getText().trim();
                        int intensity = (int) intensitySpinner.getValue();
                        String bikeType = (String) bikeTypeComboBox.getSelectedItem();

                        try {
                            int durationValue = Integer.parseInt(duration);
                            double distanceValue = Double.parseDouble(distance);
                            Cycling C = new Cycling(userId, durationValue, distanceValue, bikeType, intensity);
                            int ID = C.logActivityWithType("CYCLING");
                            C.setActivityId(ID);
                            C.logCyclingActivity();
                            loadActivities();
                            dialog.dispose();

                            if (!duration.isEmpty() && !distance.isEmpty() && bikeType != null) {
                                dialog.dispose();
                            } else {
                                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for both duration and distance.",
                                    "Input Error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });

                cancelBtn.addActionListener(new ActionListener() {
                    @Override

                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });

                dialog.setVisible(true);
                break;
            }
            case "Yoga": {
                JDialog dialog = new JDialog((Frame) null, "Log in Yoga", true);
                dialog.setSize(400, 400);
                dialog.setResizable(false);
                dialog.setLocationRelativeTo(null);
                dialog.getContentPane().setBackground(new Color(50, 63, 85));
                dialog.setLayout(new BorderLayout(10, 10));

                JLabel titleLabel = new JLabel("Log in Yoga", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
                titleLabel.setForeground(Color.WHITE);
                titleLabel.setOpaque(true);
                titleLabel.setBackground(new Color(50, 63, 85));
                titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
                dialog.add(titleLabel, BorderLayout.NORTH);

                JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
                inputPanel.setBackground(new Color(50, 63, 85));
                inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                JTextField durationField = new JTextField();
                JComboBox<String> styleComboBox = new JComboBox<>(new String[]{"Hatha", "Vinyasa", "Ashtanga", "Power"});
                JComboBox<String> levelComboBox = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Advanced"});
                JCheckBox meditationCheckBox = new JCheckBox("Includes Meditation");

                Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
                Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

                JLabel durationLabel = new JLabel("Duration (minutes):");
                durationLabel.setFont(labelFont);
                durationLabel.setForeground(Color.WHITE);
                durationField.setFont(fieldFont);

                JLabel styleLabel = new JLabel("Yoga Style:");
                styleLabel.setFont(labelFont);
                styleLabel.setForeground(Color.WHITE);
                styleComboBox.setFont(fieldFont);

                JLabel levelLabel = new JLabel("Level:");
                levelLabel.setFont(labelFont);
                levelLabel.setForeground(Color.WHITE);
                levelComboBox.setFont(fieldFont);

                inputPanel.add(durationLabel);
                inputPanel.add(durationField);
                inputPanel.add(styleLabel);
                inputPanel.add(styleComboBox);
                inputPanel.add(levelLabel);
                inputPanel.add(levelComboBox);
                inputPanel.add(new JLabel());
                inputPanel.add(meditationCheckBox);

                dialog.add(inputPanel, BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel();
                buttonPanel.setBackground(new Color(50, 63, 85));

                JButton submitBtn = new JButton("Submit");
                submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
                submitBtn.setFocusPainted(false);
                submitBtn.setBackground(new Color(45, 145, 80));
                submitBtn.setForeground(Color.WHITE);

                JButton cancelBtn = new JButton("Cancel");
                cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
                cancelBtn.setFocusPainted(false);
                cancelBtn.setBackground(new Color(200, 50, 50));
                cancelBtn.setForeground(Color.WHITE);

                buttonPanel.add(submitBtn);
                buttonPanel.add(cancelBtn);

                dialog.add(buttonPanel, BorderLayout.SOUTH);

                submitBtn.addActionListener(new ActionListener() {
                    @Override

                    public void actionPerformed(ActionEvent e) {
                        String duration = durationField.getText().trim();
                        int durationValue = Integer.parseInt(duration);

                        String style = (String) styleComboBox.getSelectedItem();
                        String level = (String) levelComboBox.getSelectedItem();
                        boolean includesMeditation = meditationCheckBox.isSelected();

                        try {
                            int durationInt = Integer.parseInt(duration);
                            if (durationInt <= 0) {
                                JOptionPane.showMessageDialog(dialog, "Please enter a positive number for Duration.", "Input Error", JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(dialog, "Please enter a valid number for Duration.", "Input Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        if (!duration.isEmpty() && style != null && level != null) {
                            Yoga Y = new Yoga(userId, durationValue, style, level, includesMeditation);
                            int ID = Y.logActivityWithType("YOGA");
                            Y.setActivityId(ID);
                            Y.logYogaActivity();
                            loadActivities();
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });

                cancelBtn.addActionListener(new ActionListener() {
                    @Override

                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });

                dialog.setVisible(true);
                break;
            }
            case "Swimming": {
                JDialog dialog = new JDialog((Frame) null, "Log in Swimming", true);
                dialog.setSize(400, 360);
                dialog.setResizable(false);
                dialog.setLocationRelativeTo(null);
                dialog.getContentPane().setBackground(new Color(50, 63, 85));
                dialog.setLayout(new BorderLayout(10, 10));

                JLabel titleLabel = new JLabel("Log in Swimming", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
                titleLabel.setForeground(Color.WHITE);
                titleLabel.setOpaque(true);
                titleLabel.setBackground(new Color(50, 63, 85));
                titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
                dialog.add(titleLabel, BorderLayout.NORTH);

                JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
                inputPanel.setBackground(new Color(50, 63, 85));
                inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                JTextField durationField = new JTextField();
                JTextField distanceField = new JTextField();
                JComboBox<String> styleComboBox = new JComboBox<>(new String[]{"Freestyle", "Breaststroke", "Butterfly", "Backstroke"});
                JTextField poolLengthField = new JTextField();
                JSpinner intensitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

                Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
                Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

                JLabel durationLabel = new JLabel("Duration (minutes):");
                durationLabel.setFont(labelFont);
                durationLabel.setForeground(Color.WHITE);
                durationField.setFont(fieldFont);

                JLabel distanceLabel = new JLabel("Distance (m):");
                distanceLabel.setFont(labelFont);
                distanceLabel.setForeground(Color.WHITE);
                distanceField.setFont(fieldFont);

                JLabel styleLabel = new JLabel("Swimming Style:");
                styleLabel.setFont(labelFont);
                styleLabel.setForeground(Color.WHITE);
                styleComboBox.setFont(fieldFont);

                JLabel poolLengthLabel = new JLabel("Pool Length (m):");
                poolLengthLabel.setFont(labelFont);
                poolLengthLabel.setForeground(Color.WHITE);
                poolLengthField.setFont(fieldFont);

                JLabel intensityLabel = new JLabel("Intensity (1–10):");
                intensityLabel.setFont(labelFont);
                intensityLabel.setForeground(Color.WHITE);
                intensitySpinner.setFont(fieldFont);

                inputPanel.add(durationLabel);
                inputPanel.add(durationField);
                inputPanel.add(distanceLabel);
                inputPanel.add(distanceField);
                inputPanel.add(styleLabel);
                inputPanel.add(styleComboBox);
                inputPanel.add(poolLengthLabel);
                inputPanel.add(poolLengthField);
                inputPanel.add(intensityLabel);
                inputPanel.add(intensitySpinner);

                dialog.add(inputPanel, BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel();
                buttonPanel.setBackground(new Color(50, 63, 85));

                JButton submitBtn = new JButton("Submit");
                submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
                submitBtn.setFocusPainted(false);
                submitBtn.setBackground(new Color(45, 145, 80));
                submitBtn.setForeground(Color.WHITE);

                JButton cancelBtn = new JButton("Cancel");
                cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
                cancelBtn.setFocusPainted(false);
                cancelBtn.setBackground(new Color(200, 50, 50));
                cancelBtn.setForeground(Color.WHITE);

                buttonPanel.add(submitBtn);
                buttonPanel.add(cancelBtn);

                dialog.add(buttonPanel, BorderLayout.SOUTH);

                submitBtn.addActionListener(new ActionListener() {
                    @Override

                    public void actionPerformed(ActionEvent e) {
                        String duration = durationField.getText().trim();
                        String distance = distanceField.getText().trim();
                        String style = (String) styleComboBox.getSelectedItem();
                        String poolLength = poolLengthField.getText().trim();
                        int intensity = (int) intensitySpinner.getValue();

                        if (!duration.isEmpty() && !distance.isEmpty() && !style.isEmpty() && !poolLength.isEmpty()) {
                            try {

                                int durationValue = Integer.parseInt(duration);
                                double distanceValue = Double.parseDouble(distance);
                                int poolLengthValue = Integer.parseInt(poolLength);

                                Swimming S = new Swimming(userId, durationValue, distanceValue, style, poolLengthValue, intensity);
                                int ID = S.logActivityWithType("Swimming");
                                S.setActivityId(ID);
                                S.logSwimmingActivity();
                                loadActivities();
                                dialog.dispose();
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for Duration, Distance, and Pool Length.", "Input Error", JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });

                cancelBtn.addActionListener(new ActionListener() {
                    @Override

                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });

                dialog.setVisible(true);
                break;
            }
            case "Weight Training": {
                JDialog dialog = new JDialog((Frame) null, "Log in Weight Training", true);
                dialog.setSize(400, 350);
                dialog.setResizable(false);
                dialog.setLocationRelativeTo(null);
                dialog.getContentPane().setBackground(new Color(50, 63, 85));
                dialog.setLayout(new BorderLayout(10, 10));

                JLabel titleLabel = new JLabel("Log in Weight Training", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
                titleLabel.setForeground(Color.WHITE);
                titleLabel.setOpaque(true);
                titleLabel.setBackground(new Color(50, 63, 85));
                titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
                dialog.add(titleLabel, BorderLayout.NORTH);

                JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
                inputPanel.setBackground(new Color(50, 63, 85));
                inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                JTextField durationField = new JTextField();
                JComboBox<String> focusAreaComboBox = new JComboBox<>(new String[]{"Upper Body", "Lower Body", "Core", "Full Body"});
                JTextField totalWeightField = new JTextField();
                JSpinner intensitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

                Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
                Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

                JLabel durationLabel = new JLabel("Duration (minutes):");
                durationLabel.setFont(labelFont);
                durationLabel.setForeground(Color.WHITE);
                durationField.setFont(fieldFont);

                JLabel focusAreaLabel = new JLabel("Focus Area:");
                focusAreaLabel.setFont(labelFont);
                focusAreaLabel.setForeground(Color.WHITE);
                focusAreaComboBox.setFont(fieldFont);

                JLabel totalWeightLabel = new JLabel("Total Weight (kg):");
                totalWeightLabel.setFont(labelFont);
                totalWeightLabel.setForeground(Color.WHITE);
                totalWeightField.setFont(fieldFont);

                JLabel intensityLabel = new JLabel("Intensity (1–10):");
                intensityLabel.setFont(labelFont);
                intensityLabel.setForeground(Color.WHITE);
                intensitySpinner.setFont(fieldFont);

                inputPanel.add(durationLabel);
                inputPanel.add(durationField);
                inputPanel.add(focusAreaLabel);
                inputPanel.add(focusAreaComboBox);
                inputPanel.add(totalWeightLabel);
                inputPanel.add(totalWeightField);
                inputPanel.add(intensityLabel);
                inputPanel.add(intensitySpinner);

                dialog.add(inputPanel, BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel();
                buttonPanel.setBackground(new Color(50, 63, 85));

                JButton submitBtn = new JButton("Submit");
                submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
                submitBtn.setFocusPainted(false);
                submitBtn.setBackground(new Color(45, 145, 80));
                submitBtn.setForeground(Color.WHITE);

                JButton cancelBtn = new JButton("Cancel");
                cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
                cancelBtn.setFocusPainted(false);
                cancelBtn.setBackground(new Color(200, 50, 50));
                cancelBtn.setForeground(Color.WHITE);

                buttonPanel.add(submitBtn);
                buttonPanel.add(cancelBtn);

                dialog.add(buttonPanel, BorderLayout.SOUTH);

                submitBtn.addActionListener(new ActionListener() {
                    @Override

                    public void actionPerformed(ActionEvent e) {
                        String duration = durationField.getText().trim();
                        String focusArea = (String) focusAreaComboBox.getSelectedItem();
                        String totalWeight = totalWeightField.getText().trim();
                        int intensity = (int) intensitySpinner.getValue();
                        int durationValue = Integer.parseInt(duration);
                        int totalWeighthValue = Integer.parseInt(totalWeight);

                        try {
                            int durationInt = Integer.parseInt(duration);
                            if (durationInt <= 0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(dialog, "Please enter a valid number for duration.", "Input Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        try {
                            double totalWeightDouble = Double.parseDouble(totalWeight);
                            if (totalWeightDouble <= 0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(dialog, "Please enter a valid number for total weight.", "Input Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        if (!duration.isEmpty() && !focusArea.isEmpty() && !totalWeight.isEmpty()) {
                            WeightTraining W = new WeightTraining(userId, durationValue, focusArea, intensity, totalWeighthValue);
                            int ID = W.logActivityWithType("WEIGHT_TRAINING");
                            W.setActivityId(ID);
                            W.logWeightTrainingActivity();
                            loadActivities();//show it 
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });

                cancelBtn.addActionListener(new ActionListener() {
                    @Override

                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });

                dialog.setVisible(true);
                break;

            }

        }
    }

    private JPanel buildNotificationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel header = new JLabel("Notifications", SwingConstants.LEFT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));
        panel.add(header, BorderLayout.NORTH);

        JPanel notificationContainer = new JPanel();
        notificationContainer.setLayout(new BoxLayout(notificationContainer, BoxLayout.Y_AXIS));
        notificationContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(notificationContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.add(scrollPane, BorderLayout.CENTER);

        Notification dao = new Notification();
        List<Notification> notifications = dao.getUserNotifications(userId);

        for (Notification notif : notifications) {
            JPanel notifPanel = new JPanel(new BorderLayout());
            notifPanel.setBackground(new Color(245, 245, 245));
            notifPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            notifPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

            JLabel msgLabel = new JLabel("<html><b>" + notif.getMessage() + "</b><br><small>Date: "
                    + notif.getDateSent() + " | Status: " + notif.getStatus() + "</small></html>");
            msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            notifPanel.add(msgLabel, BorderLayout.CENTER);

            if (!"Read".equalsIgnoreCase(notif.getStatus())) {
                JButton markReadBtn = new JButton("Mark as Read");
                markReadBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                markReadBtn.setFocusPainted(false);
                markReadBtn.setMargin(new Insets(3, 8, 3, 8));
                markReadBtn.setBackground(new Color(220, 240, 255));
                markReadBtn.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 220)));
                markReadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                markReadBtn.addActionListener(new ActionListener() {
                    @Override

                    public void actionPerformed(ActionEvent e) {
                        markNotificationAsRead(notif.getNotificationId());
                        msgLabel.setText("<html><b>" + notif.getMessage() + "</b><br><small>Date: "
                                + notif.getDateSent() + " | Status: Read</small></html>");
                        markReadBtn.setEnabled(false);
                    }
                });

                notifPanel.add(markReadBtn, BorderLayout.EAST);
            }

            notificationContainer.add(notifPanel);
            notificationContainer.add(Box.createVerticalStrut(8));
        }

        return panel;
    }

    public void markNotificationAsRead(int notificationId) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/health_tracker", "root", "111111");
            String query = "UPDATE notifications SET status = 'Read' WHERE notification_id = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, notificationId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating notification: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private JPanel buildSubscriptionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel header = new JLabel("Subscription", SwingConstants.LEFT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));
        panel.add(header, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Plan Type", "Start Date", "End Date", "Status", "Payment"};
        subscriptionTableModel = new DefaultTableModel(columnNames, 0);
        subscriptionTable = new JTable(subscriptionTableModel);
        JScrollPane tableScroll = new JScrollPane(subscriptionTable);
        panel.add(tableScroll, BorderLayout.CENTER);

        refreshSubscriptionsTable();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Subscription");
        JButton cancelButton = new JButton("Cancel Subscription");
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                openAddSubscriptionDialog();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                cancelSubscription(subscriptionTable);
            }
        });

        return panel;
    }

    private void refreshSubscriptionsTable() {
        subscriptionTableModel.setRowCount(0); 
        Subscription dao = new Subscription();
        List<Subscription> subs = dao.getUserSubscriptionHistory(userId);
        for (Subscription s : subs) {
            subscriptionTableModel.addRow(new Object[]{
                s.getSubscriptionId(),
                s.getPlanType(),
                s.getStartDate().toString(),
                s.getEndDate().toString(),
                s.getStatus(),
                s.getPayment() != null ? s.getPayment().getPaymentMethod() : "N/A"
            });
        }
    }

    private void openAddSubscriptionDialog() {
        JDialog dialog = new JDialog((Frame) null, "Add Subscription", true);
        dialog.setSize(400, 300);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.getContentPane().setBackground(new Color(50, 63, 85));
        dialog.setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Add Subscription", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(50, 63, 85));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        JPanel dialogPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        dialogPanel.setBackground(new Color(50, 63, 85));

        JLabel planLabel = new JLabel("Select Plan Type:");
        planLabel.setForeground(Color.WHITE);
        planTypeCombo = new JComboBox<>(new String[]{
            "Premium ($199.99/year)", "Monthly ($9.99/month)", "Annual ($99.99/year)"
        });

        JLabel paymentLabel = new JLabel("Select Payment Method:");
        paymentLabel.setForeground(Color.WHITE);
        paymentMethodCombo = new JComboBox<>(new String[]{
            "Master Card", "Apple Pay", "Visa", "PayPal"
        });

        dialogPanel.add(planLabel);
        dialogPanel.add(planTypeCombo);
        dialogPanel.add(paymentLabel);
        dialogPanel.add(paymentMethodCombo);
        dialog.add(dialogPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(50, 63, 85));
        JButton subscribeButton = new JButton("Subscribe Now");
        subscribeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subscribeButton.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                try {
                    String planType = (String) planTypeCombo.getSelectedItem();
                    String paymentMethod = (String) paymentMethodCombo.getSelectedItem();

                    Subscription subscription = new Subscription();
                    subscription.setUserId(userId);
                    subscription.setPlanType(planType);
                    subscription.setStartDate(LocalDate.now());

                    if (planType.toLowerCase().contains("month")) {
                        subscription.setEndDate(LocalDate.now().plusMonths(1));
                    } else {
                        subscription.setEndDate(LocalDate.now().plusYears(1));
                    }

                    subscription.setStatus(Subscription.Status.ACTIVE.getValue());

                    Payment payment = new Payment();
                    payment.setAmount(subscription.calculatePlanPrice(subscription.getPlanType()));
                    payment.setPaymentMethod(paymentMethod);
                    payment.setStatus(Payment.PaymentStatus.COMPLETED);
                    subscription.setPayment(payment);

                    if (subscription.saveSubscription(payment)) {

                        JOptionPane.showMessageDialog(dialog, "Subscription successfully added!");
                        refreshSubscriptionsTable();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to save subscription.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        buttonPanel.add(subscribeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void cancelSubscription(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int subscriptionId = (int) table.getValueAt(selectedRow, 0);
            String status = (String) table.getValueAt(selectedRow, 4);

            if (!"Active".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(null, "Only active subscriptions can be canceled.");
                return;
            }

            Subscription subscription = new Subscription();
            if (subscription.cancelSubscription(subscriptionId)) {
                JOptionPane.showMessageDialog(null, "Subscription canceled successfully.");
                refreshSubscriptionsTable();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to cancel subscription.");
            }

        } else {
            JOptionPane.showMessageDialog(null, "Please select a subscription to cancel.");
        }
    }

}
