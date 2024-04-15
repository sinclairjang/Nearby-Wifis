package com.nw;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.algo.MinPQ;
import com.api.db.QueryType;
import com.api.gov.OpenAPIService;
import com.api.gov.PublicWifiInfoRow;
import com.api.gov.PublicWifiInfoTable;
import com.google.gson.Gson;


@WebServlet(
        name = "proximityServlet",
        urlPatterns = {"/wifi-info",
        			   "/search-history"},
        loadOnStartup = 1
)
public class ProximityServlet extends HttpServlet {
	
	private static final long serialVersionUID = -2189642969150830292L;
	private DBAgent db;
	private Connection dbConnection;
	private final int totalItemCount = 25160;
	private static int offset;
	
	@Override
	public void init() {
		ServletContext c = this.getServletContext();
		String url = c.getInitParameter("url");
		String user = c.getInitParameter("user");
		String pw = c.getInitParameter("password");
		
		db = new DBAgent();
		dbConnection = db.connect(url, user, pw);
		
		String sql = "SELECT * "
				+ "FROM information_schema.tables "
				+ "WHERE table_schema = 'public_wifi_db' "
				+ "    AND table_name = 'SEOUL_PUBLIC_WIFI' "
				+ "LIMIT 1;";
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				//System.out.println("Table exists");
			} else { // Table does not exist
				System.out.println("INITIATE SEOUL PUBLIC WIFI DATABASE...");
				initDB(db, dbConnection);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e)	{
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	
	}
	
	@Override
	public void destroy() {
		for (HttpSession session : SessionRegistry.getAllSessions()) {
			CleanSessionDB(session);
		}
		db.close(dbConnection);
	}
	
	private void CleanSessionDB(HttpSession session) {
		SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		Connection dbConnection = (Connection) session.getAttribute("dbConnection");
		String tableName = (String) session.getAttribute("tableName");
		
		System.out.println(formatter.format(new Date()) + ": Session database " + tableName +
				" is being destroyed.");
		SessionRegistry.removeSession(session);
		session.invalidate();
		
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

	private void initDB(DBAgent db, Connection connection) {
		String spwSql = "CREATE TABLE SEOUL_PUBLIC_WIFI ("
				+ "	X_SWIFI_MGR_NO      VARCHAR(255) NOT NULL"
				+ "	X_SWIFI_WRDOFC      VARCHAR(255) NULL"
				+ "	X_SWIFI_MAIN_NM     VARCHAR(255) NULL"
				+ "	X_SWIFI_ADRES1      VARCHAR(255) NULL"
				+ "	X_SWIFI_ADRES2      VARCHAR(255) NULL"
				+ "	X_SWIFI_INSTL_FLOOR VARCHAR(255) NULL"
				+ "	X_SWIFI_INSTL_TY    VARCHAR(255) NULL"
				+ "	X_SWIFI_INSTL_MBY   VARCHAR(255) NULL"
				+ "	X_SWIFI_SVC_SE      VARCHAR(255) NULL"
				+ "	X_SWIFI_CMCWR       VARCHAR(255) NULL"
				+ "	X_SWIFI_CNSTC_YEAR  VARCHAR(255) NULL"
				+ "	X_SWIFI_INOUT_DOOR  BIT          NULL"
				+ "	X_SWIFI_REMARS3     VARCHAR(255) NULL"
				+ "	LAT                 DOUBLE       NULL"
				+ "	LNT                 DOUBLE       NULL"
				+ "	WORK_DTTM           TIMESTAMP    NULL"
				+ ")";
		
		Statement stmt = null;
		try {
			stmt = dbConnection.createStatement();
			stmt.execute(spwSql);
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
		
		String data = "";
		
		int stride = 1000;
		int start = 1;
		int end = stride;
		Instant profileStart = Instant.now();
		QueryBuilder spwSpec = new QueryBuilder("SEOUL_PUBLIC_WIFI",
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
		
		PreparedStatement pstmt = db.compile(connection, spwSpec, QueryType.INSERT);
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
			
			for (int i = 0; i < listCount; i++) {
				String s1 = rows.get(i).getX_SWIFI_MGR_NO();		spwSpec.setQueryField("X_SWIFI_MGR_NO", s1);
				String s2 = rows.get(i).getX_SWIFI_WRDOFC();		spwSpec.setQueryField("X_SWIFI_WRDOFC", s2);
				String s3 = rows.get(i).getX_SWIFI_MAIN_NM();		spwSpec.setQueryField("X_SWIFI_MAIN_NM", s3);
				String s4 = rows.get(i).getX_SWIFI_ADRES1();		spwSpec.setQueryField("X_SWIFI_ADRES1", s4);
				String s5 = rows.get(i).getX_SWIFI_ADRES2();		spwSpec.setQueryField("X_SWIFI_ADRES2", s5);
				String s6 = rows.get(i).getX_SWIFI_INSTL_FLOOR();	spwSpec.setQueryField("X_SWIFI_INSTL_FLOOR", s6);
				String s7 = rows.get(i).getX_SWIFI_INSTL_TY();		spwSpec.setQueryField("X_SWIFI_INSTL_TY", s7);
				String s8 = rows.get(i).getX_SWIFI_INSTL_MBY();		spwSpec.setQueryField("X_SWIFI_INSTL_MBY", s8);
				String s9 = rows.get(i).getX_SWIFI_SVC_SE();		spwSpec.setQueryField("X_SWIFI_SVC_SE", s9);
				String s10 = rows.get(i).getX_SWIFI_CMCWR();		spwSpec.setQueryField("X_SWIFI_CMCWR", s10);
				
				String s11 = rows.get(i).getX_SWIFI_CNSTC_YEAR();	spwSpec.setQueryField("X_SWIFI_CNSTC_YEAR", s11);
				
				Boolean b12 = rows.get(i).getX_SWIFI_INOUT_DOOR().equals("실내");		spwSpec.setQueryField("X_SWIFI_INOUT_DOOR", b12);
				String s13 = rows.get(i).getX_SWIFI_REMARS3();	spwSpec.setQueryField("X_SWIFI_REMARS3", s13);
				Double db14 = Double.parseDouble(rows.get(i).getLAT());	spwSpec.setQueryField("LAT", db14);
				Double db15 = Double.parseDouble(rows.get(i).getLNT());	spwSpec.setQueryField("LNT", db15);
				
				Timestamp date16 = Timestamp.valueOf(rows.get(i).getWORK_DTTM());	spwSpec.setQueryField("WORK_DTTM", date16);
				db.load(pstmt, spwSpec);
			}
			
			
			start += stride;
			end += stride;
		}
		db.close(pstmt);
		Instant profileEnd = Instant.now();
		double timeElapsed = Duration.between(profileStart, profileEnd).toMillis() / 1000.;
		System.out.println(String.format("[DB] Insert(%d rows) took %f seconds", totalItemCount, timeElapsed));
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			System.out.println(String.format("Sending session id(%s) to the client...", session.getId()));
		}
		session.setAttribute("dbConnection", dbConnection);
		String servletPath = request.getServletPath();
		if (servletPath.equals("/wifi-info")) {
			serviceWifiInfo(request, response);
		}
		else if (servletPath.equals("/search-history")) {
			serviceSearchHistory(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		String servletPath = request.getServletPath();
		if (servletPath.equals("/search-history")) {
			deleteSelectedRow(request, response);
		}
	}
	
	private void deleteSelectedRow(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		HttpSession session = request.getSession();
		String tableName = (String) session.getAttribute("tableName");
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
			shSql = "UPDATE " + tableName + " SET id = @num := (@num+1);";
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

	private void serviceWifiInfo(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		String queryLat = request.getParameter("lat");
		String queryLnt = request.getParameter("lnt");
		if (queryLat != null && queryLnt != null) {
			searchNearestWifis(request, response, queryLat, queryLnt);
			makeSearchHistory(request, queryLat, queryLnt);
		} else {
			viewWifiInfo(request, response);
		}
	}

	private void viewWifiInfo(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String next = request.getParameter("next");
		String prev = request.getParameter("prev");
		if(next == null && prev == null) {
			offset = 0;
		}
		else if (prev != null) {
			int temp = offset - 20;
			offset = Math.max(temp, 0);
		}
		else if (next != null) {
			int temp = offset + 20;
			offset = Math.min(temp, totalItemCount);
		}
		
		QueryBuilder spwSpec = new QueryBuilder(
				"SEOUL_PUBLIC_WIFI",
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
		
		PreparedStatement pstmt = db.compile(dbConnection, spwSpec, QueryType.SELECT_LIMIT, 
				offset,
				20, 
				"X_SWIFI_MGR_NO", "X_SWIFI_WRDOFC", "X_SWIFI_MAIN_NM",
				"X_SWIFI_ADRES1", "X_SWIFI_ADRES2", "X_SWIFI_INSTL_FLOOR",
				"X_SWIFI_INSTL_TY", "X_SWIFI_INSTL_MBY", "X_SWIFI_SVC_SE",
				"X_SWIFI_CMCWR", "X_SWIFI_CNSTC_YEAR", "X_SWIFI_INOUT_DOOR",
				"X_SWIFI_REMARS3", "LAT", "LNT", "WORK_DTTM");
		
		ResultSet rs = db.read(pstmt);
		request.setAttribute("result", rs);
		request.getRequestDispatcher("/WEB-INF/jsp/view/wifi-table.jsp")
		.forward(request, response);
		
		db.close(pstmt);
	}

	private void searchNearestWifis(HttpServletRequest request, HttpServletResponse response, String queryLat, String queryLnt) 
				throws ServletException, IOException {
		QueryBuilder spwSpec = new QueryBuilder("SEOUL_PUBLIC_WIFI",
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
		PreparedStatement pstmt = db.compile(dbConnection, spwSpec, QueryType.SELECT_LIMIT, 
				"X_SWIFI_MGR_NO", "LAT", "LNT",
				totalItemCount);
		
		ResultSet rs = db.read(pstmt);
		MinPQ<GCS> mpq = new MinPQ<>(totalItemCount);
		GCS.setPivot(Double.parseDouble(queryLat), Double.parseDouble(queryLnt));
		if (rs != null) {
			try {
				while (rs.next()) {
					String id = rs.getString("X_SWIFI_MGR_NO");
					Double latitude = rs.getDouble("LAT");
					Double longitude = rs.getDouble("LNT");
					
					mpq.insert(new GCS(id, latitude, longitude));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		db.close(rs);
		
		int delCount = 20;
		String[] filterValues = new String[delCount];
		Double[] distances = new Double[delCount];
		
		int i = 0;
		while(!mpq.isEmpty() && delCount > 0) {
			GCS min = mpq.delMin();
			distances[i] = min.calcDistance();
			filterValues[i] = min.getId();
			
			i++;
			delCount--;
		}
		
		db.close(pstmt);
		
		String pxSql = "CREATE TABLE PROXIMITY ("
				+ "	X_SWIFI_MGR_NO      VARCHAR(255)	NOT NULL,"
				+ "	DISTANCE 			DOUBLE			NULL"
				+ ")"
				+ ";";
		Statement stmt = null;
		try {
			stmt = dbConnection.createStatement();
			stmt.execute(pxSql);
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
		
		QueryBuilder pxSpec = new QueryBuilder("PROXIMITY",
				"X_SWIFI_MGR_NO",
				"DISTANCE"
				);
		
		pstmt = db.compile(dbConnection, pxSpec, QueryType.INSERT);
		
		for (int pxRow = 0; pxRow < filterValues.length; pxRow++) {
			pxSpec.setQueryField("X_SWIFI_MGR_NO", filterValues[pxRow]);
			pxSpec.setQueryField("DISTANCE", distances[pxRow]);
			
			db.pxLoad(pstmt, pxSpec);
		}
		db.close(pstmt);
		
		QueryBuilder.OrderClause orderClause = spwSpec.makeOrderClause("DISTANCE");
		QueryBuilder.TableClause otherTable = spwSpec.makeTableClause("PROXIMITY");
		pstmt = db.compile(dbConnection, spwSpec, QueryType.SELECT_NATURAL_JOIN,
				otherTable,
				"X_SWIFI_MGR_NO", "X_SWIFI_WRDOFC", "X_SWIFI_MAIN_NM",
				"X_SWIFI_ADRES1", "X_SWIFI_ADRES2", "X_SWIFI_INSTL_FLOOR",
				"X_SWIFI_INSTL_TY", "X_SWIFI_INSTL_MBY", "X_SWIFI_SVC_SE",
				"X_SWIFI_CMCWR", "X_SWIFI_CNSTC_YEAR", "X_SWIFI_INOUT_DOOR",
				"X_SWIFI_REMARS3", "LAT", "LNT", "WORK_DTTM", "DISTANCE",
				orderClause
				);
		rs = db.read(pstmt);
		
		request.setAttribute("result", rs);
		request.getRequestDispatcher("/WEB-INF/jsp/view/wifi-proximity-table.jsp")
		.forward(request, response);
		
		db.close(pstmt);
		
		pxSql = "DROP TABLE PROXIMITY;";
		try {
			stmt = dbConnection.createStatement();
			stmt.execute(pxSql);
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

	private void makeSearchHistory(HttpServletRequest request, String queryLat, String queryLnt) {
		HttpSession session = request.getSession();
		String tableName = (String) session.getAttribute("tableName");
		if (tableName == null) {
			tableName = "_generated_" + RandomKeyGenerator.generateRandomKey();
		}
		session.setAttribute("tableName", tableName);
						
		initSearchHistoryDB(db, dbConnection, tableName);
		
		QueryBuilder shSpec = new QueryBuilder(
				tableName,
				"latitude",
				"longitude",
				"access_date"
				);
		
		PreparedStatement pstmt = null;
		pstmt = db.compile(dbConnection, shSpec, QueryType.INSERT);
		
		shSpec.setQueryField("latitude", Double.parseDouble(queryLat));
		shSpec.setQueryField("longitude", Double.parseDouble(queryLnt));
		shSpec.setQueryField("access_date", Timestamp.from(Instant.now()));
		
		db.shLoad(pstmt, shSpec);
		db.close(pstmt);
		
	}
	
	private void initSearchHistoryDB(DBAgent db, Connection dbConnection, String tableName) {
		String shSql = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
				+ "	id          INT      NOT NULL AUTO_INCREMENT,"
				+ "	latitude    DOUBLE   NULL,"
				+ "	longitude   DOUBLE   NULL,"
				+ "	access_date TIMESTAMP NULL,"
				+ "	PRIMARY KEY (id)"
				+ ")";
		Statement stmt = null;
		try {
			stmt = dbConnection.createStatement();
			stmt.execute(shSql);
			//System.out.println(String.format("Search history table (%s) has been generated", tableName));
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

	private void serviceSearchHistory(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String tableName = (String) request.getSession().getAttribute("tableName");
		if (tableName != null) {
			QueryBuilder shSpec = new QueryBuilder(
					tableName,
					"id",
					"latitude",
					"longitude",
					"access_date"
					);
			
			PreparedStatement pstmt = null;
			pstmt = db.compile(dbConnection, shSpec, QueryType.SELECT_LIMIT, 
					"id",
					"latitude",
					"longitude",
					"access_date",
					20
					);
			
			ResultSet rs = db.read(pstmt);
			request.setAttribute("result", rs);
			request.getRequestDispatcher("/WEB-INF/jsp/view/wifi-search-history.jsp")
			.forward(request, response);
			
			db.close(pstmt);
		} else {
			request.getRequestDispatcher("/WEB-INF/jsp/view/wifi-search-history.jsp")
			.forward(request, response);
		}
	}
}
		
