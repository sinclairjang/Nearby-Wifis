package com.nw;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.mariadb.jdbc.Connection;

@WebListener
public class SessionListener implements HttpSessionListener {
	private SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		
	@Override
	public void sessionCreated(HttpSessionEvent e) {
		System.out.println(this.date() + ": Session " + e.getSession().getId() +
				 " created.");
		SessionRegistry.addSession(e.getSession());
	}
	
	@Override
	 public void sessionDestroyed(HttpSessionEvent e)
	 {
		HttpSession session = e.getSession();
		Connection dbConnection = (Connection) session.getAttribute("dbConnection");
		String tableName = (String) session.getAttribute("tableName");
		
		if (dbConnection != null && tableName != null) {
			System.out.println(this.date() + ": Session database " + tableName +
					" is being destroyed.");
			SessionRegistry.removeSession(e.getSession());
			
			String sql = "SELECT * "
					+ "FROM information_schema.tables "
					+ "WHERE table_schema = 'public_wifi_db' "
					+ "    AND table_name = '" + tableName + "'"
					+ "LIMIT 1;";
			
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = dbConnection.createStatement();
				rs = stmt.executeQuery(sql);
				if (rs.next()) { // Table exists
					sql = "DROP TABLE " + tableName + ";";
					try {
						stmt = dbConnection.createStatement();
						stmt.executeUpdate(sql);
					} catch (SQLException sqlError) {
						sqlError.printStackTrace();
					} finally {
						if (stmt != null) {
							try {
								stmt.close();
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
						}
					}
				} 
			} catch (SQLException sqlError) {
				sqlError.printStackTrace();
			} finally {
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException sqlError)	{
						sqlError.printStackTrace();
					}
				}
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqlError) {
						sqlError.printStackTrace();
					}
				}
			}
		}
	 }

	private String date() {
		
		return this.formatter.format(new Date());
	}
}
