package model;

import java.sql.*;
import javax.swing.JOptionPane;

public class Activity {

    private int activityId;
    private int userId;
    private String activityType;
    private int duration;
    private int caloriesBurned;
    private int stepsCount;
    private double distanceCovered;
    private Timestamp  dateRecorded;

    private Connection connection;

    public Activity() {
    }

    public Activity(int userId, String activityType, int duration, int caloriesBurned,
            int stepsCount, double distanceCovered) {
        this.userId = userId;
        this.activityType = activityType;
        this.duration = duration;
        this.caloriesBurned = caloriesBurned;
        this.stepsCount = stepsCount;
        this.distanceCovered = distanceCovered;
        this.dateRecorded = new Timestamp(System.currentTimeMillis());
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public int getStepsCount() {
        return stepsCount;
    }

    public void setStepsCount(int stepsCount) {
        this.stepsCount = stepsCount;
    }

    public double getDistanceCovered() {
        return distanceCovered;
    }

    public void setDistanceCovered(double distanceCovered) {
        this.distanceCovered = distanceCovered;
    }

    public Timestamp  getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(Timestamp  dateRecorded) {
        this.dateRecorded = dateRecorded;
    }

    public double calculatePace() {
        return (distanceCovered > 0) ? duration / distanceCovered : 0.0;
    }

    public double calculateSpeed() {
        return (distanceCovered > 0) ? (distanceCovered / duration) * 60 : 0.0;
    }

    public double calculateCaloriesPerMinute() {
        return duration > 0 ? (double) caloriesBurned / duration : 0;
    }

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

    public int logActivityWithType(String activityType) {
        try {
            initializeDatabase();
            String query = "INSERT INTO activities (user_id, activity_type, duration, calories_burned, distance_covered, date_recorded) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, this.getUserId());
            pstmt.setString(2, activityType);
            pstmt.setInt(3, this.getDuration());
            pstmt.setInt(4, this.getCaloriesBurned());
            pstmt.setDouble(5, this.getDistanceCovered());
            pstmt.setTimestamp(6, new java.sql.Timestamp(this.getDateRecorded().getTime()));
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                this.activityId = rs.getInt(1);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error logging activity: " + e.getMessage());
        }
        return activityId;
    }

    public  int deleteActivity(int userId, String activityType, Timestamp  dateRecorded) {
    try {
            initializeDatabase();

        String selectSQL = "SELECT activity_id FROM activities WHERE user_id = ? AND activity_type = ? AND date_recorded = ?";
        PreparedStatement selectStmt = connection.prepareStatement(selectSQL);
        selectStmt.setInt(1, userId);
        selectStmt.setString(2,activityType);
        selectStmt.setTimestamp(3,dateRecorded );
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
           this.setActivityId(rs.getInt("activity_id"));

            String deleteSQL = "DELETE FROM activities WHERE activity_id = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteSQL);
            deleteStmt.setInt(1, activityId);
            deleteStmt.executeUpdate();
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return this.getActivityId();
}
    
    @Override
    public String toString() {
        return "Activity{"
                + "activityId=" + activityId
                + ", userId=" + userId
                + ", activityType='" + activityType + '\''
                + ", duration=" + duration
                + ", caloriesBurned=" + caloriesBurned
                + ", stepsCount=" + stepsCount
                + ", distanceCovered=" + distanceCovered
                + ", dateRecorded=" + dateRecorded
                + '}';
    }
}
