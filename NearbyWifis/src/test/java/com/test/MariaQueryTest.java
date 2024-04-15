package com.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import com.nw.DBAgent;

public class MariaQueryTest {


	public static void main(String[] args) {
		String url = "jdbc:mariadb://localhost:3306/public_wifi_db";
		String user = "public_wifi_user";
		String pw = "1234";
		
		DBAgent agent = new DBAgent();
		
		Connection dbConnection = agent.connect(url, user, pw);
		
		String tableName = "_generated_SP41YKKHZB4a5M0m";
		String id = "2";
		String shSql = "DELETE FROM " + tableName
				+ " WHERE id = " + id
				+ ";";
		Statement stmt = null;
		try {
			stmt = dbConnection.createStatement();
			stmt.executeUpdate(shSql);
			
			dbConnection.setAutoCommit(false);
			shSql = "SET @num := 0;";
			stmt.addBatch(shSql);
			shSql = "UPDATE QUERY_HISTORY SET id = @num := (@num+1);";
			stmt.addBatch(shSql);
			shSql = "ALTER TABLE " + tableName + " AUTO_INCREMENT = 1;";
			stmt.addBatch(shSql);
			stmt.executeBatch();
			dbConnection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				dbConnection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
