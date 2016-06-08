import java.net.*;
import java.io.*;
import java.util.*;

/**
 * This class contains the information for a user that is connected
 * to the chat server. The class implements Runnable so that each user
 * can run in its own thread. This will allow multiple users to send/recieve
 * messages in the chatroom.
 */
public class User implements Runnable{
    private Socket userSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private ChatServer chatserver;

    public User(Socket userSocket, String username, ChatServer chatserver){
        this.userSocket = userSocket;
        this.username = username;
        this.chatserver = chatserver;
        try {
            this.in = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
            this.out = new PrintWriter(userSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Will send a string to the user client.
     *
     * @param Message string
     */
    public void sendUserClientMessage(String message){
        out.println(message);
    }

    /**
     * Recieves messages from the client that should follow
     * the chat server protocol. If the action the client
     * wants performed is valid then we let the server know.
     */
    public void run(){
        try {
            while(true){
                String clientActionMessage = in.readLine();
                if(clientActionMessage.startsWith("MESSAGE")){
                    //TODO - broadcast message to server
                } else if (clientActionMessage.startsWith("LOGOFF")){
                    this.chatserver.userLogoff(this);
                    break;
                } else {
                    //TODO - send some kind of "invalid action" message back
                }
            }
            this.userSocket.close();
            return;
        } catch (IOException e){
            System.out.println(e);
        }
    }

    public String getUsername(){
        return username;
    }
}
