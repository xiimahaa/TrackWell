-- Create database
CREATE DATABASE IF NOT EXISTS OOPD;
USE OOPD;

-- Users table (base class)
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_type ENUM('USER', 'ADMIN', 'TRAINEE') DEFAULT 'USER',
    managed_by INT,
    FOREIGN KEY (managed_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Trainee table (extends User)
CREATE TABLE IF NOT EXISTS trainees (
    user_id INT PRIMARY KEY,
    height DOUBLE NOT NULL,  -- in cm
    weight DOUBLE NOT NULL,  -- in kg
    age INT NOT NULL,
    goals TEXT,
    health_condition TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Activities table (base class)
CREATE TABLE IF NOT EXISTS activities (
    activity_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    activity_type ENUM('RUNNING', 'CYCLING', 'SWIMMING', 'WEIGHT_TRAINING', 'YOGA') NOT NULL,
    duration INT NOT NULL,
    calories_burned INT NOT NULL,
    steps_count INT,
    distance_covered DOUBLE,
    date_recorded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Running activities (extends Activity)
CREATE TABLE IF NOT EXISTS running_activities (
    activity_id INT PRIMARY KEY,
    pace DOUBLE NOT NULL,  -- minutes per kilometer
    terrain ENUM('track', 'road', 'trail') NOT NULL,
    elevation INT NOT NULL,  -- meters
    intensity ENUM('low', 'medium', 'high') NOT NULL,
    FOREIGN KEY (activity_id) REFERENCES activities(activity_id) ON DELETE CASCADE
);

-- Cycling activities (extends Activity)
CREATE TABLE IF NOT EXISTS cycling_activities (
    activity_id INT PRIMARY KEY,
    bike_type ENUM('road', 'mountain', 'stationary') NOT NULL,
    resistance INT NOT NULL,  -- 1-20 for stationary, gear number for others
    elevation INT NOT NULL,  -- meters
    intensity int ('low', 'medium', 'high') NOT NULL,
    average_speed DOUBLE NOT NULL,  -- km/h
    FOREIGN KEY (activity_id) REFERENCES activities(activity_id) ON DELETE CASCADE
);

-- Swimming activities (extends Activity)
CREATE TABLE IF NOT EXISTS swimming_activities (
    activity_id INT PRIMARY KEY,
    style ENUM('freestyle', 'breaststroke', 'butterfly', 'backstroke') NOT NULL,
    laps INT NOT NULL,
    pool_length INT NOT NULL,  -- meters
    intensity ENUM('low', 'medium', 'high') NOT NULL,
    FOREIGN KEY (activity_id) REFERENCES activities(activity_id) ON DELETE CASCADE
);

-- Weight training activities (extends Activity)
CREATE TABLE IF NOT EXISTS weight_training_activities (
    activity_id INT PRIMARY KEY,
    focus_area ENUM('upper body', 'lower body', 'core', 'full body') NOT NULL,
    intensity ENUM('low', 'medium', 'high') NOT NULL,
    total_weight INT NOT NULL,  -- kg
    FOREIGN KEY (activity_id) REFERENCES activities(activity_id) ON DELETE CASCADE
);



-- Yoga activities (extends Activity)
CREATE TABLE IF NOT EXISTS yoga_activities (
    activity_id INT PRIMARY KEY,
    style ENUM('hatha', 'vinyasa', 'ashtanga', 'power') NOT NULL,
    level ENUM('beginner', 'intermediate', 'advanced') NOT NULL,
    with_meditation BOOLEAN DEFAULT false,
    breathwork_minutes INT DEFAULT 0,
    FOREIGN KEY (activity_id) REFERENCES activities(activity_id) ON DELETE CASCADE
);

-- Payments table
CREATE TABLE IF NOT EXISTS subscriptions (
    subscription_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    plan_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('ACTIVE', 'EXPIRED', 'CANCELLED') DEFAULT 'ACTIVE',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    subscription_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method ENUM('MASTER_CARD', 'PAYPAL', 'APPLE_PAY', 'VISA') NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(subscription_id) ON DELETE CASCADE
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    date_sent TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('SENT', 'READ', 'DELETED') DEFAULT 'SENT',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Reports table
CREATE TABLE IF NOT EXISTS reports (
    report_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    generated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    file_path VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

ALTER TABLE Payments
DROP COLUMN Payment_date;

ALTER TABLE running_activities
DROP COLUMN terrain,
DROP COLUMN elevation;

ALTER TABLE cycling_activities
DROP COLUMN resistance,
DROP COLUMN elevation;

ALTER TABLE swimming_activities
DROP COLUMN laps;

Alter table yoga_activities
drop column breathwork_minutes;

SELECT CONSTRAINT_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'users' AND COLUMN_NAME = 'managed_by';
ALTER TABLE users
DROP FOREIGN KEY users_ibfk_1;
ALTER TABLE users
DROP COLUMN managed_by;

ALTER TABLE trainees
DROP COLUMN goals;

alter table activities
DROP COLUMN steps_count;


