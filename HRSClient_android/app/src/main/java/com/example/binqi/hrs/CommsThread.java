package com.example.binqi.hrs;

/**
 * Created by binqi on 9/17/15.
 */
import java.io.*;
import java.net.Socket;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class CommsThread extends Thread {
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public CommsThread(Socket sock) {
        socket = sock;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            //creates the input stream and output stream objects
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.d("!!!!client socket", e.getLocalizedMessage());
        }
        inputStream = tmpIn;
        outputStream = tmpOut;
    }

    public void run() {
        //buffer store for the stream
        //byte[] buffer = new byte[1024];
        //bytes returned from read()
        //int bytes;
        //keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                //read from the inputStream
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String result = bufferedReader.readLine();
                Log.d("!!!!!!READREAD",result);
                //update the main activity UI
                MainActivity.UIupdater.obtainMessage(MainActivity.DATA_RECEIVED, result).sendToTarget();
            } catch (IOException e) {
                Log.d("!client run", e.getLocalizedMessage());
            }
        }
    }

    // send data to the server
    public void write(String message) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(message);
            dataOutputStream.flush();
        } catch (IOException e) {
            Log.d("!!!!",e.getLocalizedMessage());
        }
    }


    // shutdown the connection
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.d("!!!!client cancel",e.getLocalizedMessage());
        }
    }
}