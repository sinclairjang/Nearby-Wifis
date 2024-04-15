package com.api.db.mariadb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDBConnection {

	public Connection mdConnect(String url, String user, String pw) 
			throws SQLException {
		
		try {
			Class.forName("org.mariadb.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Connection connection = DriverManager.getConnection(url, user, pw);
	
		return connection;	
	}
}
