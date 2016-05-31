import java.net.*;
import java.io.*;
import java.util.*;

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

    public void run(){
        try {
            while(true){
                String clientActionMessage = in.readLine();
            }
        } catch (IOException e){
            System.out.println(e);
        }
    }
}
