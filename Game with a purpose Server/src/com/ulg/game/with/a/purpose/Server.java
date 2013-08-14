package com.ulg.game.with.a.purpose;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
 
	private int port = 8080;
	private ServerSocket serverSocket;
	private int treeID = 0;
 
    public static void main(String[] args) {	
    	new Server();
    }
    
    public Server(){
    	    	
		try {
			System.out.println("Server: Server is running...");
			System.out.println("Server: creation of socket server");
			serverSocket = new ServerSocket(port);
			MySqlConnection conn = new MySqlConnection();
			treeID = conn.getLastTreeID();
			System.out.println("Last Tree ID: "+treeID);
			System.out.println("Server OK. Listenning port " + port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port);
	    	System.err.println(e.getMessage());
		}
		   	
        while(true){
			try {		
				//System.out.println("Server: waiting for client...");
				Socket clientSocket = serverSocket.accept();   //accept the client connection
				//System.out.println("Server: reading the request...");
				InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
				BufferedReader in = new BufferedReader(isr); //get the client message
				String request = in.readLine();
				//System.out.println("Server: request is "+ request);
				if(request.equals("newdataset"))
					treeID++;
	        	SocketClientThread clientThread = new SocketClientThread(clientSocket, request, treeID);
	        	clientThread.start();			
		    }catch (IOException e) {
		    	System.err.println("Could not listen on port: " + port);
		    	System.err.println(e.getMessage());
		    }
        }
    } 
}