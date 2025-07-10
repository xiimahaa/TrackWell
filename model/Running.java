package model;

import java.sql.*;
import javax.swing.JOptionPane;

public class Running extends Activity {

    private int intensity;
    private double pace;
    private Connection connection;

    public Running() {
        super();
        setActivityType("RUNNING");
    }

    public Running(int userId, int duration, Double distanceCovered, int intensity, double pace) {
        super(userId, "RUNNING", duration, 0, 0, distanceCovered);
        this.intensity = intensity;
        this.pace = pace;
        calculateAndSetCalories();
    }

    public int calculateAndSetCalories() {
        double baseCalories = 8.0;
        double intensityMultiplier = 1.0;
        if (intensity > 7) {
            intensityMultiplier = 1.3;
        } else if (intensity > 4) {
            intensityMultiplier = 1.0;
        } else {
            intensityMultiplier = 0.8;
        }
        double calories = getDuration() * baseCalories * intensityMultiplier;
        setCaloriesBurned((int) calories);
        return (int) calories;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public double getPace() {
        return pace;
    }

    public void setPace(double pace) {
        this.pace = pace;
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/health_tracker",
                    "root", "1234"
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
        }
    }

    public void logRunningActivity() {
        try {
            initializeDatabase();
            String query = "INSERT INTO running_activities (activity_id, pace, intensity) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, getActivityId());
            pstmt.setDouble(2, getPace());
            pstmt.setInt(3, getIntensity());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error logging running activity: " + e.getMessage());
        }
    }

    public void deleteRunningActivity(int activityId) throws SQLException {
        initializeDatabase();
        String query = "DELETE FROM running_activities WHERE activity_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, activityId);
         pstmt.executeUpdate();
    }

    @Override
    public String toString() {
        return "Running{"
                + "activityId=" + getActivityId()
                + ", userId=" + getUserId()
                + ", duration=" + getDuration()
                + ", distance=" + getDistanceCovered()
                + ", intensity=" + intensity
                + ", caloriesBurned=" + getCaloriesBurned()
                + '}';
    }
}
