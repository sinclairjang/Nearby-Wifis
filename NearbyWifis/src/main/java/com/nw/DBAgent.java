package com.nw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.api.db.QueryType;
import com.api.db.mariadb.MariaDBConnection;

public class DBAgent {
	
	public Connection connect(String url, String user, String pw) {
		MariaDBConnection mdbc = new MariaDBConnection();
		
		Connection connection = null;
		
		try {
			connection = mdbc.mdConnect(url, user, pw);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return connection;
	}
	
	public PreparedStatement compile(Connection connection, QueryBuilder spec, QueryType queryType, Object... args) {
		PreparedStatement pstmt = null;
		
		if (queryType == QueryType.INSERT) {
			return compileInsertQuery(connection, spec);		
		} else if (queryType == QueryType.SELECT_LIMIT) {
			return compileSelectLimitQuery(connection, spec, args);
		} else if (queryType == QueryType.SELECT_WHERE) {
			return compileSelectWhereQuery(connection, spec, args);
		} else if (queryType == QueryType.SELECT_NATURAL_JOIN) {
			return compileSelectNaturalJoin(connection, spec, args);
		}
		
		return pstmt;
	}
	

	private PreparedStatement compileSelectNaturalJoin(Connection connection, QueryBuilder spec, Object... args) {
		PreparedStatement pstmt = null;
		String sql = spec.buildQuery(QueryType.SELECT_NATURAL_JOIN, args);
		
		try {
			pstmt = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return pstmt;
	}

	private PreparedStatement compileInsertQuery(Connection connection, QueryBuilder spec) {
		PreparedStatement pstmt = null;
		String sql = spec.buildQuery(QueryType.INSERT);
		
		try {
			pstmt = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return pstmt;
	}
	
	private PreparedStatement compileSelectLimitQuery(Connection connection, QueryBuilder spec, Object... args) {
		PreparedStatement pstmt = null;
		String sql = spec.buildQuery(QueryType.SELECT_LIMIT, args);
		
		try {
			pstmt = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return pstmt;
	}
	
	private PreparedStatement compileSelectWhereQuery(Connection connection, QueryBuilder spec, Object... args) {
		PreparedStatement pstmt = null;
		String sql = spec.buildQuery(QueryType.SELECT_WHERE, args);
		
		try {
			pstmt = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return pstmt;
	}
	
	public void close(PreparedStatement pstmt) {
		try {
			if (pstmt != null && !pstmt.isClosed()) {
				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void close(Connection connection) {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
	public void close(ResultSet rs) {
		try {
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
		} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
	public void close(Connection connection, PreparedStatement pstmt) {
		try {
			if (pstmt != null && !pstmt.isClosed()) {
				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
	public void load(PreparedStatement pstmt, QueryBuilder spec) {	
		try {			
			pstmt.setString(1, (String) spec.getQueryField("X_SWIFI_MGR_NO"));
			pstmt.setString(2, (String) spec.getQueryField("X_SWIFI_WRDOFC"));
			pstmt.setString(3, (String) spec.getQueryField("X_SWIFI_MAIN_NM"));
			pstmt.setString(4, (String) spec.getQueryField("X_SWIFI_ADRES1"));
			pstmt.setString(5, (String) spec.getQueryField("X_SWIFI_ADRES2"));
			pstmt.setString(6, (String) spec.getQueryField("X_SWIFI_INSTL_FLOOR"));
			pstmt.setString(7, (String) spec.getQueryField("X_SWIFI_INSTL_TY"));
			pstmt.setString(8, (String) spec.getQueryField("X_SWIFI_INSTL_MBY"));
			pstmt.setString(9, (String) spec.getQueryField("X_SWIFI_SVC_SE"));
			pstmt.setString(10, (String) spec.getQueryField("X_SWIFI_CMCWR"));
			pstmt.setString(11, (String) spec.getQueryField("X_SWIFI_CNSTC_YEAR"));
			pstmt.setBoolean(12, (Boolean) spec.getQueryField("X_SWIFI_INOUT_DOOR"));
			pstmt.setString(13, (String) spec.getQueryField("X_SWIFI_REMARS3"));
			pstmt.setDouble(14, (Double) spec.getQueryField("LAT"));
			pstmt.setDouble(15, (Double) spec.getQueryField("LNT"));
			pstmt.setTimestamp(16, (Timestamp) spec.getQueryField("WORK_DTTM"));
			
			int affected = pstmt.executeUpdate();
			
			if (affected > 0) {
				// System.out.println(String.format("INSERT OK!\n %d row(s) affected", affected));
			} else {
				System.out.println(String.format("INSERT failed"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 	
	}
	
	public ResultSet read(PreparedStatement pstmt) {
		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			String state = e.getSQLState();
			if (state.equals("42S02")) { // 42S02: Non-existent table state
				System.out.println("Table not found.");
			}
		}
		
		return rs;
	}

	public void pxLoad(PreparedStatement pstmt, QueryBuilder pxSpec) {
		try {			
			pstmt.setString(1, (String) pxSpec.getQueryField("X_SWIFI_MGR_NO"));
			pstmt.setDouble(2, (Double) pxSpec.getQueryField("DISTANCE"));
			
			int affected = pstmt.executeUpdate();
			
			if (affected > 0) {
				// System.out.println(String.format("INSERT OK!\n %d row(s) affected", affected));
			} else {
				System.out.println(String.format("INSERT failed"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 	
	}

	public void shLoad(PreparedStatement pstmt, QueryBuilder shSpec) {		
	    try {			
			pstmt.setDouble(1, (Double) shSpec.getQueryField("latitude"));
			pstmt.setDouble(2, (Double) shSpec.getQueryField("longitude"));
			pstmt.setTimestamp(3, (Timestamp) shSpec.getQueryField("access_date"));
			
			int affected = pstmt.executeUpdate();
			
			if (affected > 0) {
				// System.out.println(String.format("INSERT OK!\n %d row(s) affected", affected));
			} else {
				System.out.println(String.format("INSERT failed"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 	
	    
		
	}

}
