package com.ulg.game.with.a.purpose;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlConnection {
	
	private String mySqlServer = "jdbc:mysql://localhost/herve_tfe";
	private String userName = "herve_tfe";
	private String password = "RzdVY5xsLEdRPv3Q";
	
	//from: http://zetcode.com/db/mysqljava/
	public MySqlConnection() {}
	
	public void sendEquationLine(String mode, EquationLine line){
		try {
			
			Connection conn = DriverManager.getConnection(mySqlServer,userName, password);
			PreparedStatement pst = conn.prepareStatement( "INSERT INTO `"+mode+"`" +
					"(`id`, `playstore`, `treeID`, `nodeID`, `classID`, `parentID`, `leftChildID`, " +
					"`rightChildID`, `database`, `attributX`, `attributY` , " +
					"`slope`, `y-Intercept`)" +
					" VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
			
			if(line.getDatabase().equals("END")){
				pst.setBoolean(1, line.isGooglePlayStore());
				pst.setInt(2, line.getTreeID());
				pst.setInt(3, 0);
				pst.setInt(4, 0);
				pst.setInt(5, 0);
				pst.setInt(6, 0);
				pst.setInt(7, 0);
				pst.setString(8, line.getDatabase());
				pst.setInt(9, 0);
				pst.setInt(10, 0);
				pst.setFloat(11, 0);
				pst.setFloat(12, 0);
			}
			else{
				pst.setBoolean(1, line.isGooglePlayStore());
				pst.setInt(2, line.getTreeID());
				pst.setInt(3, line.getNodeID());
				pst.setInt(4, line.getMajorityClassID());
				pst.setInt(5, line.getParentID());
				pst.setInt(6, line.getLeftChildID());
				pst.setInt(7, line.getRightChildID());
				pst.setString(8, line.getDatabase());
				pst.setInt(9, line.getAttributePair().getFirst());
				pst.setInt(10, line.getAttributePair().getSecond());
				pst.setFloat(11, line.getSlope());
				pst.setFloat(12, line.getYIntercept());
			}
			
		    pst.executeUpdate();
		    
		    pst.close();
		    conn.close();
			
		} catch (SQLException e) {
			System.err.println("SQL Exception");
		    System.err.println(e.getMessage());
		}
	}
	
	public List<String[]> getData(String databaseName){
		List<String[]> data = new ArrayList<String[]>();
		try {
			Connection conn = DriverManager.getConnection(mySqlServer,userName, password);
			PreparedStatement pst = conn.prepareStatement("SELECT * from `"+databaseName+"`");
			ResultSet rs = pst.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();	
			int numberOfColumns = rsmd.getColumnCount();
			//while there are records to read
			while (rs.next()){
				String[] record = new String[numberOfColumns];
				for (int i=1; i <= numberOfColumns; i++){
					record[i-1] = rs.getString(i);				
				}
				data.add(record);
			}
			
		} catch (SQLException e) {
			System.err.println("SQL Exception");
		    System.err.println(e.getMessage());
		}
		return data;
	}
	
	public int getLastTreeID(){
		int lastTreeID = 0;
		try {
			int learningLastTreeID = 0;
			Connection conn = DriverManager.getConnection(mySqlServer,userName, password);
			PreparedStatement pst = conn.prepareStatement("SELECT * from " +
					"`LEARNING` ORDER BY `id` DESC LIMIT 1");
			ResultSet rs = pst.executeQuery();
			if (rs.next())
				learningLastTreeID = rs.getInt(3);
			
			int gameLastTreeID = 0;
			conn = DriverManager.getConnection(mySqlServer,userName, password);
			pst = conn.prepareStatement("SELECT * from `GAME` ORDER " +
					"BY `id` DESC LIMIT 1");
			rs = pst.executeQuery();
			if (rs.next())
				gameLastTreeID = rs.getInt(3);
			
			if (gameLastTreeID > learningLastTreeID)
				lastTreeID = gameLastTreeID;
			else
				lastTreeID = learningLastTreeID;
		} catch (SQLException e) {
			System.err.println("SQL Exception");
			System.err.println(e.getMessage());
		}
		return lastTreeID;
	}
	
	public List<String> getDatabasesName(){
		List<String> databasesName = new ArrayList<String>();
		try {
			Connection conn = DriverManager.getConnection(mySqlServer,userName, password);	
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rsDatabases = md.getTables(null, null, "%", null);
			while (rsDatabases.next()) {
				String name = rsDatabases.getString(3);
				
				if (name.equals("LEARNING") || name.equals("GAME"))
					continue;
				databasesName.add(name);
			}
			
		} catch (SQLException e) {
			System.err.println("SQL Exception");
		    System.err.println(e.getMessage());
		}
		
		return databasesName;	
	}

}
