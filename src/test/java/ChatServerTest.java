import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.*;
import java.net.*;
import java.io.*;

public class ChatServerTest {

    static ChatServer cs;

    @BeforeClass
    public static void initialize(){
        // Create the chat server
        cs = new ChatServer();
        Thread serverThread = new Thread(cs);
        serverThread.start();
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
            assertEquals("LOGON|SUCCESS|User1",response);
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
            assertEquals("LOGOFF|SUCCESS|User3",logoffSuccessMessage);
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

    @Test
    public void testServerPropogateMessage(){
        Socket clientA;
        Socket clientB;

        try {

            clientA = new Socket("localhost",1337);
            clientB = new Socket("localhost",1337);

            BufferedReader inA = new BufferedReader(new InputStreamReader(clientA.getInputStream()));
            PrintWriter outA = new PrintWriter(clientA.getOutputStream(), true);
            BufferedReader inB = new BufferedReader(new InputStreamReader(clientB.getInputStream()));
            PrintWriter outB = new PrintWriter(clientB.getOutputStream(), true);

            outA.println("LOGON|User5");
            inA.readLine();
            outB.println("LOGON|User6");
            inB.readLine();

            outA.println("MESSAGE|User5|HIYA!");
            String messageFromA = inB.readLine();

            assertEquals("MESSAGE|User5|HIYA!",messageFromA);
        } catch (IOException e) {
            System.out.println(e);
            fail();
        }
    }

    @AfterClass
    public static void endServer(){
        cs.stopChatServer();
    }
}
