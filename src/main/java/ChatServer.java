import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer implements Runnable{

    //Be able to run server in its own thread for testing
    public void run(){
        main(new String[]{});
    }

    public static void main(String[] args){

        LinkedList<User> userList = new LinkedList<User>();

        try{
            ServerSocket listener = new ServerSocket(1337);
            while(true){
                User newUser = new User(listener.accept(),userList);
                new Thread(newUser).start();
            }
        } catch (IOException e){
            System.out.println(e);
        }
    }
}
