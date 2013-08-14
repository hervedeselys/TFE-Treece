package com.ulg.game.with.a.purpose;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;


public class SocketClientManager extends AsyncTask<String, Void, Object>{
	
	private Socket clientSocket;
	private String simon = "139.165.144.110";
	private int port = 8080;
	private Object response;
	private Object dataToSend;
	
	public SocketClientManager() {}
	
	public void setDataToSend(Object data){
		dataToSend = data;
	}
	
	protected Object doInBackground(String... params) {
		try {
			
			String request = params[0];
			clientSocket = new Socket();
			clientSocket.connect(new InetSocketAddress(simon, port), 2000);
						
			//System.out.println(request);
			PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
			InputStream is = clientSocket.getInputStream();
			OutputStream os = clientSocket.getOutputStream();
			pw.println(request);
			
			//We have something to send and to receive
			if (request.equals("newdataset")){	  
				ObjectOutputStream oos = new ObjectOutputStream(os);  
				oos.writeObject(dataToSend);
				ObjectInputStream ois = new ObjectInputStream(is);
				response = ois.readObject();			
				oos.close();
				ois.close();
				os.close();
			}
			//We have something to send
			else if (request.equals("equationlinefromgame")
					|| request.equals("equationlinefromlearning")){
				ObjectOutputStream oos = new ObjectOutputStream(os);  
				oos.writeObject(dataToSend);
				oos.flush();
				oos.close();
				os.close();
			}			
			//Closing
			pw.close();
			is.close();
			clientSocket.close();
						
		} catch (UnknownHostException e) {
			System.err.println("Connection failed: unknown host");
            System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println("I/O: Could not sending the request or receive the response.");
            System.err.println(e.getMessage());  

		} catch (ClassNotFoundException e) {
			System.err.println("Class not found Exception");
            System.err.println(e.getMessage());  
		}	
		return response;
	}
}
		
	

			


