package Objects;

import DAOs.MySqlEmployeeDao;
import DAOs.EmployeeDaoInterface;
import DTOs.Employee;
import DTOs.JsonConverter;
import Exceptions.DaoException;
import Exceptions.InvalidIdException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Main author: Luke Hilliard
 * Other contributors: ...
 *
 */

public class App {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean exit = false;

        try{
            do{
                int choice;
                EmployeeDaoInterface IEmployeeDao = new MySqlEmployeeDao();
                displayMainMenu();
                System.out.print(":");

                choice = input.nextInt();
                switch (choice) {
                    case -1:
                        System.out.println("\n\nProgram ending...");
                        exit = true;
                        break;
                    case 1:
                        // call method1 to display all employees, pass a call to method2 as a parameter, method 2 returns a List of Employee objects
                        displayAllEmployees(getAllEmployees(IEmployeeDao));
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
                        updateEmployeeByID(IEmployeeDao);
                        break;
                    case 6:
                        findEmployeeMatchingFilter(IEmployeeDao);
                        break;
                    case 7:
                        displayJsonOptions(IEmployeeDao);
                        break;

                }

            }while(!exit);
        } catch(DaoException e) {
            System.out.println("** Error connecting to the database. **" + e.getMessage());
        }
    }


    /**
     *  Author: Haroldas Tamosauskas
     *  Other contributors: Luke Hilliard
     *  Displays all Employees in the database
     *
     * @param dao interface
     */
    private static List<Employee> getAllEmployees(EmployeeDaoInterface dao) throws DaoException{
        List<Employee> employeeList = new ArrayList<>();
        try {
            System.out.println("\nRetrieving all employees...");
            employeeList = dao.getAllEmployees();

            if (employeeList.isEmpty())
                System.out.println("There are no Employees\n");

        } catch(DaoException e) {
            System.out.println("** Error getting employee **" + e.getMessage());
        }

        return employeeList;
    }


    /**
     * Author: Haroldas Tamosauskas
     * Other contributors: Luke Hilliard
     * Displays all Employees in the database
     *
     * @param dao interface
     */
    private static void findEmployeeByID(EmployeeDaoInterface dao) {
        int id;

        try {
            // stay in infinite loop until user wants to return back to main menu by entering -1
            while(true) {
                id = validateIntInput("\nEnter an ID to find (-1 to return): ");
                if(id == -1) {
                    System.out.println("\n");
                    break; // exit loop and return to main menu
                }

                // create new employee object from database
                System.out.println("Finding employee...");
                Employee employee = dao.getEmployeeById(id);

                // if database returned an employee, display it
                if (employee != null)
                    displayOneEmployee(employee);
            }

        } catch(DaoException e) {
            System.out.println("** Error finding employee. **" + e.getMessage());
        }
    }


    /**
     * Author: Katie Lynch
     * Other contributors: Luke Hilliard
     * Deleting an Employee from the database
     *
     * @param dao interface
     */
    private static void deleteEmployeeByID(EmployeeDaoInterface dao){

        try{
            while(true) {
                int id;
                boolean confirmDelete = false;
                boolean selectedDisplayAll = false;

                id = validateIntInput("\nEnter ID of Employee to be deleted (-1 to return, -2 to display all): ");
                if(id == -1) { // exit method
                    System.out.println("Cancelling...\n");
                    return;
                }
                if(id == -2) { // display all and skip over this iteration of the process
                    selectedDisplayAll = true;
                    System.out.println("\nRetrieving all employees...");
                    displayAllEmployees(dao.getAllEmployees());
                }


                // if user hasn't selected display all, their input is used here, else skip over this and ask for id again
                if(!selectedDisplayAll) {

                    if (id > -1) {
                        if (dao.getEmployeeById(id) != null) { // if id returns a result
                            Scanner input = new Scanner(System.in);
                            char choice;

                            // initialize an employee for displaying
                            Employee employeeView = dao.getEmployeeById(id);
                            displayOneEmployee(employeeView);

                            // confirm deletion
                            System.out.print("Are you sure you want to delete " + employeeView.getFirstName() + " " + employeeView.getLastName() + "? \ny/n:");
                            choice = input.next().charAt(0);

                            // Lock user here until they make a decision
                            while (true) {
                                if (choice == 'y') {
                                    confirmDelete = true;
                                    break;
                                } else if (choice == 'n') {
                                    id = -1;
                                    break;
                                } else {
                                    System.out.print("\n* Invalid, enter 'y' for YES, 'n' for NO *\n y/n: ");
                                }
                                choice = input.next().charAt(0); // take input again
                            }

                        }
                    } else {
                        throw new InvalidIdException("Please enter a valid employee ID");
                    }
                }

                if(confirmDelete) {
                    System.out.println("Deleting Employee with ID: " + id);
                    dao.deleteEmployee(id);
                }
            }

        } catch (DaoException ex){
            System.out.println("** Error deleting employee **" + ex.getMessage());
        } catch(InvalidIdException e){
            System.out.println(e.getMessage());
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


            username = validateStringInput("\tUsername: ");
            password = validateStringInput("\tPassword: ");

            dao.addEmployee(new Employee(fName, lName, gender, dateOfBirth, salary, role, username, password));

        } catch(DaoException e) {
            System.out.println("** Error creating new employee. **" + e.getMessage());
        }

    }


    /**
     *  Author: Katie Lynch
     *  Displays the menu for updating an existing Employee
     */
    private static void updateEmployeeByID(EmployeeDaoInterface dao){
        try {
            displayAllEmployees(dao.getAllEmployees());
        }catch(DaoException e){
            System.out.println("----* Error connecting to database *----");
        }

        try{
            Scanner kbrd = new Scanner(System.in);
            int id;
            String fName, lName, gender, role, username, password, wantToUpdate;
            double salary;
            LocalDate dateOfBirth;
            System.out.println("*---- Update An Employee ----*");
            System.out.println("\nEnter ID Of Employee You Want To Update:");
            id = kbrd.nextInt();
            //checks ID entered is valid
            if(id <= 0){
                System.out.println("The Employee ID You Want To Update Must Be Above 0");
            }else{
                Employee employee = dao.getEmployeeById(id);
                //checks employee has data
                if(employee != null){
                    System.out.println("Employee To Update: ");
                    displayOneEmployee(employee);
                    System.out.println("Enter What You Want To Update:");
                    wantToUpdate = kbrd.next();
                    //if the user wants to update a field, that field's data changes but the original data stays
                    if(wantToUpdate.equalsIgnoreCase("first_name")){
                        fName = validateStringInput("First Name: ");
                        lName = employee.getLastName();
                        gender = employee.getGender();
                        dateOfBirth = employee.getDob();
                        salary = employee.getSalary();
                        role = employee.getRole();
                        username = employee.getUsername();
                        password = employee.getPassword();
                        dao.updateEmployee(id, new Employee(fName, lName, gender, dateOfBirth, salary, role, username, password));
                    }else if(wantToUpdate.equalsIgnoreCase("last_name")){
                        fName = employee.getFirstName();
                        lName = validateStringInput("Last Name: ");
                        gender = employee.getGender();
                        dateOfBirth = employee.getDob();
                        salary = employee.getSalary();
                        role = employee.getRole();
                        username = employee.getUsername();
                        password = employee.getPassword();
                        dao.updateEmployee(id, new Employee(fName, lName, gender, dateOfBirth, salary, role, username, password));
                    }else if(wantToUpdate.equalsIgnoreCase("gender")){
                        fName = employee.getFirstName();
                        lName = employee.getLastName();
                        gender = validateStringInput("Gender: ");
                        dateOfBirth = employee.getDob();
                        salary = employee.getSalary();
                        role = employee.getRole();
                        username = employee.getUsername();
                        password = employee.getPassword();
                        dao.updateEmployee(id, new Employee(fName, lName, gender, dateOfBirth, salary, role, username, password));
                    }else if(wantToUpdate.equalsIgnoreCase("dob")){
                        fName = employee.getFirstName();
                        lName = employee.getLastName();
                        gender = employee.getGender();
                        dateOfBirth = getDateOfBirth();
                        salary = employee.getSalary();
                        role = employee.getRole();
                        username = employee.getUsername();
                        password = employee.getPassword();
                        dao.updateEmployee(id, new Employee(fName, lName, gender, dateOfBirth, salary, role, username, password));
                    }else if(wantToUpdate.equalsIgnoreCase("salary")){
                        fName = employee.getFirstName();
                        lName = employee.getLastName();
                        gender = employee.getGender();
                        dateOfBirth = employee.getDob();
                        salary = getAnnualSalary();
                        role = employee.getRole();
                        username = employee.getUsername();
                        password = employee.getPassword();
                        dao.updateEmployee(id, new Employee(fName, lName, gender, dateOfBirth, salary, role, username, password));
                    }else if(wantToUpdate.equalsIgnoreCase("role")){
                        fName = employee.getFirstName();
                        lName = employee.getLastName();
                        gender = employee.getGender();
                        dateOfBirth = employee.getDob();
                        salary = employee.getSalary();
                        role = validateStringInput("Role: ");
                        username = employee.getUsername();
                        password = employee.getPassword();
                        dao.updateEmployee(id, new Employee(fName, lName, gender, dateOfBirth, salary, role, username, password));
                    }else if(wantToUpdate.equalsIgnoreCase("username")){
                        fName = employee.getFirstName();
                        lName = employee.getLastName();
                        gender = employee.getGender();
                        dateOfBirth = employee.getDob();
                        salary = employee.getSalary();
                        role = employee.getRole();
                        username = validateStringInput("Username: ");
                        password = employee.getPassword();
                        dao.updateEmployee(id, new Employee(fName, lName, gender, dateOfBirth, salary, role, username, password));
                    }else if(wantToUpdate.equalsIgnoreCase("password")){
                        fName = employee.getFirstName();
                        lName = employee.getLastName();
                        gender = employee.getGender();
                        dateOfBirth = employee.getDob();
                        salary = employee.getSalary();
                        role = employee.getRole();
                        username = employee.getUsername();
                        password = validateStringInput("Password: ");
                        dao.updateEmployee(id, new Employee(fName, lName, gender, dateOfBirth, salary, role, username, password));
                    }else{
                        System.out.println("This Field Does Not Exist");
                    }
                }else{
                    System.out.println("Employee With That ID Was Not Found");
                }
            }

        }catch(DaoException ex){
            System.out.println("Encountered An Error Updating Employee: " + ex.getMessage());
        }
    }

    /**
     * Author: Luke Hilliard
     * Filter employees by a specified comparator and order.
     * Filter by: First name            Order by: Ascending
     *            Date of Birth                   Descending
     *            Salary
     *
     *
     * @param dao connection
     */
    private static void findEmployeeMatchingFilter(EmployeeDaoInterface dao) {
        List<Employee> filteredEmployeeList;
        String filterMessage = "";

        // Ask for the type of filter to use
        System.out.println("+-----* Select Filter *-----+");
        System.out.println("""
            \t.1 By First Name
            \t.2 By Date of Birth
            \t.3 By Salary

            \t.-1 Return""");
        int filterChoice = validateIntInput(":");

        if(filterChoice == -1) // exit
            return;

        // validate input further to keep it within range 0 - 3
        while(filterChoice < 0 || filterChoice > 3 ) {
            System.out.println("--* Input " + filterChoice + " is out of bounds *--");
            filterChoice = validateIntInput(":");
        }
        // based on user input from the menu options, set a variable to the type of filter they want
        String filter = "";
        switch(filterChoice) {
            case 1:
                filter = "fName";
                break;
            case 2:
                filter = "dob";
                break;
            case 3:
                filter = "salary";
                break;
            default:
                System.out.println("----* Invalid option, select a corresponding number from the menu. *----");
        }

        // Ask for the order to display them
        System.out.println("+-----* Select Order *-----+");
        System.out.println("""
                            \t.1 Ascending
                            \t.2 Descending
                
                            """);
        int orderChoice = validateIntInput(":");

        // validate input further to keep it within range 1 - 2
        while(orderChoice < 1 || orderChoice > 2 ) {
            System.out.println("--* Input " + orderChoice + " is out of bounds *--");
            orderChoice = validateIntInput(":");
        }

        boolean order;
        // set order based on input

        order = orderChoice == 1;

        // set filter message
        switch(filter) {
            case "fName":
                if(order)
                    filterMessage = "Retrieving employees based on first name ASC...";
                else
                    filterMessage = "Retrieving employees based on first name DESC...";
                break;
            case "dob":
                if(order)
                    filterMessage = "Retrieving employees based on date of birth ASC...";
                else
                    filterMessage = "Retrieving employees based on date of birth DESC...";
                break;
            case "salary":
                if(order)
                    filterMessage = "Retrieving employees based on salary ASC...";
                else
                    filterMessage = "Retrieving employees based on salary DESC...";
                break;
        }

        try{
            // pass filter name to apply the right filter before displaying
            filteredEmployeeList = dao.getEmployeesMatchingFilter(filter, order);
            System.out.println("\n" + filterMessage);
            displayAllEmployees(filteredEmployeeList);

        } catch(DaoException e) {
            System.out.println("** Error connecting to database. **" + e.getMessage());
        }
    }
    /**
     *  Author: Luke Hilliard
     *  Displays the main menu (Default)
     */
    private static void displayMainMenu() {
        System.out.println("+-------------* Employee Database *-------------+");
        System.out.println("""
                            |                                               |
                            |           .1 Display all Employees            |
                            |           .2 Display Employee                 |
                            |           .3 Delete Employee                  |
                            |           .4 Add an Employee                  |
                            |           .5 Update Employee                  |
                            |           .6 Filter Employees                 |
                            |           .7 Employee to JSON                 |
                            |                                               |
                            |           .-1 Exit                            |
                            +-----------------------------------------------+
                            """);
    }

    /**
     * Author: Luke Hilliard
     * Displays menu and takes input for options for JSON functions
     *
     * @param dao connection to database
     */
    private static void displayJsonOptions(EmployeeDaoInterface dao) {
        int choice;
        int key;
        String employeeJson;
        JsonConverter converter = new JsonConverter();
        while(true) {
            System.out.println("""
                    +--------------------* JSON *-------------------+
                    |                                               |
                    |       . 1 Display all Employees as JSON       |
                    |       . 2 Display Employee as JSON by ID      |
                    |                                               |
                    |       .-1 Return                              |
                    +-----------------------------------------------+
                    """);

            choice = validateIntInput(":");
            if(choice == -1) {
                return;
            }

            switch (choice) {
                case 1:



                    break;
                case 2:
                    // Display all employees as a table for user to select
                    // Stay in loop until user wants to return to main menu
                    while (true) {
                        boolean selectedDisplayAll = false;

                        // ask for ID of user
                        System.out.print("----* Enter an employee ID (-1 to exit, -2 to display all) ");
                        key = validateIntInput(": ");
                        if(key == -1)
                            break;

                        if(key == -2)
                            selectedDisplayAll = true;

                        // if user hasn't selected display all, do conversion else skip conversion and display all
                        if(!selectedDisplayAll) {
                            // pass key to converter
                            employeeJson = converter.employeeToJsonByKey(key);

                            // if converter did not return null, display JSON and exit loop
                            if (employeeJson != null) {
                                System.out.println(employeeJson);
                            }
                        } else {
                            try {
                                System.out.println("Retrieving all employees...");
                                displayAllEmployees(dao.getAllEmployees());
                            } catch(DaoException e) {
                                System.out.println(e.getMessage());
                            }

                        }

                    }
                    break;
                default:
                    System.out.print("---* Invalid input, select an option from the menu *---\n:");
            }
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
        String validStr;
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
     * Use this method whenever you want to take an integer value. Tries to parse user input to an
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

            }catch(NumberFormatException | InputMismatchException e){
                System.out.println("\n** Invalid input. Please enter a valid integer value. **\n");
            }
        }
    }
}
