package com.ulg.game.with.a.purpose;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class SocketClientThread extends Thread{
	
	private Socket clientSocket;
	private String request;
	private int treeID;
	
	public SocketClientThread(){
		super();
	}
	
	SocketClientThread(Socket s, String r, Integer t){	
		clientSocket = s;	
		treeID = t;
		request = r;
	}

	public void run()  {		
        try{       	
        	InputStream is = clientSocket.getInputStream();
        	OutputStream os = clientSocket.getOutputStream();
        	MySqlConnection conn;
        	ObjectInputStream ois = null;
        	ObjectOutputStream oos = null;
        	switch (request) {
			case "newdataset":
				//System.out.println("Server: new game created");
        		ois = new ObjectInputStream(is);
				String databaseName = (String) ois.readObject();
				//System.out.println("Server: database "+databaseName);
        		conn = new MySqlConnection();
        		List<String[]> data = null;  		
    			data = conn.getData(databaseName);
    			String[] id = {String.valueOf(treeID)};
    			//Add the tree ID at the end of the list
    			data.add(id);
    			//System.out.println("Server: scatter plots generated");
        		oos = new ObjectOutputStream(os);
				oos.writeObject(data);
				oos.flush();
				//System.out.println("Server: confirmation sent");
				oos.close();
				ois.close();
				break;				
			
			case "equationlinefromgame":
				//System.out.println("Server: receiving equation from game...");
        		ois = new ObjectInputStream(is);
				EquationLine lineFromGame = (EquationLine) ois.readObject();
				conn = new MySqlConnection();
				conn.sendEquationLine("GAME", lineFromGame);
				ois.close();
				break;
			case "equationlinefromlearning":
				//System.out.println("Server: receiving equation from learning...");
        		ois = new ObjectInputStream(is);
				EquationLine lineFromLearning = (EquationLine) ois.readObject();
				conn = new MySqlConnection();
				conn.sendEquationLine("LEARNING", lineFromLearning);
				ois.close();
				break;
			default:
				break;
			}
        	
			os.close();
			is.close();
			clientSocket.close();
        	//System.out.println("Server: data sent");
        } catch (IOException e) {
            System.err.println("I/O exception");
            System.err.println(e.getMessage());    
        } catch (ClassNotFoundException e) {
        	System.err.println("Class not found Exception");
            System.err.println(e.getMessage());    
        }
	 }
}
