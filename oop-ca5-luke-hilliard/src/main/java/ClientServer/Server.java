package ClientServer;

import DAOs.EmployeeDaoInterface;
import DAOs.MySqlEmployeeDao;
import DTOs.Employee;
import DTOs.JsonConverter;
import Exceptions.DaoException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;


/**
 * Main author: Luke Hilliard
 *
 *
 */
public class Server {
    final int SERVER_PORT_NO = 8888;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start(){
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(SERVER_PORT_NO);
            System.out.println("*** Employee Database Server ***");
            int clientNum = 0;

            while (true){
                System.out.println("Server listening for connections on port: " + SERVER_PORT_NO);
                clientSocket = serverSocket.accept();
                clientNum++;


                System.out.println("Server Client " + clientNum + " has connected.");
                System.out.println("Server: Port number of remote client: " + clientSocket.getPort());
                System.out.println("Server: Port number of the socket used to talk with client " + clientSocket);

                Thread thread = new Thread(new ClientHandler(clientSocket, clientNum));
                thread.start();

                System.out.println("Server: Client Handler started in thread: " + thread.getName() + " for client: " + clientNum);
            }
        }
        catch (IOException ex){
            System.out.println("Server IOException " + ex);
        }
        finally {
            try{
                if (clientSocket != null){
                    clientSocket.close();
                }
            }
            catch (IOException ex){
                System.out.println("Server IOException when closing client socket " + ex);
            }
            try{
                if (serverSocket != null){
                    serverSocket.close();
                }
            }
            catch (IOException ex){
                System.out.println("Server IOException when closing client socket " + ex);
            }
        }

    }
}


class ClientHandler implements Runnable {
    BufferedReader socketReader;
    PrintWriter socketWriter;
    Socket clientSocket;
    final int clientNum;

    public ClientHandler(Socket clientSocket, int clientNumber) {
        this.clientNum = clientNumber;
        this.clientSocket = clientSocket;

        try {
            this.socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            this.socketReader = new BufferedReader(new InputStreamReader((clientSocket.getInputStream())));
        } catch (IOException ex) {
            System.out.println("Client Handler (Server) IOException: " + ex);
        }
    }

    @Override
    public void run() {
        String request;
        Scanner input = new Scanner(System.in);
        EmployeeDaoInterface dao = new MySqlEmployeeDao();
        try
        {
            while ((request = socketReader.readLine()) != null)
            {
                System.out.println("Server: (Client Handler): Read command from client " + clientNum + "-> " + request);
                String[] parameters = request.split("&"); // split request with passed delimiter
                String option = parameters[0];

                // PROTOCOL
                switch (option) {
                    case "1": // Display all employees
                        System.out.println("Client requested to display all employees");
                        /**
                         *  Main author: Haroldas Tamosauskas
                         * Other contributors: ...
                         *
                         */
                        try {
                            List<Employee> employees = dao.getAllEmployees();
                            //Will go through the employees list, and will print each of the employees to the client
                            for (Employee employee : employees) {
                                socketWriter.println(employee.toString()); // Append each employee's details
                            }

                            // Sends an error to the client
                        } catch (DaoException e) {
                            System.out.println("** Error getting all employees. **" + e.getMessage());
                        }
                        break;
                    case "2": // Find employee by ID
                        System.out.println("*******HERE");
                        /**
                         * Main author: Katie Lynch
                         */
                        System.out.println("Client Requested To Find Employee By ID");
                        int id;
                            id = Integer.parseInt(parameters[1]);
                            //getting and displaying employee from database
                            JsonConverter converter = new JsonConverter();
                            String response = converter.employeeToJsonByKey(id);

                            System.out.println("Employee found-" + response);
                            socketWriter.println(response);
                        break;

                    /**
                     * Main author: Luke Hilliard
                     */
                    case "3": // Add employee

                        // Initialize object values
                        String fName = parameters[1];
                        String lName = parameters[2];
                        String gender = parameters[3];
                        LocalDate dateOfBirth = LocalDate.parse(parameters[4]);
                        double salary = Double.parseDouble(parameters[5]);
                        String role = parameters[6];
                        String username = parameters[7];
                        String password = parameters[8];

                        try {
                            // create employee object and add it to database
                            dao.addEmployee(new Employee(fName, lName, gender, dateOfBirth, salary, role, username, password));
                        } catch (DaoException ex) {
                            System.out.println("** Error creating new employee. **" + ex.getMessage());
                        }
                        break;
                    default:
                        System.out.println("Invalid request");
                        break;
                }
            }
        }catch(IOException ex){
            System.out.println("Client Handler (Server) IOException: " + ex);
        }finally{
            this.socketWriter.close();
            try {
                this.socketReader.close();
                this.clientSocket.close();
            } catch (IOException ex) {
                System.out.println("Client Handler (Server) IOException " + ex);
            }
        }
    }


}



