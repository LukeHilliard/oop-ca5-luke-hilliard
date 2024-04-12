package ClientServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

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
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ){
            System.out.println("Client: client has connected to server.");
            Scanner keyboard = new Scanner(System.in);
            System.out.println("Valid commands are: \"time\"to get time, or \"echo <message>\" to get message echoed back.");
            System.out.println("Please enter a command");
            String userRequest = keyboard.nextLine();

            while(true) {
                // send the command to the server on the socket
                out.println(userRequest);      // write the request to socket along with a newline terminator (which is required)
                // out.flush();                      // flushing buffer NOT necessary as auto flush is set to true

                // process the answer returned by the server
                //
                if (userRequest.startsWith("time"))   // if user asked for "time", we expect the server to return a time (in milliseconds)
                {
                    String timeString = in.readLine();  // (blocks) waits for response from server, then input string terminated by a newline character ("\n")
                    System.out.println("Client message: Response from server after \"time\" request: " + timeString);
                }
                else if (userRequest.startsWith("echo")) // if the user has entered the "echo" command
                {
                    String response = in.readLine();   // wait for response - expecting it to be the same message that we sent to server
                    System.out.println("Client message: Response from server: \"" + response + "\"");
                }
                else if (userRequest.startsWith("quit")) // if the user has entered the "quit" command
                {
                    String response = in.readLine();   // wait for response -
                    System.out.println("Client message: Response from server: \"" + response + "\"");
                    break;  // break out of while loop, client will exit.
                }
                else {
                    System.out.println("Command unknown. Try again.");
                }

                keyboard = new Scanner(System.in);
                System.out.println("Valid commands are: \"time\" to get time, or \"echo <message>\" to get message echoed back, \"quit\"");
                System.out.println("Please enter a command: ");
                userRequest = keyboard.nextLine();
            }

        }
        catch (IOException ex){
            System.out.println("Client IOException: " + ex);
        }

        System.out.println("Exiting client, server");
    }
}
