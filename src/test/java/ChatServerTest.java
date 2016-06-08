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
            fail();
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
            fail();
        }
    }

    @Test
    public void testDupicateLogon(){
        //Create a socket to send messages to the chat server
        Socket clientOne;
        Socket clientTwo;
        try {
            clientOne = new Socket("localhost",1337);
            clientTwo = new Socket("localhost",1337);
            //Obtain reader and writer for socket
            BufferedReader inClientOne = new BufferedReader(new InputStreamReader(clientOne.getInputStream()));
            PrintWriter outClientOne = new PrintWriter(clientOne.getOutputStream(), true);
            BufferedReader inClientTwo = new BufferedReader(new InputStreamReader(clientTwo.getInputStream()));
            PrintWriter outClientTwo = new PrintWriter(clientTwo.getOutputStream(), true);
            //Attempt to log in twice with the same user name
            outClientOne.println("LOGON|User2");
            String responseOne = inClientOne.readLine();
            outClientTwo.println("LOGON|User2");
            String responseTwo = inClientTwo.readLine();
            //Error
            assertEquals("ERROR|There is already a User2 in the chatroom.",responseTwo);
        } catch (IOException e){
            System.out.println(e);
            fail();
        }
    }

    @Test
    public void testServerLogoff(){
        Socket client;
        try {
            //User will log on, and then log off
            client = new Socket("localhost",1337);
            //Obtain reader and writer for socket
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            out.println("LOGON|User3");
            in.readLine();
            out.println("LOGOFF|User3");
            String logoffSuccessMessage = in.readLine();
            assertEquals("User3 has logged off successfully.",logoffSuccessMessage);
        } catch (IOException e) {
            System.out.println(e);
            fail();
        }
    }

    @Test
    public void testServerLogoffNotLoggedIn(){
        Socket client;
        try {
            //User will log off
            client = new Socket("localhost",1337);
            //Obtain reader and writer for socket
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            out.println("LOGOFF|User4");
            String logoffFailMessage = in.readLine();
            assertEquals("ERROR|You are not logged in yet.",logoffFailMessage);
        } catch (IOException e) {
            System.out.println(e);
            fail();
        }
    }
}
