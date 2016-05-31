import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.*;
import java.net.*;
import java.io.*;

public class ChatServerTest {

    @BeforeClass
    public static void initialize(){
        // Create the chat server
        ChatServer cs = new ChatServer();
        new Thread(cs).start();
    }


    @Test
    public void testServerLogon() {
        //Create a socket to send messages to the chat server
        Socket client;
        try {
            client = new Socket("localhost",1337);
            //Obtain reader and writer for socket
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            //Use the socket to send log onto the server
            out.println("LOGON|User1");
            String response = in.readLine();
            //See that the user was logged on
            assertEquals("User1 has joined the chat.",response);
        } catch (IOException e){
            System.out.println(e);
        }
    }

    @Test
    public void testServerMessageNotLoggedOn(){
        //Create a socket to send messages to the chat server
        Socket client;
        try {
            client = new Socket("localhost",1337);
            //Obtain reader and writer for socket
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            //Use the socket to send log onto the server
            out.println("MESSAGE|Hi!");
            String response = in.readLine();
            //See that the user was logged on
            assertEquals("ERROR|You are not logged in yet.",response);
        } catch (IOException e){
            System.out.println(e);
        }
    }
}
