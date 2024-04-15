package com.test;

import com.api.db.QueryType;
import com.nw.QueryBuilder;

public class QueryBuildTest {

	public static void main(String[] args) {		
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
		
		String sql = spec.buildQuery(QueryType.SELECT_LIMIT, 10, 20, "X_SWIFI_MGR_NO", "X_SWIFI_WRDOFC");
		System.out.println(sql);
		
		QueryBuilder.WhereClause whereClause = spec.makeWhereClause("X_SWIFI_MGR_NO", "X_SWIFI_WRDOFC");
		String[] filters = {"-WF171016", "-WF171017"};
		whereClause.setCondition("X_SWIFI_MGR_NO", filters);
		whereClause.setCondition("X_SWIFI_WRDOFC", "중구", "강남구");
		
		sql = spec.buildQuery(QueryType.SELECT_WHERE, 
				"X_SWIFI_MGR_NO", "X_SWIFI_WRDOFC", "X_SWIFI_MAIN_NM", "X_SWIFI_ADRES1", "X_SWIFI_ADRES2",
				whereClause);
		System.out.println(sql);
		
		QueryBuilder.OrderClause orderClause = spec.makeOrderClause("DISTANCE");
		QueryBuilder.TableClause otherTable = spec.makeTableClause("PROXIMITY");
		sql = spec.buildQuery(QueryType.SELECT_NATURAL_JOIN,
				otherTable,
				"X_SWIFI_MGR_NO", "X_SWIFI_WRDOFC", "X_SWIFI_MAIN_NM",
				"X_SWIFI_ADRES1", "X_SWIFI_ADRES2", "X_SWIFI_INSTL_FLOOR",
				"X_SWIFI_INSTL_TY", "X_SWIFI_INSTL_MBY", "X_SWIFI_SVC_SE",
				"X_SWIFI_CMCWR", "X_SWIFI_CNSTC_YEAR", "X_SWIFI_INOUT_DOOR",
				"X_SWIFI_REMARS3", "LAT", "LNT", "WORK_DTTM", "DISTANCE",
				orderClause
				);
		System.out.println(sql);		
				
		
	}

}
