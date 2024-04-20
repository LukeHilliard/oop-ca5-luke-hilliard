package ClientServer;

import DAOs.EmployeeDaoInterface;
import DAOs.MySqlEmployeeDao;
import DTOs.Employee;
import Utilities.JsonConverter;
import Exceptions.DaoException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
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
    final String[] IMAGE_FILES = {"images/david-lee.jpeg", "images/emily-brown.jpeg", "images/jane-smith.jpeg", "images/john-doe.jpg", "images/micheal-johnson.jpg"};

    BufferedReader socketReader;
    PrintWriter socketWriter;
    Socket clientSocket;
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

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
            JsonConverter converter = new JsonConverter();
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
                         * Other contributors: Luke Hilliard
                         *
                         */
                        try {
                            String response = converter.jsonEmployeeList();
                            socketWriter.println(response);

                            // Sends an error to the client
                        } catch (DaoException e) {
                            System.out.println("** Error getting all employees. **" + e.getMessage());
                        }
                        break;
                    case "2": // Find employee by ID
                        /**
                         * Main author: Katie Lynch
                         */
                        System.out.println("Client Requested To Find Employee By ID");
                        int id;
                        id = Integer.parseInt(parameters[1]);
                        //getting and displaying employee from database
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
                    case "4":
                        System.out.println("**** client has requested employee image file names ****\n");
                        StringBuilder fileNames = new StringBuilder();
                        for(int i = 0; i < IMAGE_FILES.length; i++) { // build a string containing only the employee names and not any information about the image
                            fileNames.append(i).append(". ").append(IMAGE_FILES[i], IMAGE_FILES[i].indexOf("/") + 1, IMAGE_FILES[i].indexOf("."));
                            //System.out.println(IMAGE_FILES[i].substring(IMAGE_FILES[i].indexOf("/") + 1, IMAGE_FILES[i].indexOf(".")));
                        }
                        socketWriter.println(fileNames);
                        socketWriter.println("END_OF_DATA");
                        socketWriter.flush();
                        break;
                    case "SEND_IMAGE":
                        System.out.println("sending image to client");
                        int imageId = Integer.parseInt(parameters[1]);

                        try {
                            dataInputStream = new DataInputStream(clientSocket.getInputStream());
                            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                            sendFile(IMAGE_FILES[imageId]);
                            dataInputStream.close();
                            dataOutputStream.close();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        System.out.println("Image sent");


                        break;

                    default:
                        System.out.println("Invalid request");
                        break;
                }
            }
        }catch(IOException ex){
            System.out.println("Client Handler (Server) IOException: " + ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally{
            this.socketWriter.close();
            try {
                this.socketReader.close();
                this.clientSocket.close();
            } catch (IOException ex) {
                System.out.println("Client Handler (Server) IOException " + ex);
            }
        }
    }

    private static void sendFile(String path)
            throws Exception
    {
        int bytes = 0;
        // Open the File at the specified location (path)
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        // send the length (in bytes) of the file to the server
        dataOutputStream.writeLong(file.length());

        // Here we break file into chunks
        byte[] buffer = new byte[4 * 1024]; // 4 kilobyte buffer

        // read bytes from file into the buffer until buffer is full or we reached end of file
        while ((bytes = fileInputStream.read(buffer))!= -1) {
            // Send the buffer contents to Server Socket, along with the count of the number of bytes
            dataOutputStream.write(buffer, 0, bytes);
            dataOutputStream.flush();   // force the data into the stream
        }
        // close the file
        fileInputStream.close();

    }

}



