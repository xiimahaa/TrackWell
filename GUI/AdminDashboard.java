
package GUI;


import model.Admin;
import model.Subscription;
import model.User;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;


public class AdminDashboard extends JFrame {

    private JTable usersTable;
    private JTable subTable;
    private final Admin admin = new Admin();

    public AdminDashboard() {
        super("Health Tracker - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        JPanel usersPanel = new JPanel(new BorderLayout());
        usersTable = new JTable(new DefaultTableModel(new String[]{"User ID", "Name", "Email"}, 0));
        loadUsers();
        usersPanel.add(new JScrollPane(usersTable), BorderLayout.CENTER);

        JPanel usersBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton deleteUserBtn = new JButton("Delete User");
        usersBtnPanel.add(deleteUserBtn);
        usersPanel.add(usersBtnPanel, BorderLayout.SOUTH);

        deleteUserBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                int selectedRow = usersTable.getSelectedRow();
                if (selectedRow != -1) {
                    int userId = Integer.parseInt(usersTable.getValueAt(selectedRow, 0).toString());
                    if (admin.deleteUser(userId)) {
                        loadUsers();
                        JOptionPane.showMessageDialog(AdminDashboard.this,
                                 "User deleted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(AdminDashboard.this,
                                 "Failed to delete user.");
                    }
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this,
                             "Please select a user to delete.");
                }
            }
        });

        JPanel subPanel = new JPanel(new BorderLayout());
        subTable = new JTable(new DefaultTableModel(
                new String[]{"Subscription ID", "User ID", "Plan Type", "Start Date", "End Date", "Status"}, 0));
        loadSubscriptions();
        subPanel.add(new JScrollPane(subTable), BorderLayout.CENTER);

        JPanel subBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton updateStatusBtn = new JButton("Update Status");
        subBtnPanel.add(updateStatusBtn);
        subPanel.add(subBtnPanel, BorderLayout.SOUTH);

        updateStatusBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                int selectedRow = subTable.getSelectedRow();
                if (selectedRow != -1) {
                    String[] options = {"ACTIVE", "EXPIRED", "CANCELLED"};
                    String newStatus = (String) JOptionPane.showInputDialog(
                            AdminDashboard.this,
                             "Select new status:", "Update Status",
                            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (newStatus != null) {
                        int subscriptionId = Integer.parseInt(subTable.getValueAt(selectedRow, 0).toString());
                        if (admin.updateSubscriptionStatus(subscriptionId, newStatus)) {
                            loadSubscriptions();
                            JOptionPane.showMessageDialog(AdminDashboard.this,
                                     "Status updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(AdminDashboard.this,
                                     "Failed to update status.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this,
                             "Please select a subscription to update.");
                }
            }
        });

        JPanel reportsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 15, 0);

        JButton activityReportBtn = new JButton("Generate User Report");
        JButton revenueReportBtn = new JButton("Generate Revenue Report");
        activityReportBtn.setPreferredSize(new Dimension(300, 50));
        revenueReportBtn.setPreferredSize(new Dimension(300, 50));

        gbc.gridy = 0;
        reportsPanel.add(activityReportBtn, gbc);
        gbc.gridy = 1;
        reportsPanel.add(revenueReportBtn, gbc);

        activityReportBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save User Activity Report");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
                fileChooser.setSelectedFile(new File("User_Activity_Report.txt"));
                if (fileChooser.showSaveDialog(AdminDashboard.this
                ) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    admin.exportUserActivityReport(file.getAbsolutePath());
                    JOptionPane.showMessageDialog(AdminDashboard.this,
                             "Report generated and saved!");

                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this,
                             "Failed to generate report.");
                }
            }
        });
        revenueReportBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Subscription Report");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
                fileChooser.setSelectedFile(new File("Subscription_Report" + ".txt"));
                if (fileChooser.showSaveDialog(AdminDashboard.this
                ) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    admin.exportSubscriptionsToFile(file.getAbsolutePath());
                    JOptionPane.showMessageDialog(AdminDashboard.this,
                             "Report generated and saved!");
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this,
                             "Failed to generate report.");
                }
            }
        });

        tabs.add("Users Management", usersPanel);
        tabs.add("Subscriptions", subPanel);
        tabs.add("Reports", reportsPanel);
        add(tabs);

        setVisible(true);
    }

    private void loadUsers() {
        DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
        model.setRowCount(0);
        for (User user : admin.getAllUsers()) {
            model.addRow(new Object[]{user.getUserId(), user.getName(), user.getEmail()});
        }
    }

    private void loadSubscriptions() {
        DefaultTableModel model = (DefaultTableModel) subTable.getModel();
        model.setRowCount(0);
        for (Subscription sub : admin.getAllSubscriptions()) {
            model.addRow(new Object[]{
                sub.getSubscriptionId(),
                sub.getUserId(),
                sub.getPlanType(),
                sub.getStartDate(),
                sub.getEndDate(),
                sub.getStatus()
            });
        }
    }

}

