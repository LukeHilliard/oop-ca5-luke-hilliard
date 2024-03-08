package Objects;

import DAOs.MySqlEmployeeDao;
import DAOs.EmployeeDaoInterface;
import DTOs.Employee;
import Exceptions.DaoException;

import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        /**
         * Main author: Haroldas Tamosauskas
         * Other contributors: ...
         *
         */

        // Testing find all employees
//        try {
//            System.out.println("\nFinding all the Employees");
//            List<Employee> employees = IEmployeeDao.getAllEmployees();
//
//            if (employees.isEmpty())
//                System.out.println("There are no Employees");
//            else {
//                for (Employee employee : employees)
//                    System.out.println("Employee: " + employee.toString());
//            }
//
//        } catch( DaoException e) {
//            e.printStackTrace();
//        }

        // Testing to find the ID
//        try {
//            System.out.println("\nFinding all the Employees with ID");
//            int id = 1;
//            Employee employee = IEmployeeDao.getEmployeeById(id);
//
//            if( employee != null )
//                System.out.println("Employee found: " + employee);
//            else
//                System.out.println("Employee with that ID was not found");
//            }
//        catch( DaoException e)
//        {
//            e.printStackTrace();
//        }

        EmployeeDaoInterface IEmployeeDao = new MySqlEmployeeDao();
        displayMainMenu();
    }

    /**
     *  Author: Luke Hilliard
     *  Displays the main menu (Default)
     */
   private static void displayMainMenu() {
        System.out.println("------* Employee Database ------*");
        System.out.println("\t.1 Display Entity by ID\n\t.2 Display all Entities\n\t.3 Add an Entity\n\t.4 Delete Entity by ID\n\t.5 Get Images List");
    }

    /**
     *  Author: Luke Hilliard
     *  Displays the menu for adding a new Employee
     */
   private static void displayAddEmployee() {
        Scanner kb = new Scanner(System.in);
        String fName, lName, gender, role, username, password;
        LocalDate dateOfBirth;
        double salary;
        System.out.println("------* Add Employee ------*");
        fName = validateStringInput("First Name: ");
        lName = validateStringInput("Last Name: ");
        gender = validateStringInput("Gender: ");
        dateOfBirth = getDateOfBirth();
        System.out.print("Annual Salary: ");
        role = validateStringInput("Role: ");
        System.out.print("Username: ");
        System.out.print("Password: ");

    }

    /**
     *  Author: Luke Hilliard
     *  Takes a message as a parameter to display again if invalid input,
     *  loops until input is a string only containing letters.
     *
     * @param requestMessage e.g. "First Name: "
     * @return validated String
     */
    private static String validateStringInput(String requestMessage) {
        Scanner input = new Scanner(System.in);
        String validStr = "";

        // set to always true so loop can only exit from break
        while(true) {
            System.out.print(requestMessage);
            validStr = input.nextLine();

            // Check if the input string contains ONLY letters
            if(validStr.matches("^[a-zA-Z]+$")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter a string containing only letters.\n" + requestMessage);
            }
        }
        // return validated string
        return validStr;
    }

    /**
     *  Author: Luke Hilliard
     *  Use this method whenever you want to get a date of birth.
     *  Tries to parse user input with date format 'yyyy-MM-dd' catches invalid format,
     *  loops infinitely until return is hit
     *
     * @return Employees date of birth in the correct format.
     */
    private static LocalDate getDateOfBirth() {
        Scanner input = new Scanner(System.in);

        // true so loop can only exit from return
        while(true) {
            try{
                String dateString = input.nextLine();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                // if input matches format
                return LocalDate.parse(dateString, dateFormatter);

            } catch(DateTimeParseException e) { // catch any invalid formats and try input again
                System.out.println("Invalid date format. Please enter the date in the format YYYY-MM-DD");
            }
        }
    }




}
