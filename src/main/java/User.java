import java.net.*;
import java.io.*;
import java.util.*;

public class User implements Runnable{
    private Socket userSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private List userList;

    public User(Socket userSocket, List<User> userList){
        this.userList = userList;
        try {
            this.in = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
            this.out = new PrintWriter(userSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public synchronized void subscribeToUserList(){
        this.userList.add(this);
    }

    public void run(){
        try {
            String initialMessage = in.readLine();
            if(!initialMessage.startsWith("LOGON")){
                out.println("ERROR|You are not logged in yet.");
                return;
            }
            this.username = initialMessage.substring(initialMessage.indexOf('|')+1);
            out.println(username + " has joined the chat.");
            //TODO validate user not already in list
            subscribeToUserList();
            //TODO add loop to listen for message continuously
            return;
        } catch (IOException e){
            System.out.println(e);
        }
    }
}
