package model;

import java.sql.*;
import javax.swing.JOptionPane;

public class WeightTraining extends Activity {

    private String focusArea;
    private int intensity;
    private int totalWeight;

    public WeightTraining() {
        super();
        setActivityType("WEIGHT_TRAINING");
    }

    public WeightTraining(int userId, int duration, String focusArea, int intensity, int totalWeight) {
        super(userId, "WEIGHT_TRAINING", duration, 0, 0, 0.0);
        this.focusArea = focusArea;
        this.intensity = intensity;
        this.totalWeight = totalWeight;
        calculateAndSetCalories();
    }

    private void calculateAndSetCalories() {
        double baseCalories = 5.0;

        double intensityMultiplier = 1.0;
        if (intensity == 10 || intensity > 7) {
            intensityMultiplier = 1.3;
        } else if (intensity > 4) {
            intensityMultiplier = 1.0;
        } else {
            intensityMultiplier = 0.8;
        }

        double focusAreaMultiplier = 1.0;
        if (focusArea != null) {
            if (focusArea.equalsIgnoreCase("full body")) {
                focusAreaMultiplier = 1.2;
            } else if (focusArea.equalsIgnoreCase("lower body")) {
                focusAreaMultiplier = 1.1;
            } else if (focusArea.equalsIgnoreCase("upper body")) {
                focusAreaMultiplier = 1.0;
            } else if (focusArea.equalsIgnoreCase("core")) {
                focusAreaMultiplier = 0.9;
            }
        }

        int totalCalories = (int) (getDuration() * baseCalories
                * intensityMultiplier * focusAreaMultiplier);
        setCaloriesBurned(totalCalories);
    }

    public String getFocusArea() {
        return focusArea;
    }

    public void setFocusArea(String focusArea) {
        this.focusArea = focusArea;
        if (this.intensity != 0) {
            calculateAndSetCalories();
        }
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
        if (this.focusArea != null) {
            calculateAndSetCalories();
        }
    }

    public int getTotalWeight() {
        return totalWeight;
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

    public void logWeightTrainingActivity() {
        try {
            initializeDatabase();
            String query = "INSERT INTO weight_training_activities (activity_id, focus_area, intensity, total_weight) "
                    + "VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, this.getActivityId());
            pstmt.setString(2, this.getFocusArea().toLowerCase());
            pstmt.setInt(3, this.getIntensity());
            pstmt.setInt(4, this.getTotalWeight());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error logging weight training activity: " + e.getMessage());
        }
    }

    public void deleteWeightTrainingActivity(int activityId) throws SQLException {
                    initializeDatabase();

        String query = "DELETE FROM weight_training_activities WHERE activity_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, activityId);
         pstmt.executeUpdate();
    }

    @Override
    public String toString() {
        return "WeightTraining{"
                + "activityId=" + getActivityId()
                + ", userId=" + getUserId()
                + ", duration=" + getDuration()
                + ", focusArea='" + focusArea + '\''
                + ", intensity='" + intensity + '\''
                + ", totalWeight=" + totalWeight
                + ", caloriesBurned=" + getCaloriesBurned()
                + '}';
    }
}
