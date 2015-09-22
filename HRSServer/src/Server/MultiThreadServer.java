package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadServer {
    public MultiThreadServer() throws IOException{
	try{
	    ServerSocket serverSocket = new ServerSocket(8000);
	    while(true){
		Socket socket = serverSocket.accept();
		HandleAClient task = new HandleAClient(socket);
		new Thread(task).start();
	    }
	}catch(IOException ex){
	    System.err.println(ex);
	}
    }
}
