package model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Subscription {

    private int subscriptionId;
    private int userId;
    private String planType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Payment payment;

    public enum Status {
        ACTIVE("Active"),
        EXPIRED("Expired"),
        CANCELLED("Cancelled");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PaymentMethod {
        MASTER_CARD("Master Card"),
        PAYPAL("PayPal"),
        APPLE_PAY("Apple pay"),
        VISA("Visa");

        private final String value;

        PaymentMethod(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public Subscription() {
    }

    public Subscription(int userId, String planType, LocalDate startDate, LocalDate endDate, String status) {
        this.userId = userId;
        this.planType = planType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public boolean isActive() {
        return Status.ACTIVE.getValue().equals(status)
                && LocalDate.now().isBefore(endDate.plusDays(1));
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate);
    }

    public int getRemainingDays() {
        if (isExpired()) {
            return 0;
        }
        return (int) LocalDate.now().until(endDate).getDays();
    }

    public boolean isValidDateRange() {
        return startDate != null && endDate != null && startDate.isBefore(endDate);
    }

    public boolean hasValidPayment() {
        return payment != null && payment.isSuccessful();
    }

    private Connection connection;

    public void initializeDatabase() {
        try {

            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/health_tracker",
                    "root", "111111"
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database connection failed: " + e.getMessage());
        }
    }

    public boolean saveSubscription(Payment payment) {

        try {
            initializeDatabase();

            String subscriptionQuery = "INSERT INTO subscriptions (user_id, plan_type, start_date, end_date, status) "
                    + "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(subscriptionQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, userId);
            stmt.setString(2, planType);
            stmt.setDate(3, java.sql.Date.valueOf(startDate));
            stmt.setDate(4, java.sql.Date.valueOf(endDate));
            stmt.setString(5, "Active");

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Creating subscription failed");
            }

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                subscriptionId = generatedKeys.getInt(1);
                this.setSubscriptionId(subscriptionId);
                payment.setSubscriptionId(subscriptionId);
            } else {
                throw new SQLException("Creating subscription failed, no ID obtained.");
            }
            String paymentQuery = "INSERT INTO payments (subscription_id, amount, payment_method, status) "
                    + "VALUES ( ?, ?, ?, ?)";
            PreparedStatement paymentStmt = connection.prepareStatement(paymentQuery);
            paymentStmt.setInt(1, payment.getSubscriptionId());
            paymentStmt.setDouble(2, payment.getAmount());
            paymentStmt.setString(3, payment.getPaymentMethod());
            paymentStmt.setString(4, payment.getStatus().toString());

            if (paymentStmt.executeUpdate() == 0) {
                throw new SQLException("Creating payment failed");
            }

            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error creating subscription: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelSubscription(int subscriptionId) {

        try {
            initializeDatabase();

            String updateSubQuery = "UPDATE subscriptions SET status = ? WHERE subscription_id = ?";
            PreparedStatement updateSubStmt = connection.prepareStatement(updateSubQuery);
            updateSubStmt.setString(1, Subscription.Status.CANCELLED.getValue());
            updateSubStmt.setInt(2, subscriptionId);

            if (updateSubStmt.executeUpdate() == 0) {
                throw new SQLException("Subscription not found or already cancelled");
            }

            String updatePaymentQuery = "UPDATE payments SET status = ? WHERE subscription_id = ?";
            PreparedStatement updatePaymentStmt = connection.prepareStatement(updatePaymentQuery);
            updatePaymentStmt.setString(1, Payment.PaymentStatus.REFUNDED.toString());
            updatePaymentStmt.setInt(2, subscriptionId);
            updatePaymentStmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error cancelling subscription: " + e.getMessage());
            return false;
        }
    }

    public List<Subscription> getUserSubscriptionHistory(int userId) {
        List<Subscription> subscriptions = new ArrayList<>();
        try {
            initializeDatabase();

            String query = "SELECT s.subscription_id, s.user_id, s.plan_type, s.start_date, s.end_date, s.status AS subscription_status, "
                    + "p.payment_id, p.amount, p.payment_method, p.status AS payment_status "
                    + "FROM subscriptions s "
                    + "LEFT JOIN payments p ON s.subscription_id = p.subscription_id "
                    + "WHERE s.user_id = ? "
                    + "ORDER BY s.start_date DESC";

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                try {
                    Subscription subscription = new Subscription();
                    subscription.setSubscriptionId(rs.getInt("subscription_id"));
                    subscription.setUserId(rs.getInt("user_id"));
                    subscription.setPlanType(rs.getString("plan_type"));

                    java.sql.Date startDate = rs.getDate("start_date");
                    if (startDate != null) {
                        subscription.setStartDate(startDate.toLocalDate());
                    }

                    java.sql.Date endDate = rs.getDate("end_date");
                    if (endDate != null) {
                        subscription.setEndDate(endDate.toLocalDate());
                    }

                    subscription.setStatus(rs.getString("subscription_status"));

                    int paymentId = rs.getInt("payment_id");
                    if (!rs.wasNull()) {
                        Payment payment = new Payment();
                        payment.setPaymentId(paymentId);
                        payment.setSubscriptionId(rs.getInt("subscription_id"));
                        payment.setAmount(rs.getDouble("amount"));

                        payment.setPaymentMethod(rs.getString("payment_method"));

                        String paymentStatusStr = rs.getString("payment_status");
                        if (paymentStatusStr != null) {
                            try {
                                payment.setStatus(Payment.PaymentStatus.valueOf(paymentStatusStr.toUpperCase()));
                            } catch (IllegalArgumentException e) {
                                System.err.println("Unknown payment status: " + paymentStatusStr);
                            }
                        }

                        subscription.setPayment(payment);
                    }

                    subscriptions.add(subscription);
                } catch (IllegalArgumentException e) {
                    System.err.println("Error processing subscription record: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error fetching subscription history: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return subscriptions;
    }

    public double calculatePlanPrice(String planType) {
        switch (planType.toUpperCase()) {
            case "BASIC":
                return 29.99;
            case "PREMIUM":
                return 49.99;
            case "PRO":
                return 99.99;
            default:
                return 29.99;
        }
    }

    @Override
    public String toString() {
        return "Subscription{"
                + "subscriptionId=" + subscriptionId
                + ", userId=" + userId
                + ", planType='" + planType + '\''
                + ", startDate=" + startDate
                + ", endDate=" + endDate
                + ", status='" + status + '\''
                + ", payment=" + payment
                + '}';
    }
}
