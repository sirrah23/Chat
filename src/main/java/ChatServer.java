import java.net.*;
import java.io.*;

public class ChatServer implements Runnable{

    //Be able to run server in its own thread for testing
    public void run(){
        main(new String[]{});
    }

    public static void main(String[] args){
        try{
            ServerSocket listener = new ServerSocket(1337);
            while(true){
                User newUser = new User(listener.accept());
                newUser.run();
            }
        } catch (IOException e){
            System.out.println(e);
        }
    }
}
