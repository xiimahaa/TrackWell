package model;

import java.sql.*;
import javax.swing.JOptionPane;

public class Cycling extends Activity {

    private String bikeType;
    private int intensity;

    public Cycling() {
        super();
        setActivityType("");
    }

    public Cycling(int userId, int duration, Double distanceCovered,
            String bikeType, int intensity) {
        super(userId, "CYCLING", duration, 0, 0, distanceCovered);
        this.bikeType = bikeType;
        this.intensity = intensity;
        calculateAndSetCalories();
    }

    private void calculateAndSetCalories() {
        double baseCalories = 6.0;

        double intensityMultiplier = 1.0;
        if (intensity == 10 || intensity > 7) {
            intensityMultiplier = 1.3;
        } else if (intensity > 4) {
            intensityMultiplier = 1.0;
        } else {
            intensityMultiplier = 0.8;
        }

        double bikeTypeMultiplier = 1.0;
        if (bikeType != null) {
            switch (bikeType.toLowerCase()) {
                case "mountain":
                    bikeTypeMultiplier = 1.2;
                    break;
                case "stationary":
                    bikeTypeMultiplier = 0.9;
                    break;
                case "road":
                    bikeTypeMultiplier = 1.0;
                    break;
            }
        }

        int totalCalories = (int) (getDuration() * baseCalories
                * intensityMultiplier * bikeTypeMultiplier);
        setCaloriesBurned(totalCalories);
    }

    public String getBikeType() {
        return bikeType;
    }

    public void setBikeType(String bikeType) {
        this.bikeType = bikeType;
        if (this.intensity != 0) {
            calculateAndSetCalories();
        }
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
        if (this.bikeType != null) {
            calculateAndSetCalories();
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

    public void logCyclingActivity() {
        try {
            initializeDatabase();
            String query = "INSERT INTO cycling_activities (activity_id, bike_type,  "
                    + "intensity, average_speed) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, this.getActivityId());
            pstmt.setString(2, this.getBikeType().toLowerCase());
            pstmt.setInt(3, this.getIntensity());
            pstmt.setDouble(4, this.calculateSpeed());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error logging cycling activity: " + e.getMessage());
        }
    }

    public void deleteCyclingActivity(int activityId) throws SQLException {
                    initializeDatabase();

        String query = "DELETE FROM cycling_activities WHERE activity_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, activityId);
         pstmt.executeUpdate() ;
    }

    @Override
    public String toString() {
        return "Cycling{"
                + "activityId=" + getActivityId()
                + ", userId=" + getUserId()
                + ", duration=" + getDuration()
                + ", distance=" + getDistanceCovered()
                + ", bikeType='" + bikeType + '\''
                + ", intensity='" + intensity + '\''
                + ", caloriesBurned=" + getCaloriesBurned()
                + '}';
    }
}
