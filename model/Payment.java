package model;

import java.sql.*;
import javax.swing.JOptionPane;

public class Payment {

    private int paymentId;
    private int subscriptionId;
    private double amount;
    private String paymentMethod;
    private PaymentStatus status;

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }

    public Payment() {
    }

    public Payment(int subscriptionId, double amount, String paymentMethod) {
        this.subscriptionId = subscriptionId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }

    public void processPayment_Completed() {
        if (processPayment()) {
            this.status = PaymentStatus.COMPLETED;
        }
    }

    public void refundPayment() {
        if (this.status == PaymentStatus.COMPLETED) {
            this.status = PaymentStatus.REFUNDED;
        }
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

    public boolean processPayment() {
        initializeDatabase();
        try {
            String paymentQuery = "INSERT INTO payments (payment_id, subscription_id, amount, payment_method, status) "
                    + "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(paymentQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, this.getPaymentId());
                pstmt.setInt(2, this.getSubscriptionId());
                pstmt.setDouble(3, this.getAmount());
                pstmt.setString(4, this.getPaymentMethod());
                pstmt.setString(5, this.getStatus().toString());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            this.setPaymentId(generatedKeys.getInt(1));
                        }
                    }

                    if (this.getStatus() == Payment.PaymentStatus.COMPLETED) {
                        String subscriptionQuery = "UPDATE subscriptions SET status = 'ACTIVE' "
                                + "WHERE subscription_id = ? AND status = 'PENDING'";
                        try (PreparedStatement subscriptionStmt = connection.prepareStatement(subscriptionQuery)) {
                            subscriptionStmt.setInt(1, this.getSubscriptionId());
                            subscriptionStmt.executeUpdate();
                        }
                    }

                    return true;
                }
            }

            return false;
        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null,
                    "Error processing payment: " + e.getMessage(),
                    "Payment Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

    }

    @Override
    public String toString() {
        return "Payment{"
                + "paymentId=" + paymentId
                + ", subscriptionId=" + subscriptionId
                + ", amount=" + amount
                + ", paymentMethod='" + paymentMethod + '\''
                + ", status=" + status
                + '}';
    }
}
