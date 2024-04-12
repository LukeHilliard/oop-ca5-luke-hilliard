package DTOs;

import java.time.LocalDate;

/**
 * Main author: Haroldas Tamosauskas
 * Other contributors: Luke Hilliard
 *
 */

public class Employee {
    private int id;
    private final String firstName;
    private final String lastName;
    private final String gender;
    private final LocalDate dob;
    private final double salary;
    private final String role;
    private final String username;
    private final String password;


    // With id
    public Employee(int id, String firstName, String lastName, String gender, LocalDate dob, double salary, String role, String username, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dob = dob;
        this.salary = salary;
        this.role = role;
        this.username = username;
        this.password = password;
    }


    // Without id
    public Employee(String firstName, String lastName, String gender, LocalDate dob, double salary, String role, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dob = dob;
        this.salary = salary;
        this.role = role;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }


    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public String getGender() {
        return gender;
    }


    public LocalDate getDob() {
        return dob;
    }


    public double getSalary() {
        return salary;
    }


    public String getRole() {
        return role;
    }


    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", dob=" + dob +
                ", salary=" + salary +
                ", role='" + role + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
