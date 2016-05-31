import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer implements Runnable{

    LinkedList<User> userList = new LinkedList<User>();

    public void run(){
        try{
            ServerSocket listener = new ServerSocket(1337);
            while(true){
                ChatServer.Handler userHandler = new ChatServer.Handler(listener.accept(), this);
                new Thread(userHandler).start();
            }
        } catch (IOException e){
            System.out.println(e);
        }
    }

    public void userLogin(Socket clientSocket, String username){
        User newUser = new User(clientSocket, username, this);
        synchronized(userList) {
            userList.add(newUser);
        }
        try {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(username + " has joined the chat.");
        new Thread(newUser).start();
        } catch (IOException e){
            synchronized(userList) {
                userList.remove(newUser);
            }
            System.out.println(e);
            try {
                clientSocket.close();

            } catch (IOException e2){
                System.out.println(e2);
                return;
            }
            return;
        }
    }

    public static class Handler implements Runnable{
        Socket clientSocket;
        ChatServer chatserver;

        public Handler(Socket clientSocket, ChatServer chatserver){
            this.clientSocket = clientSocket;
            this.chatserver = chatserver;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                String loginMessage = in.readLine();

                if(!loginMessage.startsWith("LOGON")){
                    out.println("ERROR|You are not logged in yet.");
                    this.clientSocket.close();
                    return;
                }

                String username = loginMessage.substring(loginMessage.indexOf('|')+1);
                chatserver.userLogin(this.clientSocket, username);
                return;
            } catch (IOException e){
                System.out.println(e);
                try {
                    this.clientSocket.close();

                } catch (IOException e2){
                    System.out.println(e2);
                    return;
                }
                return;
            }
        }
    }
}
