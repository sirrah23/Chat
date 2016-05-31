import java.net.*;
import java.io.*;

public class User implements Runnable{
    private Socket userSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public User(Socket userSocket){
        this.userSocket = userSocket;
        try {
            this.in = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
            this.out = new PrintWriter(userSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void run(){
        try {
            String initialMessage = in.readLine();
            if(!initialMessage.startsWith("LOGON")){
                out.println("ERROR|The user is not logged in yet.");
                return;
            }
            this.username = initialMessage.substring(initialMessage.indexOf('|')+1);
            out.println(username + " has joined the chat.");
            return;
        } catch (IOException e){
            System.out.println(e);
        }
    }
}
