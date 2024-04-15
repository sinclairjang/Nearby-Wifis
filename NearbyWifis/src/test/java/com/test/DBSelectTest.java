package com.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import com.algo.MinPQ;
import com.api.db.QueryType;
import com.nw.DBAgent;
import com.nw.GCS;
import com.nw.QueryBuilder;

public class DBSelectTest {


	public static void main(String[] args) {
		String url = "jdbc:mariadb://localhost:3306/public_wifi_db";
		String user = "public_wifi_user";
		String pw = "1234";
		
		DBAgent agent = new DBAgent();
		
		QueryBuilder spec = new QueryBuilder("SEOUL_PUBLIC_WIFI",
				"X_SWIFI_MGR_NO",
				"X_SWIFI_WRDOFC",
				"X_SWIFI_MAIN_NM",
				"X_SWIFI_ADRES1",
				"X_SWIFI_ADRES2",
				"X_SWIFI_INSTL_FLOOR",
				"X_SWIFI_INSTL_TY",
				"X_SWIFI_INSTL_MBY",
				"X_SWIFI_SVC_SE",
				"X_SWIFI_CMCWR",
				"X_SWIFI_CNSTC_YEAR",
				"X_SWIFI_INOUT_DOOR",
				"X_SWIFI_REMARS3",
				"LAT",
				"LNT",
				"WORK_DTTM"
				);
		
		
		Instant profileStart = Instant.now();
		Connection connnection = agent.connect(url, user, pw);
		int totalItemCount = 25160;
		PreparedStatement pstmt = agent.compile(connnection, spec, QueryType.SELECT_LIMIT, 
				"X_SWIFI_MGR_NO", "LAT", "LNT",
				totalItemCount);
		
		ResultSet rs = agent.read(pstmt);
		MinPQ<GCS> mpq = new MinPQ<>(totalItemCount);
		//TODO: User input
		GCS.setPivot(37.55187, 127.01254);
		if (rs != null) {
			try {
				while (rs.next()) {
					String id = rs.getString("X_SWIFI_MGR_NO");
					Double latitude = rs.getDouble("LAT");
					Double longitude = rs.getDouble("LNT");
					
					mpq.insert(new GCS(id, latitude, longitude));
					System.out.println(
							id + ", " + 
							latitude + ", " + 
							longitude + ", "
							);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		int delCount = 20;
		String[] filterValues = new String[delCount];
		int i = 0;
		while(!mpq.isEmpty() && delCount > 0) {
			GCS min = mpq.delMin();
			filterValues[i++] = min.getId();
			
			delCount--;
		}
		
		System.out.println(Arrays.toString(filterValues));
		
		agent.close(pstmt);
		
		QueryBuilder.WhereClause whereClause = spec.makeWhereClause("X_SWIFI_MGR_NO");
		whereClause.setCondition("X_SWIFI_MGR_NO", filterValues);
		pstmt = agent.compile(connnection, spec, QueryType.SELECT_WHERE, 
				"X_SWIFI_MGR_NO", "LAT", "LNT",
				whereClause);
		
		rs = agent.read(pstmt);
		if (rs != null) {
			try {
				while (rs.next()) {
					String id = rs.getString("X_SWIFI_MGR_NO");
					Double latitude = rs.getDouble("LAT");
					Double longitude = rs.getDouble("LNT");
					
					System.out.println(
							id + ", " + 
							latitude + ", " + 
							longitude + ", "
							);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		agent.close(connnection, pstmt);
		Instant profileEnd = Instant.now();
		double timeElapsed = Duration.between(profileStart, profileEnd).toMillis() / 1000.;
		System.out.println(String.format("[DB] Select(%d rows) took %f seconds", totalItemCount, timeElapsed));
	}

}
