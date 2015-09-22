package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Database {
    private Connection conn;
    
    public void openConnection() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        System.out.println("SERVER:DB Driver loaded");
        this.conn = DriverManager.getConnection("jdbc:mysql://localhost/hrs", "hrs", "123456");
        System.out.println("SERVER: DB connected.");
    }
    
    public void closeConnection() throws SQLException{
	this.conn.close();
        System.out.println("SERVER: Database connection closed.");
    }
    
    public JSONObject getPatientList(String tableName) throws ClassNotFoundException, SQLException{
	openConnection();
        Statement statement = this.conn.createStatement();
        String query = "SELECT * FROM " + tableName; 
        ResultSet resultSet = statement.executeQuery(query);
        
        //Iterate through the result and write them into JSON
        JSONArray patientList = new JSONArray();
        while(resultSet.next()){
            JSONObject patient = new JSONObject();
            patient.put("name", resultSet.getString(1));
            patient.put("id",resultSet.getString(2));
            patientList.add(patient);
        }
        JSONObject sendResult = new JSONObject();
        sendResult.put("list", patientList);
        System.out.println("SERVER: Get result:\n" + sendResult.toString());
        //Close the connection
        closeConnection();
        return sendResult;
    }
    
    public void addPatient(String tableName,String patientName,String patientID) throws ClassNotFoundException, SQLException{
	openConnection();
        Statement statement = conn.createStatement();
        String query = "INSERT INTO " + tableName + " (name,id) VALUES ('" + patientName + "','" + patientID + "');";
        statement.executeUpdate(query);
        System.out.println("SERVER: Patient added.");
        closeConnection();
    }
    
    public void deletePatient(String tableName,String patientName) throws ClassNotFoundException, SQLException{
	openConnection();
        Statement statement = conn.createStatement();
        String query = "DELETE FROM " + tableName + " WHERE name = '" + patientName + "';";
        System.out.println(query);
        statement.executeUpdate(query);
        System.out.println("SERVER: Patient deleted.");
        closeConnection();
    }
}
