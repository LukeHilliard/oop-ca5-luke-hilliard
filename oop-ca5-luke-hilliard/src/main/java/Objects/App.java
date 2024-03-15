package Objects;

import DAOs.MySqlEmployeeDao;
import DAOs.EmployeeDaoInterface;
import DTOs.Employee;
import Exceptions.DaoException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Main author: Luke Hilliard
 * Other contributors: ...
 *
 */

/*
      TODO - 1. change string validation to allow space characters
      TODO - 2.
 */
public class App {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean exit = false;

        try{
            do{
                int choice = 0;
                EmployeeDaoInterface IEmployeeDao = new MySqlEmployeeDao();
                displayMainMenu();

                choice = input.nextInt();
                switch (choice) {
                    case -1:
                        System.out.println("\n\nProgram ending...");
                        exit = true;
                        break;
                    case 1:
                        getAllEmployees(IEmployeeDao);
                        break;
                    case 2:
                        findEmployeeByID(IEmployeeDao);
                        break;
                    case 3:
                        deleteEmployeeByID(IEmployeeDao);
                        break;
                    case 4:
                        addEmployee(IEmployeeDao);
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
     *  Displays the main menu (Default)
     */
    private static void displayMainMenu() {
        System.out.println("+-----* Employee Database *-----+");
        System.out.println("""
                \t.1 Display all Entities
                \t.2 Display Entity by ID
                \t.3 Delete Entity by ID
                \t.4 Add an Entity
                \t.5 Update an existing Entity by ID
                \t.6 Get list of entities matching a filter

                \t.-1 Exit""");
    }


    /**
     *  Author: Haroldas Tamosauskas
     *  Other contributors: Luke Hilliard
     *  Displays all Employees in the database
     *
     * @param dao interface
     */
    private static void getAllEmployees(EmployeeDaoInterface dao) throws DaoException{
        try {
            System.out.println("\nFinding all employees...");
            List<Employee> employeeList = dao.getAllEmployees();

            if (employeeList.isEmpty())
                System.out.println("There are no Employees\n");
            else {
                displayAllEmployees(employeeList); // display employees as a table
            }
        } catch(DaoException e) {
            System.out.println("** Error getting employee **" + e.getMessage());
        }

    }


    /**
     * Author: Haroldas Tamosauskas
     * Other contributors: Luke Hilliard
     * Displays all Employees in the database
     *
     * @param dao interface
     */
    private static void findEmployeeByID(EmployeeDaoInterface dao) {
        try {
            int id;

            id = validateIntInput("Enter an ID to find: ");

            System.out.println("Finding employee with id " + id + "...");
            Employee employee = dao.getEmployeeById(id);

            if(employee != null)
                displayOneEmployee(employee);
            else
                System.out.println("Employee with that ID was not found\n");
        }
        catch( DaoException e)
        {
            System.out.println("** Error finding employee. **" + e.getMessage());
        }
    }


    /**
     * Author: Katie Lynch
     * Deleting an Employee from the database
     *
     * @param dao interface
     */
    private static void deleteEmployeeByID(EmployeeDaoInterface dao){

        try{
            int id;
            id = validateIntInput("Enter ID of Employee to be deleted (-1 to cancel): ");

            if(id == -1) { // exit method
                System.out.println("Cancelling...\n");
                return;
            }

            //checks that ID entered is above 0 as ID cannot be 0 or anything less
            if (id <= 0) {
                System.out.println("The Employee ID you want to delete must be above 0");
            } else {
                System.out.println("Deleting Employee with ID: " + id);
                //checks for employee ID in database and deletes it if it is there
                dao.deleteEmployee(id);
            }

        }catch (DaoException ex){
            System.out.println("** Error deleting employee **" + ex.getMessage());
        }
    }


    /**
     * Author: Luke Hilliard
     * Displays the menu for adding a new Employee
     *
     * @param dao interface
     */
    private static void addEmployee(EmployeeDaoInterface dao) {
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


    private static void findEmployeeMatchingFilter(EmployeeDaoInterface dao) {
        try {
            Scanner input = new Scanner(System.in);
            System.out.println("+-----* Employee Database *-----+");
            System.out.println("""
                \t.1 By First Name
                \t.2 By Date of Birth
                \t.3 By Salary

                \t.-1 Return""");


        } catch(DaoException e) {

        }
    }

    /**
     * Author: Luke Hilliard
     * Takes an Employee object as a parameter and displays it in table form
     *
     * @param employee details of this instance of employee
     */
    private static void displayOneEmployee(Employee employee)  {

        // Display a single employee in table form
        System.out.println("+----+--------------+--------------+--------+------------+------------+-------------------------+----------------------+----------------+");
        System.out.println("| ID |  First Name  |  Last Name   | Gender |    DOB     |   Salary   |      Role               |    Username          | Password       |");
        System.out.println("+----+--------------+--------------+--------+------------+------------+-------------------------+----------------------+----------------+");

            System.out.printf("| %-2d | %-12s | %-12s | %-6s | %10s | $% .2f | %-23s | %-20s | %-14s |%n",
                    employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getGender(), employee.getDob(), employee.getSalary(), employee.getRole(), employee.getUsername(), employee.getPassword());
        System.out.println("+----+--------------+--------------+--------+------------+------------+-------------------------+----------------------+----------------+\n");

    }

    /**
     * Author: Luke Hilliard
     * Takes a List of employees as a parameter and displays all of them in table form
     *
     * @param employeeList a list of employees, populated with results from SQL DB
     */
    private static void displayAllEmployees(List<Employee> employeeList) {
        // Display all employee in table form
        System.out.println("+----+--------------+--------------+--------+------------+------------+-------------------------+----------------------+----------------+");
        System.out.println("| ID |  First Name  |  Last Name   | Gender |    DOB     |   Salary   |      Role               |    Username          | Password       |");
        System.out.println("+----+--------------+--------------+--------+------------+------------+-------------------------+----------------------+----------------+");

        for(Employee employee : employeeList) {
            System.out.printf("| %-2d | %-12s | %-12s | %-6s | %10s | $% .2f | %-23s | %-20s | %-14s |%n",
                    employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getGender(), employee.getDob(), employee.getSalary(), employee.getRole(), employee.getUsername(), employee.getPassword());
        }
        System.out.println("+----+--------------+--------------+--------+------------+------------+-------------------------+----------------------+----------------+");
        System.out.println("|  Total: "+ employeeList.size() + "                                                                                                                            |");
        System.out.println("+----+--------------+--------------+--------+------------+------------+-------------------------+----------------------+----------------+\n");
    }



    /////////////////////////// Validation //////////////////////////////
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
     * Use this method whenever you want to get the annual salary.
     * Tries to parse user input to double, catches invalid input and tries again.
     * This method could also be user to validate double inputs.
     *
     * @return validated annual salary.
     */
    private static double getAnnualSalary() {
        Scanner input = new Scanner(System.in);

        // true so loop can only exit from return
        while(true) {
            try {
                System.out.print("\tAnnual Salary: ");
                return Double.parseDouble(input.nextLine()); // if the input cannot be parsed to a double, it is invalid

            } catch (NumberFormatException e) {
                System.out.print("\n** Invalid input. Please enter a valid annual salary. **\n");
            }
        }
    }

    /**
     * Author: Luke Hilliard
     * Use this method whenever you want to take an integer value. Tries to parse user input to a
     * Integer, catches invalid input and tries again.
     *
     * @param requestMessage e.g. "Enter ID: "
     * @return validated Integer value
     */
    private static Integer validateIntInput(String requestMessage){
        Scanner input = new Scanner(System.in);

        // true so loop can only exit from return
        while(true) {
            try{
                System.out.print(requestMessage);
                return Integer.parseInt(input.nextLine()); // if the input cannot be parsed to an integer, it is invalid

            }catch(NumberFormatException e){
                System.out.println("\n** Invalid input. Please enter a valid employee ID. **\n");
            } catch(InputMismatchException e) {
                System.out.println("\n** Invalid input. Please enter a valid integer value. **\n");
            }
        }
    }
}
