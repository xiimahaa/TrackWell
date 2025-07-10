package model;

import java.sql.*;
import javax.swing.JOptionPane;

public class Swimming extends Activity {

    private String style;
    private int poolLength;
    private int intensity;

    public Swimming() {
        super();
        setActivityType("SWIMMING");
    }

    public Swimming(int userId, int duration, Double distanceCovered, String style,
            int poolLength, int intensity) {
        super(userId, "SWIMMING", duration, 0, 0, calculateDistance(poolLength));
        this.style = style;

        this.poolLength = poolLength;
        this.intensity = intensity;
        calculateAndSetCalories();
    }

    private static Double calculateDistance(int poolLength) {
        return (double) (poolLength) / 1000;
    }

    private void calculateAndSetCalories() {
        double baseCalories = 7.0;
        if (style != null) {
            if (style.equalsIgnoreCase("butterfly")) {
                baseCalories = 11.0;
            } else if (style.equalsIgnoreCase("freestyle")) {
                baseCalories = 8.0;
            } else if (style.equalsIgnoreCase("breaststroke") || style.equalsIgnoreCase("backstroke")) {
                baseCalories = 7.0;
            }
        }

        double intensityMultiplier = 1.0;
        if (intensity == 10 || intensity > 7) {
            intensityMultiplier = 1.3;
        } else if (intensity > 4) {
            intensityMultiplier = 1.0;
        } else {
            intensityMultiplier = 0.8;
        }

        int totalCalories = (int) (getDuration() * baseCalories * intensityMultiplier);
        setCaloriesBurned(totalCalories);
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
        calculateAndSetCalories();
    }

    public int getPoolLength() {
        return poolLength;
    }

    public void setPoolLength(int poolLength) {
        this.poolLength = poolLength;
        setDistanceCovered(calculateDistance(poolLength));
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
        calculateAndSetCalories();
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

    public void logSwimmingActivity() {
        try {
            initializeDatabase();
            String query = "INSERT INTO swimming_activities (activity_id, style, pool_length, intensity) "
                    + "VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, this.getActivityId());
            pstmt.setString(2, this.getStyle().toLowerCase());
            pstmt.setInt(3, this.getPoolLength());
            pstmt.setInt(4, this.getIntensity());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error logging swimming activity: " + e.getMessage());
        }
    }

    public void deleteSwimmingActivity(int activityId) throws SQLException {
                    initializeDatabase();

        String query = "DELETE FROM swimming_activities WHERE activity_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, activityId);
         pstmt.executeUpdate() ;
    }

    @Override
    public String toString() {
        return "Swimming{"
                + "activityId=" + getActivityId()
                + ", userId=" + getUserId()
                + ", duration=" + getDuration()
                + ", style='" + style + '\''
                + ", poolLength=" + poolLength
                + ", intensity='" + intensity + '\''
                + ", distance=" + getDistanceCovered()
                + ", caloriesBurned=" + getCaloriesBurned()
                + '}';
    }
}
