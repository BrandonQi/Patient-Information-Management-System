package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import DB.Database;

public class HandleAClient implements Runnable {
    private Socket socket;
    
    public HandleAClient(Socket socket){
	this.socket = socket;
    }
    @Override
    public void run(){
	try{
	    while(true){
		InputStream inStream = socket.getInputStream();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream));
		System.out.println("LISTENED");
		String in = bufferedReader.readLine();
		System.out.println("LISTENED");
		System.out.println(in);
		if(in == null){
		    break;
		}
		requestDistributor(in);
	    }
	}catch(IOException ex){
	    System.err.println(ex);
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
    
    public void requestDistributor(String json) throws ClassNotFoundException, SQLException, IOException{
	//parse the JSON request
	JSONObject obj = (JSONObject)JSONValue.parse(json);
	String request = obj.get("request").toString();
	String hospitalName = obj.get("hospitalName").toString();
	String patientName = obj.get("patientName").toString();
	String patientID = obj.get("patientID").toString();
	
	if(request.equals("patientsList")){
	    patientsList(hospitalName);
	}else if(request.equals("addPatient")){
	    addPatient(hospitalName,patientName,patientID);
	}else if(request.equals("deletePatient")){
	    deletePatient(hospitalName,patientName);
	}else{
	    System.err.println("SERVER: REQUEST CAN NOT BE EXCUTED");
	}
    }
    
    public void patientsList(String hospitalName) throws ClassNotFoundException, SQLException, IOException{
	//execute request
	Database database = new Database();
	JSONObject result = database.getPatientList(hospitalName);
	System.out.println("SERVER:" + result.toString());
	
	//output back to the client
	backToClient(result);
    }
    
    public void addPatient(String hospitalName,String patientName,String patientID) throws ClassNotFoundException, SQLException, IOException{	
	//execute request
	Database database = new Database();
	database.addPatient(hospitalName, patientName, patientID);
	JSONObject result = database.getPatientList(hospitalName);
	
	//output back to the client
	backToClient(result);
    }
    
    public void deletePatient(String hospitalName,String patientName) throws ClassNotFoundException, SQLException, IOException{
	//execute request
	Database database = new Database();
	database.deletePatient(hospitalName, patientName);
	JSONObject result = database.getPatientList(hospitalName);
	
	//output back to the client
	backToClient(result);
    }
    
    public void backToClient(JSONObject result) throws IOException{
	//output back to the client
	OutputStream outStream = this.socket.getOutputStream();
	DataOutputStream dataOutputStream = new DataOutputStream(outStream);
	dataOutputStream.writeBytes(result.toString() + "\n");
	dataOutputStream.flush();
    }
}
