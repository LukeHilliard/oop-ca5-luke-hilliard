package ClientServer;

import DAOs.EmployeeDaoInterface;
import DAOs.MySqlEmployeeDao;
import DTOs.Employee;
import Exceptions.DaoException;
import Utilities.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


/**
 * Main author: Luke Hilliard
 *
 *
 */
public class Client {

    final int SERVER_PORT_NO = 8888;
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
    public void start(){
        try(
                Socket socket = new Socket("localhost", SERVER_PORT_NO);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        ){
            System.out.println("Client: client has connected to server.");
            Scanner input = new Scanner(System.in);
            Scanner submenuInput = new Scanner(System.in);
            boolean exit = false;
            boolean hasSelectedDisplayAll, hasSelectedById;
            //socket.setSoTimeout(50); debugging while loop not exiting when setting (response = in.readline) != null
            do{
                int option;
                displayMainMenu();
                System.out.print(":");
                hasSelectedDisplayAll = false;
                hasSelectedById = false;

                option = input.nextInt();
                String request = "0";
                String response = "";
                switch (option) {
                    case 1: // Display all employees;
                        request = Integer.toString(option);
                        hasSelectedDisplayAll = true;
                        break;
                    case 2: // Find employee by ID
                        int id;
                        System.out.print("\nEnter Character ID (-1 to exit): ");
                        while(!input.hasNextInt() && input.nextInt() >= -1){
                            System.out.println("*--- Invalid ID Entered ---*");
                            input.next();
                        }
                        id = input.nextInt();
                        input.nextLine();
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
                    case 4:
                        int employeeChoice;
                        System.out.println("+---------------* Client Access *---------------+\nRequesting files...\n");
                        out.println(option);
                        while((response = in.readLine()) != null && !response.equals("END_OF_DATA")) { // display options, sending a termination message from the server, without it the program gets stuck at this loop indefinitely
                                System.out.println(response);
                        }
                        System.out.print("Select an employee: ");
                        while(!input.hasNextInt()) {
                            System.out.print("Invalid input, select a number relating to an employee\n: ");
                            input.next();
                        }
                        employeeChoice = input.nextInt();

                        out.println("SEND_IMAGE" + "&" + employeeChoice);

                        try {
                            dataInputStream = new DataInputStream(socket.getInputStream());
                            dataOutputStream = new DataOutputStream(socket.getOutputStream());
                            receiveFile("received/new-image");
                            dataInputStream.close();
                            dataOutputStream.close();
                        }catch(Exception e) {
                            e.printStackTrace();
                        }

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

                    if(hasSelectedDisplayAll) {
                        String jsonString = in.readLine();
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                                .create();
                        Type listType = new TypeToken<List<Employee>>(){}.getType(); // use TypeToken to for entities from json to Employee Objects
                        List<Employee> employees = gson.fromJson(jsonString, listType);
                        displayAllEmployees(employees);
                    }

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Exiting online system...");

    }


    private static void receiveFile(String fileName)
            throws Exception
    {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        // DataInputStream allows us to read Java primitive types from stream e.g. readLong()
        // read the size of the file in bytes (the file length)
        long bytes_remaining = dataInputStream.readLong(); // bytes remaining to be read (initially equal to file size)
        System.out.println("Server: file size in bytes = " + bytes_remaining);

        // create a buffer to receive the incoming bytes from the socket
        byte[] buffer = new byte[4 * 1024];         // 4 kilobyte buffer

        System.out.println("Server:  Bytes remaining to be read from socket: ");
        int bytes_read = 0;    // number of bytes read from the socket

        // next, read the raw bytes in chunks (buffer size) that make up the image file
        while (bytes_remaining > 0 &&  (bytes_read =
                dataInputStream.read(buffer, 0,(int)Math.min(buffer.length, bytes_remaining))) != -1) {

            // above, we read a number of bytes from stream to fill the buffer (if there are enough remaining)
            // - the number of bytes we must read is the smallest (min) of: the buffer length and the remaining size of the file
            //- (remember that the last chunk of data read will usually not fill the buffer)

            // Here we write the buffer data into the local file
            fileOutputStream.write(buffer, 0, bytes_read);

            // reduce the 'bytes_remaining' to be read by the number of bytes read in.
            // 'bytes_remaining' represents the number of bytes remaining to be read from the source file (via socket)
            // We repeat this until all the bytes are dealt with and the size is reduced to zero
            bytes_remaining = bytes_remaining - bytes_read;

            System.out.print(bytes_remaining + ", ");
        }

        System.out.println("File is Received");

        //System.out.println("Look in the images folder to see the transferred file: parrot_image_received.jpg");
        fileOutputStream.close();
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
                            |             .4 Image Server                   |
                            |                                               |
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


    /**
     * Read file length from socket, using a buffer, and write the received bytes to a local file.
     *
     * @param fileName - name for new local file to store received data
     * @throws Exception
     */

}


