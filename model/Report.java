package model;

import java.time.LocalDateTime;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Report {

    private int reportId;
    private int userId;
    private String reportType;
    private LocalDateTime generatedDate;
    private String filePath;
    private List<Activity> activities;

    public Report() {
    }

    public Report(int userId, String reportType, String filePath) {
        this.userId = userId;
        this.reportType = reportType;
        this.filePath = filePath;
        this.generatedDate = LocalDateTime.now();
        this.activities = new ArrayList<>();
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDateTime generatedDate) {
        this.generatedDate = generatedDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isFileExists() {
        if (filePath == null) {
            return false;
        }
        File file = new File(filePath);
        return file.exists();
    }

    public String getFileExtension() {
        if (filePath == null) {
            return "";
        }
        int lastDot = filePath.lastIndexOf('.');
        return lastDot > 0 ? filePath.substring(lastDot + 1) : "";
    }

    public List<Activity> getActivitiesByUser(int userId) {
        List<Activity> userActivities = new ArrayList<>();
        for (Activity activity : activities) {
            if (activity.getUserId() == userId) {
                userActivities.add(activity);
            }
        }
        return userActivities;
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

    public Report generateActivityReport(int userId) {
        try {
            List<Activity> activities = getActivitiesByUser(userId);
            if (activities.isEmpty()) {
                return null;
            }

            String reportContent = generateActivityReportContent(activities);
            Report report = new Report(userId, "Activity Report", reportContent);

            String query = "INSERT INTO reports (user_id, report_type, generated_date, file_path) "
                    + "VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setString(2, "Activity Report");
            pstmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, reportContent);

            if (pstmt.executeUpdate() > 0) {
                return report;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error generating report: " + e.getMessage());
        }
        return null;
    }

    private String generateActivityReportContent(List<Activity> activities) {
        StringBuilder content = new StringBuilder();

        for (Activity activity : activities) {
            content.append("Activity Type: ").append(activity.getActivityType())
                    .append("\nDuration: ").append(activity.getDuration())
                    .append(" minutes\nCalories Burned: ").append(activity.getCaloriesBurned())
                    .append("\nDate: ").append(activity.getDateRecorded())
                    .append("\n-------------------\n");
        }

        return content.toString();
    }

    @Override
    public String toString() {
        return "Report{"
                + "reportId=" + reportId
                + ", userId=" + userId
                + ", reportType='" + reportType + '\''
                + ", generatedDate=" + generatedDate
                + ", filePath='" + filePath + '\''
                + '}';
    }
}
