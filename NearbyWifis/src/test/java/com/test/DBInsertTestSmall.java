package com.test;

import com.api.db.QueryType;
import com.nw.DBAgent;
import com.nw.QueryBuilder;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class DBInsertTestSmall {

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
		
		Connection connnection = agent.connect(url, user, pw);
		PreparedStatement pstmt = agent.compile(connnection, spec, QueryType.INSERT);
		
		String s1 = "s1";	spec.setQueryField("X_SWIFI_MGR_NO", s1);
		String s2 = "s2";	spec.setQueryField("X_SWIFI_WRDOFC", s2);
		String s3 = "s3";	spec.setQueryField("X_SWIFI_MAIN_NM", s3);
		String s4 = "s4";	spec.setQueryField("X_SWIFI_ADRES1", s4);
		String s5 = "s5";	spec.setQueryField("X_SWIFI_ADRES2", s5);
		String s6 = "s6";	spec.setQueryField("X_SWIFI_INSTL_FLOOR", s6);
		String s7 = "s7";	spec.setQueryField("X_SWIFI_INSTL_TY", s7);
		String s8 = "s8";	spec.setQueryField("X_SWIFI_INSTL_MBY", s8);
		String s9 = "s9";	spec.setQueryField("X_SWIFI_SVC_SE", s9);
		String s10 = "s10";	spec.setQueryField("X_SWIFI_CMCWR", s10);
		
		LocalDate _date11 = LocalDate.now();
		Date date11 = Date.valueOf(_date11);	spec.setQueryField("X_SWIFI_CNSTC_YEAR", date11);
		
		Boolean b12 = true;	spec.setQueryField("X_SWIFI_INOUT_DOOR", b12);
		String s13 = "s13";	spec.setQueryField("X_SWIFI_REMARS3", s13);
		Double db14 = 1.;	spec.setQueryField("LAT", db14);
		Double db15 = 2.;	spec.setQueryField("LNT", db15);
		
		LocalDate _date16 = LocalDate.now();	
		Date date16 = Date.valueOf(_date16);	spec.setQueryField("WORK_DTTM", date16);
				
		agent.load(pstmt, spec);
		agent.close(connnection, pstmt);
	}

}
