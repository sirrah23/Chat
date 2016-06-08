import java.net.*;
import java.io.*;
import java.util.*;

/**
 * This class consists of a chat server its associated
 * operations. The server runs in its own thread which
 * makes it easy to run and test.
 *
 * Once the server starts up clients can connect and
 * perform actions like: Login, Logout, Send a message.
 */
public class ChatServer implements Runnable{

    LinkedList<User> userList = new LinkedList<User>();
    Set<String> usernamesInUse = Collections.synchronizedSet(new HashSet<String>(20));

    /**
     * Used the run the chat server in its own thread.
     */
    public void run(){
        try{
            //TODO Get rid of the hardcode on the port #
            ServerSocket listener = new ServerSocket(1337);
            while(true){
                ChatServer.Handler userHandler = new ChatServer.Handler(listener.accept(), this);
                new Thread(userHandler).start();
            }
        } catch (IOException e){
            System.out.println(e);
        }
    }

    /**
     * Creates a User object and runs the object
     * in its own thread.
     *
     * @param The socket containing the client connection.
     * @param The username for this client.
     */
    public void userLogin(Socket clientSocket, String username){
        User newUser = new User(clientSocket, username, this);
        synchronized(userList) {
            userList.add(newUser);
        }
        usernamesInUse.add(username);
        try {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(username + " has joined the chat.");
        new Thread(newUser).start();
        } catch (IOException e){
            synchronized(userList) {
                userList.remove(newUser);
            }
            usernamesInUse.remove(username);
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

    /**
     * Given a User object, will "log off" the
     * user by removing them from the list of
     * active users and notifying the user that
     * their logoff was successful.
     *
     * Assume this method will not be called by
     * a user that is not already logged in.
     *
     * @param User object
     */
    public void userLogoff(User user){
        synchronized(this.userList) {
            this.userList.remove(user);
        }
        usernamesInUse.remove(user.getUsername());
        user.sendUserClientMessage(user.getUsername() + " has logged off successfully.");
        return;
    }

    /**
     * Checks to see if a given username is already
     * being used by someone on the server.
     * @param The username to be tested
     */
    public boolean usernameIsInUse(String username){
        return usernamesInUse.contains(username);
    }

    /**
     * This class is an inner static class that is used
     * by the ChatServer to perform the initial validation
     * when the client first contacts the server.
     */
    public static class Handler implements Runnable{
        Socket clientSocket;
        ChatServer chatserver;

        public Handler(Socket clientSocket, ChatServer chatserver){
            this.clientSocket = clientSocket;
            this.chatserver = chatserver;
        }

        /**
         * Validates that the user's initial contact is a login attempt.
         * If the login attempt is valid then it tells the ChatServer
         * to create a new User object that will be able to interact
         * with the server.
         */
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

                if(this.chatserver.usernameIsInUse(username)){
                    out.println("ERROR|There is already a " + username + " in the chatroom.");
                    this.clientSocket.close();
                    return;
                }

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
