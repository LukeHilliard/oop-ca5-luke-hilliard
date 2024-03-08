package Objects;

import DAOs.MySqlEmployeeDao;
import DAOs.EmployeeDaoInterface;
import DTOs.Employee;
import Exceptions.DaoException;

import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean exit = false;
        /**
         * Main author: Luke Hilliard
         * Other contributors: ...
         *
         */
        try{
            do{
                int choice = 0;
                EmployeeDaoInterface IEmployeeDao = new MySqlEmployeeDao();
                displayMainMenu();

                choice = input.nextInt();
                switch (choice) {
                    case -1:
                        exit = true;
                        break;
                    case 1:
                        displayAllEmployees(IEmployeeDao);
                        break;
                    case 2:
                        findEmployeeByID(IEmployeeDao);
                        break;
                    case 3:
                        // TODO implement delete entity
                        break;
                    case 4:
                        displayAddEmployee(IEmployeeDao);
                        break;
                    case 5:
                        // TODO implement update existing by id
                        break;
                    case 6:
                        // TODO implement get list matching filters
                        break;
                }

            }while(!exit);
        } catch(DaoException e) {
            System.out.println("** Error connecting to the database. **" + e.getMessage());
        }

    }
    /**
     *  Author: Luke Hilliard
     *
     *  Displays the main menu (Default)
     */
    private static void displayMainMenu() {
        System.out.println("------* Employee Database ------*");
        System.out.println("\t.1 Display Entity by ID\n\t.2 Display all Entities\n\t.3 Delete Entity by ID\n\t.4 Add an Entity\n\t.5 Get Images List\n\t.-1 Exit");
    }


    /**
     *  Author: Haroldas Tamosauskas
     *  Other contributors: Luke Hilliard
     *
     *  Displays all Employees in the database
     */
    private static void displayAllEmployees(EmployeeDaoInterface dao) throws DaoException {
        try {
            System.out.println("\n\nFinding all the Employees");
            List<Employee> employees = dao.getAllEmployees();

            if (employees.isEmpty())
                System.out.println("There are no Employees");
            else {
                for (Employee employee : employees)
                    System.out.println("Employee: " + employee.toString());
            }
        } catch(DaoException e) {
            System.out.println("** Error getting employee **" + e.getMessage());
        }

    }


    /**  TODO
     *  Author: Luke Hilliard
     *
     *  Display one employee
     */
    private static void displayOneEmployee(Employee employee)  {
        // does not need try catch
    }


    /**
     *  Author: Haroldas Tamosauskas
     *  Other contributors: Luke Hilliard
     *
     *  Displays all Employees in the database
     */
    private static void findEmployeeByID(EmployeeDaoInterface dao) {
        try {
            Scanner input = new Scanner(System.in);
            int id = -1; // -1 cannot be a valid index
            System.out.print("Enter an ID to find: ");
            id = input.nextInt();
            System.out.println("\nFinding the Employee with ID -> " + id);

            Employee employee = dao.getEmployeeById(id);

            // TODO use displayOneEmployee to display the results better
            if( employee != null )
                System.out.println("Employee found: " + employee);
            else
                System.out.println("Employee with that ID was not found");
            }
        catch( DaoException e)
        {
            System.out.println("** Error finding employee. **" + e.getMessage());
        }
    }


    /**
     *  Author: Luke Hilliard
     *
     *  Displays the menu for adding a new Employee
     */
    private static void displayAddEmployee(EmployeeDaoInterface dao) {
        try {
            Scanner input = new Scanner(System.in);
            String fName, lName, gender, role, username, password;
            LocalDate dateOfBirth;
            double salary;
            System.out.println("-------*   Add Employee   -------*");
            fName = validateStringInput("\tFirst Name: ");
            lName = validateStringInput("\tLast Name: ");
            gender = validateStringInput("\tGender: ");
            dateOfBirth = getDateOfBirth();
            salary = getAnnualSalary();
            role = validateStringInput("\tRole: ");

            // TODO hash password??, create better validation for these two in particular
            username = validateStringInput("\tUsername: ");
            password = validateStringInput("\tPassword: ");

            dao.addEmployee(new Employee(fName, lName, gender, dateOfBirth, salary, role, username, password));

        } catch(DaoException e) {
            System.out.println("** Error creating new employee. **" + e.getMessage());
        }

    }


    /**
     *  Author: Luke Hilliard
     *
     *  Takes a message as a parameter to display again if invalid input,
     *  loops until input is a string only containing letters.
     *
     * @param requestMessage e.g. "First Name: "
     * @return validated String
     */
    private static String validateStringInput(String requestMessage) {
        Scanner input = new Scanner(System.in);
        String validStr = "";
        System.out.print(requestMessage);
        // set to always true so loop can only exit from break
        while(true) {

            validStr = input.nextLine();

            // Check if the input string contains ONLY letters
            if(validStr.matches("^[a-zA-Z]+$")) {
                break;
            } else {
                System.out.print("** Invalid input. Please enter a string containing only letters. **\n\n" + requestMessage);
            }
        }
        // return validated string
        return validStr;
    }

    /**
     *  Author: Luke Hilliard
     *
     *  Use this method whenever you want to get a date of birth.
     *  Tries to parse user input with date format 'yyyy-MM-dd' catches invalid format,
     *  loops infinitely until return is hit
     *
     * @return Employees date of birth in the correct format.
     */
    private static LocalDate getDateOfBirth() {
        Scanner input = new Scanner(System.in);

        System.out.print("\tDate of Birth(yyyy-MM-dd):");
        // true so loop can only exit from return
        while(true) {
            try{
                String dateString = input.nextLine();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                // if input matches format & can be parsed to LocalDate
                return LocalDate.parse(dateString, dateFormatter);

            } catch(DateTimeParseException e) { // catch any invalid formats and try input again
                System.out.print("** Invalid date format. Please enter the date in the format YYYY-MM-DD **\n** (e.g. 2024-01-31) **\n");
                System.out.print("\n\tDate of Birth(yyyy-MM-dd):");
            }
        }
    }

    /**
     * Author: Luke Hilliard
     *
     * Use this method whenever you want to get the annual salary.
     * Tries to parse user input to double, catches invalid input and tries again.
     * This method could also be user to validate double inputs.
     *
     * @return validated annual salary.
     */
    private static double getAnnualSalary() {
        Scanner input = new Scanner(System.in);

        // true so loop can only exit from return
        while (true) {
            try {
                System.out.print("\tAnnual Salary: ");
                return Double.parseDouble(input.nextLine()); // if the input cannot be parsed to a double, it is invalid
            } catch (NumberFormatException e) {
                System.out.print("\n** Invalid input. Please enter a valid annual salary. **\n");
            }
        }
    }

    }
