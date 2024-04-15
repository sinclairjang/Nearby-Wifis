package com.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.time.Duration;
import java.time.Instant;

import com.api.db.QueryType;
import com.api.gov.OpenAPIService;
import com.api.gov.PublicWifiInfoRow;
import com.api.gov.PublicWifiInfoTable;
import com.google.gson.Gson;
import com.nw.DBAgent;
import com.nw.QueryBuilder;
import java.sql.Timestamp;

public class DBInsertTestBig {

	public static void main(String[] args) {
		String url = "jdbc:mariadb://localhost:3306/public_wifi_db";
		String user = "public_wifi_user";
		String pw = "1234";
		
		DBAgent agent = new DBAgent();
		
		String data = "";
		
		int totalItemCount = 25160;
		int stride = 1000;
		int start = 1;
		int end = stride;
		Instant profileStart = Instant.now();
		for (int requestCount = 0; requestCount <= (totalItemCount / stride); requestCount++) {
			if (requestCount == (totalItemCount / stride)) {
				if (totalItemCount % stride == 0) break;
				end = end - stride;
				end = start + (totalItemCount % stride);
			}
			
			try {
				data = OpenAPIService.fetchData(start, end);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Gson gson = new Gson();
			
			PublicWifiInfoTable wifiTB = gson.fromJson(data, PublicWifiInfoTable.class);
			List<PublicWifiInfoRow> rows = wifiTB.getTbPublicWifiInfo().getRow();
			int listCount = rows.size();
			
			QueryBuilder spec = new QueryBuilder( "SEOUL_PUBLIC_WIFI",
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
			
			Connection connection = agent.connect(url, user, pw);
			PreparedStatement pstmt = agent.compile(connection, spec, QueryType.INSERT);
			
			for (int i = 0; i < listCount; i++) {
				String s1 = rows.get(i).getX_SWIFI_MGR_NO();		spec.setQueryField("X_SWIFI_MGR_NO", s1);
				String s2 = rows.get(i).getX_SWIFI_WRDOFC();		spec.setQueryField("X_SWIFI_WRDOFC", s2);
				String s3 = rows.get(i).getX_SWIFI_MAIN_NM();		spec.setQueryField("X_SWIFI_MAIN_NM", s3);
				String s4 = rows.get(i).getX_SWIFI_ADRES1();		spec.setQueryField("X_SWIFI_ADRES1", s4);
				String s5 = rows.get(i).getX_SWIFI_ADRES2();		spec.setQueryField("X_SWIFI_ADRES2", s5);
				String s6 = rows.get(i).getX_SWIFI_INSTL_FLOOR();	spec.setQueryField("X_SWIFI_INSTL_FLOOR", s6);
				String s7 = rows.get(i).getX_SWIFI_INSTL_TY();		spec.setQueryField("X_SWIFI_INSTL_TY", s7);
				String s8 = rows.get(i).getX_SWIFI_INSTL_MBY();		spec.setQueryField("X_SWIFI_INSTL_MBY", s8);
				String s9 = rows.get(i).getX_SWIFI_SVC_SE();		spec.setQueryField("X_SWIFI_SVC_SE", s9);
				String s10 = rows.get(i).getX_SWIFI_CMCWR();		spec.setQueryField("X_SWIFI_CMCWR", s10);
				
				String s11 = rows.get(i).getX_SWIFI_CNSTC_YEAR();	spec.setQueryField("X_SWIFI_CNSTC_YEAR", s11);
				
				Boolean b12 = rows.get(i).getX_SWIFI_INOUT_DOOR().equals("실내");		spec.setQueryField("X_SWIFI_INOUT_DOOR", b12);
				String s13 = rows.get(i).getX_SWIFI_REMARS3();	spec.setQueryField("X_SWIFI_REMARS3", s13);
				Double db14 = Double.parseDouble(rows.get(i).getLAT());	spec.setQueryField("LAT", db14);
				Double db15 = Double.parseDouble(rows.get(i).getLNT());	spec.setQueryField("LNT", db15);
				
				Timestamp date16 = Timestamp.valueOf(rows.get(i).getWORK_DTTM());	spec.setQueryField("WORK_DTTM", date16);
				agent.load(pstmt, spec);
			}
			
			agent.close(connection, pstmt);
			
			start += stride;
			end += stride;
		}
		Instant profileEnd = Instant.now();
		double timeElapsed = Duration.between(profileStart, profileEnd).toMillis() / 1000.;
		System.out.println(String.format("[DB] Insert(%d rows) took %f seconds", totalItemCount, timeElapsed));
	}

}
