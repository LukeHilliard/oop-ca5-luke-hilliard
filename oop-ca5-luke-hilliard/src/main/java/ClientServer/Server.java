package ClientServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;

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
            System.out.println("Server Started");
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
    public ClientHandler(Socket clientSocket, int clientNumber){
        this.clientNum = clientNumber;
        this.clientSocket = clientSocket;

        try{
            this.socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                    this.socketReader = new BufferedReader(new InputStreamReader((clientSocket.getInputStream())));
        }
        catch (IOException ex){
            System.out.println("Client Handler (Server) IOException: " + ex);
        }
    }

    @Override
    public void run() {
        String request;
        try{
            while ((request = socketReader.readLine()) != null){
                System.out.println("Server: (Client Handler): Read command from client " + clientNum + "; " + request);

                if(request.startsWith("time")){
                    LocalTime time = LocalTime.now();
                    socketWriter.println(time);
                    System.out.println("Server message: time sent to client.");
                }
                //if the command is "echo" with a message (for example "Hello")
                // then it will print the message to the client ("Hello")
                else if (request.startsWith("echo")){
                    String message = request.substring(5);
                    socketWriter.println(message);
                    System.out.println("Server message: echo message sent to client");
                }
                //If the command is unknown, then it will not do anything
                else {
                    socketWriter.println("error, server does not understand request");
                    System.out.println("Server message: invalid request from client");
                }
            }
        }
        catch (IOException ex){
            System.out.println("Client Handler (Server) IOException: " + ex);
        }
        finally {
            this.socketWriter.close();
            try{
                this.socketReader.close();
                this.clientSocket.close();
            }
            catch (IOException ex){
                System.out.println("Client Handler (Server) IOException " + ex);
            }
        }
    }
}
