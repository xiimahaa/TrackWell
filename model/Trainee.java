package model;

public class Trainee extends User {

    private double height;
    private double weight;
    private int age;

    public Trainee() {
        super();
        this.height = 0.0;
        this.weight = 0.0;
        this.age = 0;
    }

    public Trainee(String name, String email, String password,
            double height, double weight, int age) {
        super(name, email, password, "TRAINEE");
        this.height = height;
        this.weight = weight;
        this.age = age;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double calculateBMI() {
        double heightInMeters = height / 100;
        return weight / (heightInMeters * heightInMeters);
    }

    @Override
    public String toString() {
        return "Trainee{"
                + "id=" + getUserId()
                + ", name='" + getName() + '\''
                + ", email='" + getEmail() + '\''
                + ", height=" + height
                + ", weight=" + weight
                + ", age=" + age
                + '}';
    }
}
