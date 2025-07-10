package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

public class Notification {

    private int notificationId;
    private int userId;
    private String message;
    private Date dateSent;
    private String status;

    public Notification() {
    }

    public Notification(int userId, String message) {
        this.userId = userId;
        this.message = message;
        this.dateSent = new Date();
        this.status = "Sent";
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRead() {
        return "Read".equalsIgnoreCase(status);
    }

    public boolean isDeleted() {
        return "Deleted".equalsIgnoreCase(status);
    }

    public void markAsRead() {
        this.status = "Read";
    }

    public void markAsDeleted() {
        this.status = "Deleted";
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

    public List<Notification> getUserNotifications(int user_id) {
        List<Notification> notifications = new ArrayList<>();
        try {
            initializeDatabase();
            String query = "SELECT * FROM notifications WHERE user_id = ? AND status != 'Deleted' "
                    + "ORDER BY date_sent DESC";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, user_id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Notification notification = new Notification();
                notification.setNotificationId(rs.getInt("notification_id"));
                notification.setUserId(rs.getInt("user_id"));
                notification.setMessage(rs.getString("message"));
                notification.setDateSent(rs.getDate("date_sent"));
                notification.setStatus(rs.getString("status"));
                notifications.add(notification);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading notifications: " + e.getMessage());
        }
        return notifications;
    }

    @Override
    public String toString() {
        return "Notification{"
                + "notificationId=" + notificationId
                + ", userId=" + userId
                + ", message='" + message + '\''
                + ", dateSent=" + dateSent
                + ", status='" + status + '\''
                + '}';
    }
}
