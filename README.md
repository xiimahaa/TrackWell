# TrackWell
The Health Tracker System is a Java-based desktop app for monitoring health activities like steps, calories, sleep, and water, with custom notifications. It uses object-oriented principles, a Java Swing GUI, JDBC for data storage, and a modular design for team development, showcasing OOP2 concepts in a user-friendly, maintainable solution.

## 📂 Project Structure
```plaintext
TrackWell-Health-Tracker/
│
├── src/
│   ├── GUI/                           # Graphical User Interface (GUI) classes
│   │   ├── AdminDashBoard.java        # Admin dashboard
│   │   ├── Dashboard.java             # User dashboard
│   │   ├── LoginFrame.java            # Login interface
│   │   ├── RegistrationFrame.java     # Registration interface
│   │   └── WelcomePage.java           # Welcome / Start page
│   │
│   ├── model/                         # Data models for the application
│   │   ├── Activity.java
│   │   ├── Admin.java
│   │   ├── Cycling.java
│   │   ├── Notification.java
│   │   ├── Payment.java
│   │   ├── Report.java
│   │   ├── Running.java
│   │   ├── Swimming.java
│   │   ├── Trainee.java
│   │   ├── User.java
│   │   ├── Yoga.java
│   │   └── WeightTraining.java
│   │
│   ├── Database/                      # Database connection and queries
│   │   └── DBConnection.java
│   │
│   └── Main/                          # Main class to run the application
│       └── TrackWell.java
│
├── resources/                         # Application resources (images, icons, etc.)
│   ├── logo.png
│   └── logo1.png
│
├── sql/                               # SQL scripts for database setup
│   └── trackwell_db.sql
│
├── README.md                          # Project documentation (this file)

## 📌 Key Features:
- User-friendly Java Swing GUI.
- Real-time synchronization with SQL database using JDBC.
- Automated health notifications.
- Modular OOP-based architecture.
- Login, Registration, Admin, and User Dashboard interfaces.

## 💾 Technologies:
- Java (Swing)
- JDBC (SQL Database)
- Object-Oriented Programming (OOP)


