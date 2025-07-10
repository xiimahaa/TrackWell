package model;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Admin extends User {

    private List<Payment> payments;
    private List<Subscription> subscriptions;
    private List<Notification> notifications;
    private List<Report> reports;

    public Admin() {

    }

    public Admin(String name, String email, String password) {
        super(name, email, password, "Admin");
        this.payments = new ArrayList<>();
        this.subscriptions = new ArrayList<>();
        this.notifications = new ArrayList<>();
        this.reports = new ArrayList<>();
    }

    public void processPayment(Payment payment) {
        payment.processPayment();
        payments.add(payment);
    }

    public void refundPayment(Payment payment) {
        payment.refundPayment();
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments);
    }

    public List<Subscription> getActiveSubscriptions() {
        List<Subscription> activeSubscriptions = new ArrayList<>();
        for (Subscription subscription : subscriptions) {
            if (subscription.isActive()) {
                activeSubscriptions.add(subscription);
            }
        }
        return activeSubscriptions;
    }

    public void sendNotification(int userId, String message) {
        Notification notification = new Notification(userId, message);
        notifications.add(notification);
    }

   

    public List<Report> getAllReports() {
        return new ArrayList<>(reports);
    }


    private Connection connection;

    private void initializeDatabase() {
        try {

            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/health_tracker",
                    "root", "1234"
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database connection failed: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            initializeDatabase();
            String query = "SELECT user_id, name, email FROM users WHERE user_type = 'TRAINEE'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("Name"));
                user.setEmail(rs.getString("Email"));

                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<Subscription> getAllSubscriptions() {
        List<Subscription> subscriptions = new ArrayList<>();
        try {
            initializeDatabase();
            String query = "SELECT s.subscription_id, s.user_id, s.plan_type, s.start_date, s.end_date, s.status AS subscription_status, "
                    + "p.payment_id, p.amount, p.payment_method, p.status AS payment_status "
                    + "FROM subscriptions s "
                    + "LEFT JOIN payments p ON s.subscription_id = p.subscription_id";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {

                Subscription sub = new Subscription();
                sub.setSubscriptionId(rs.getInt("subscription_id"));
                sub.setUserId(rs.getInt("user_id"));
                sub.setPlanType(rs.getString("plan_type"));
                java.sql.Date startDate = rs.getDate("start_date");

                if (startDate != null) {
                    sub.setStartDate(startDate.toLocalDate());
                }
                java.sql.Date endDate = rs.getDate("end_date");
                if (endDate != null) {
                    sub.setEndDate(endDate.toLocalDate());
                }
                sub.setStatus(rs.getString("subscription_status"));

                int paymentId = rs.getInt("payment_id");
                if (!rs.wasNull()) {
                    Payment payment = new Payment();
                    payment.setPaymentId(paymentId);
                    payment.setSubscriptionId(rs.getInt("subscription_id"));
                    payment.setAmount(rs.getDouble("amount"));
                    payment.setPaymentMethod(rs.getString("payment_method"));
                    String payStatus = rs.getString("payment_status");
                    if (payStatus != null) {
                        payment.setStatus(Payment.PaymentStatus.valueOf(payStatus.toUpperCase()));
                    }
                    sub.setPayment(payment);
                }
                subscriptions.add(sub);

            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading subscriptions: " + e.getMessage());
        }
        return subscriptions;
    }

    public void exportSubscriptionsToFile(String filePath) {

        initializeDatabase();
        List<Subscription> subscriptions = getAllSubscriptions();  
        StringBuilder reportContent = new StringBuilder();
        reportContent.append("Subscription Report - Generated on ").append(new java.util.Date()).append("\n\n");

        for (Subscription subscription : subscriptions) {
            reportContent.append("Subscription ID: ").append(subscription.getSubscriptionId()).append("\n");
            reportContent.append("User ID: ").append(subscription.getUserId()).append("\n");
            reportContent.append("Plan Type: ").append(subscription.getPlanType()).append("\n");
            reportContent.append("Status: ").append(subscription.getStatus()).append("\n");
            reportContent.append("Start Date: ").append(subscription.getStartDate()).append("\n");
            reportContent.append("End Date: ").append(subscription.getEndDate()).append("\n");
            reportContent.append("Payment Method: ").append(subscription.getPayment().getPaymentMethod()).append("\n");
            reportContent.append("Amount: ").append(subscription.getPayment().getAmount()).append("\n");
            reportContent.append("-----------------------------\n");

        }

        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.print(reportContent.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing to file: " + e.getMessage());
            return;
        }

        try {
            String insertQuery = "INSERT INTO reports (report_type, generated_date, file_path) VALUES ( ?, NOW(), ?)";
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setString(1, "Subscription Report");
            pstmt.setString(2, filePath);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error inserting report into DB: " + e.getMessage());
        }
    }

    public void exportUserActivityReport(String filePath) {
        List<User> users = getAllUsers();
        initializeDatabase();

        StringBuilder content = new StringBuilder();
        content.append(String.format("%-10s %-20s %-30s%n", "User ID", "Name", "Email"));

        for (User user : users) {
            content.append(String.format("%-10d %-20s %-30s%n",
                    user.getUserId(),
                    user.getName(),
                    user.getEmail()));
        }

        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.write(content.toString());

            String insertQuery = "INSERT INTO reports (report_type, generated_date, file_path) VALUES ( ?, NOW(), ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                pstmt.setString(1, "User  Report");
                pstmt.setString(2, filePath);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error exporting User  Report: " + e.getMessage());
        }
    }

    public boolean updateSubscriptionStatus(int subscriptionId, String newStatus) {
        try {
            initializeDatabase();

            String query = "UPDATE subscriptions SET status = ? WHERE subscription_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, newStatus);
            stmt.setInt(2, subscriptionId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        try {
            initializeDatabase();

            String query = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting user: " + e.getMessage());
            return false;
        }
    }

  

}
