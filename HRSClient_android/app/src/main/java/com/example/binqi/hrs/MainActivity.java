package com.example.binqi.hrs;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.ArrayAdapter;

public class MainActivity extends Activity implements View.OnClickListener,InputDialog.InputDialogListener{

    InetAddress serverAddress;
    Socket socket;
    EditText txtMessage;
    static ListView listView;
    static ArrayList<String> patientsArray;
    static ArrayAdapter adapter;
    CommsThread commsThread;

    final static int DATA_RECEIVED = 9999;

    //used for updating the UI on the main activity
    public static Handler UIupdater = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case DATA_RECEIVED:
                    String strReceived = (String)msg.obj;
                    Log.d("!!!!!!!result",strReceived);
                    //get the json array
                    try {
                        patientsArray.clear();
                        JSONObject jsonObject = new JSONObject(strReceived);
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        for(int i = 0;i < jsonArray.length();i++){
                            JSONObject patientObject = jsonArray.getJSONObject(i);
                            String name = patientObject.getString("name");
                            String id = patientObject.getString("id");
                            patientsArray.add(name);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
            adapter.notifyDataSetChanged();
            Log.d("!Handler",patientsArray.toString());
        }
    };

    private class CreateCommThreadTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                serverAddress = InetAddress.getByName("192.168.0.6");
                socket = new Socket(serverAddress, 8000);
                commsThread = new CommsThread(socket);
                commsThread.start();
            } catch (IOException e) {
                Log.d("!create thread", e.getLocalizedMessage());
            }
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMessage = (EditText) findViewById(R.id.txtMessage);
        listView = (ListView)findViewById(R.id.listView);
        patientsArray = new ArrayList<String>();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,patientsArray);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String patientName = (String) parent.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("WARNING")
                        .setMessage("Are you sure to delete " + patientName +" ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //send the message to the server
                                try {
                                    String hospitalName = txtMessage.getText().toString();
                                    String message = deleteRequest(hospitalName, patientName);
                                    sendToServer(message);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });
        Button button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(this);
        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        new CreateCommThreadTask().execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        new CloseSocketTask().execute();
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button1:
                String input = txtMessage.getText().toString();
                ArrayList<String> tableSet = new ArrayList<String>();
                tableSet.add("aHospital");
                tableSet.add("sHospital");
                if(tableSet.contains(input)){
                    //send the message to the server
                    try {
                        String message = listRequest(input);
                        sendToServer(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"No Result",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button2:
                InputDialog inputDialog = new InputDialog();
                inputDialog.show(getFragmentManager(),"InputDialog");
        }
    }

    private void sendToServer(String message) {
        new WriteToServerTask().execute(message);
    }

    private class WriteToServerTask extends AsyncTask <String, Void, Void> {
        protected Void doInBackground(String... message) {
            commsThread.write(message[0]);
            return null;
        }
    }

    private class CloseSocketTask extends AsyncTask <Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                socket.close();
            } catch (IOException e) {
                Log.d("!CloseSocketTask", e.getLocalizedMessage());
            }
            return null;
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog,String patientName,String patientId){
        //send the message to the server
        try {
            String hospitalName = txtMessage.getText().toString();
            String message = addRequest(hospitalName,patientName, patientId);
            sendToServer(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //generate the request json
    public String listRequest(String hospitalName) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("request","patientsList");
        obj.put("hospitalName", hospitalName);
        obj.put("patientName", "null");
        obj.put("patientID", "null");
        return obj.toString() + '\n';
    }

    public String addRequest(String hospitalName,String patientName,String patientId) throws JSONException{
        JSONObject obj = new JSONObject();
        obj.put("request","addPatient");
        obj.put("hospitalName", hospitalName);
        obj.put("patientName", patientName);
        obj.put("patientID", patientId);
        return obj.toString() + '\n';
    }

    public String deleteRequest(String hospitalName,String patientName) throws JSONException{
        JSONObject obj = new JSONObject();
        obj.put("request","deletePatient");
        obj.put("hospitalName", hospitalName);
        obj.put("patientName", patientName);
        obj.put("patientID", "null");
        return obj.toString() + '\n';
    }
}