package ClientServer;

import DAOs.EmployeeDaoInterface;
import DAOs.MySqlEmployeeDao;
import DTOs.Employee;
import Exceptions.DaoException;
import Utilities.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Scanner;


/**
 * Main author: Luke Hilliard
 *
 *
 */
public class Client {

    final int SERVER_PORT_NO = 8888;

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
    public void start(){
        try(
                Socket socket = new Socket("localhost", SERVER_PORT_NO);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ){
            System.out.println("Client: client has connected to server.");
            Scanner input = new Scanner(System.in);
            Scanner submenuInput = new Scanner(System.in);
            boolean exit = false;
            boolean hasSelectedById = false;

            do{
                int option;
                displayMainMenu();
                System.out.print(":");

                option = input.nextInt();
                String request = "0";
                String response = "";
                switch (option) {
                    case 1: // Display all employees;
                        request = Integer.toString(option);
                        break;
                    case 2: // Find employee by ID
                        int id;
                        System.out.print("\nEnter Character ID (-1 to exit): ");
                        while(!input.hasNextInt() || input.nextInt() < -1){
                            System.out.println("*--- Invalid ID Entered ---*");
                            input.next();
                        }
                        id = input.nextInt();
                        if(id == -1){
                            System.out.println("\n");
                            break;
                        }
                        request = option + "&" + id;
                        hasSelectedById = true;

                        break;
                    case 3: // Add an employee
                        Scanner employeeInput = new Scanner(System.in);
                        String fName, lName, gender, role, username, password, dateOfBirth, salary;

                        System.out.println("-------*   Add Employee   -------*\n");
                        System.out.print("\n\tFirst Name: ");
                        fName = employeeInput.nextLine();

                        System.out.print("\n\tLast Name: ");
                        lName = employeeInput.nextLine();

                        System.out.print("\n\tGender: ");
                        gender = employeeInput.nextLine();

                        System.out.print("\nDate of Birth(yyyy-MM-DD):");
                        dateOfBirth = employeeInput.nextLine();

                        System.out.print("\n\tSalary: €");
                        salary = employeeInput.nextLine();

                        System.out.print("\n\tRole: ");
                        role = employeeInput.nextLine();

                        System.out.print("\n\tUsername: ");
                        username = employeeInput.nextLine();

                        System.out.print("\n\tPassword: ");
                        password = employeeInput.nextLine();

                        request = option + "&" + fName + "&" + lName +"&" + gender + "&" + dateOfBirth + "&" + salary + "&" + role + "&" + username + "&" + password;
                        break;
                    case -1:
                        while(true) {
                            System.out.print("\nAre you sure you want to disconnect from the server?\ny/n\t:");
                            char confirm = submenuInput.nextLine().charAt(0);

                            if(confirm == 'y') { // TODO disconnect and reconnect to server from App
                                exit = true;
                                break;
                            } else  if (confirm == 'n') { // return to menu
                                break;
                            } else { // stay in loop
                                System.out.println("Invalid input enter 'y' or 'n'\n");
                            }
                        }
                }
                if(!exit) {
                    out.println(request);

                    if(hasSelectedById) {
                        String jsonString = in.readLine();
                        Gson gsonParser = new GsonBuilder()
                                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                                .create();
                        Employee employeeFromJson = gsonParser.fromJson(jsonString, Employee.class);
                        displayOneEmployee(employeeFromJson);
                    }
                }
            }while(!exit);
        } catch (IOException ex){
            System.out.println("Client IOException: " + ex);
        }
        System.out.println("Exiting online system...");



    }
    /**
     * Author: Luke Hilliard
     *
     */
    private static void displayMainMenu() {
        System.out.println("+---------------* Client Access *---------------+");
        System.out.println("""
                            |                                               |
                            |             .1 Display all Employees          |
                            |             .2 Display Employee               |
                            |             .3 Add an Employee                |
                            |                                               |
                            |           .-1 Exit                            |
                            +-----------------------------------------------+
                            """);
    }

    /**
     * Author: Luke Hilliard
     * Takes a list of characters as a parameter and displays them
     *
     * @param employee database information of one character
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




}
