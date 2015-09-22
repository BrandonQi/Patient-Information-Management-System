package Client_Test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ClientTest {
    public static void main(String[] args) {
        try {
                Socket socket = new Socket("localhost", 8000);
		System.out.println("log1");
                OutputStream outStream = socket.getOutputStream();
		System.out.println("log2");
                InputStream inStream = socket.getInputStream();
		System.out.println("log3");
		
                DataOutputStream dataOutputStream = new DataOutputStream(outStream);
		System.out.println("log4");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream));
		System.out.println("log5");
                
                String s = getJsonObject();
		System.out.println("log6");
                dataOutputStream.writeBytes(s);
		System.out.println("log7");
                dataOutputStream.flush();
		System.out.println("log8");
		String result = bufferedReader.readLine();
                System.out.println(result);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getJsonObject() {
        JSONObject obj = new JSONObject();
        obj.put("name", "weehawken");        
        return obj.toString() + '\n';
    }
}
