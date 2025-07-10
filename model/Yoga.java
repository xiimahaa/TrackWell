package model;

import java.sql.*;
import javax.swing.JOptionPane;

public class Yoga extends Activity {

    private String style;
    private String level;
    private boolean withMeditation;

    public Yoga() {
        super();
        setActivityType("YOGA");

    }

    public Yoga(int userId, int duration, String style, String level,
            boolean withMeditation) {
        super(userId, "YOGA", duration, 0, 0, 0.0);
        this.style = style;
        this.level = level;
        this.withMeditation = withMeditation;
        calculateAndSetCalories();
    }

    private void calculateAndSetCalories() {
        double baseCalories = 3.0;

        double styleMultiplier = 1.0;
        if (style != null) {
            switch (style.toLowerCase()) {
                case "power":
                    styleMultiplier = 1.5;
                    break;
                case "ashtanga":
                    styleMultiplier = 1.3;
                    break;
                case "vinyasa":
                    styleMultiplier = 1.2;
                    break;
                case "hatha":
                    styleMultiplier = 1.0;
                    break;
                case "restorative":
                    styleMultiplier = 0.8;
                    break;
                default:
                    styleMultiplier = 1.0;
                    break;
            }
        }

        double levelMultiplier = 1.0;
        if (level != null) {
            switch (level.toLowerCase()) {
                case "advanced":
                    levelMultiplier = 1.3;
                    break;
                case "intermediate":
                    levelMultiplier = 1.1;
                    break;
                case "beginner":
                    levelMultiplier = 1.0;
                    break;
                default:
                    levelMultiplier = 1.0;
                    break;
            }
        }

        double mindfulnessBonus = 1.0;
        if (withMeditation) {
            mindfulnessBonus += 0.1;
        }

        int totalCalories = (int) (getDuration() * baseCalories
                * styleMultiplier * levelMultiplier
                * mindfulnessBonus);
        setCaloriesBurned(totalCalories);
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
        if (this.level != null) {
            calculateAndSetCalories();
        }
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
        if (this.style != null) {
            calculateAndSetCalories();
        }
    }

    public boolean isWithMeditation() {
        return withMeditation;
    }

    public void setWithMeditation(boolean withMeditation) {
        this.withMeditation = withMeditation;
        calculateAndSetCalories();
    }

    private Connection connection;

    private void initializeDatabase() {
        try {

            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/health_tracker",
                    "root", "1234   "
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database connection failed: " + e.getMessage());
        }
    }

    public void logYogaActivity() {
        try {
            initializeDatabase();
            String query = "INSERT INTO yoga_activities (activity_id, style, level, with_meditation) "
                    + "VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, this.getActivityId());
            pstmt.setString(2, this.getStyle().toLowerCase());
            pstmt.setString(3, this.getLevel().toLowerCase());
            pstmt.setBoolean(4, this.isWithMeditation());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error logging yoga activity: " + e.getMessage());
        }
    }

    public void deleteYogaActivity(int activityId) throws SQLException {
                    initializeDatabase();

        String query = "DELETE FROM yoga_activities WHERE activity_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, activityId);
         pstmt.executeUpdate() ;
    }

    @Override
    public String toString() {
        return "Yoga{"
                + "activityId=" + getActivityId()
                + ", userId=" + getUserId()
                + ", duration=" + getDuration()
                + ", style='" + style + '\''
                + ", level='" + level + '\''
                + ", withMeditation=" + withMeditation
                + ", caloriesBurned=" + getCaloriesBurned()
                + '}';
    }
}
