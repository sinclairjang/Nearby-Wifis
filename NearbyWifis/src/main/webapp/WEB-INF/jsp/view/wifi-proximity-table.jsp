<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.text.DecimalFormat" %>
<!DOCTYPE>
<html>
	<head>
		<title>Nearby Wifis</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resource/style/wifi-proxy-table.css" />
	</head>
	
	<body>
		<h1>${pageContext.request.contextPath} <img id=logo src="${pageContext.request.contextPath}/resource/image/wifi_icon.png" width="42" height="42"></h1>
			<label id="lat-label" for="lat">위도:</label>
			<p id="lat" class="lat"> <%= request.getParameter("lat") %> | </p>
			<label for="lnt">경도:</label>
			<p id="lnt" class="lnt"> <%= request.getParameter("lnt") %> </p>
			<a class="prev" href="${pageContext.request.contextPath}/wifi-info">돌아가기</a>
		<table>
			<tr>
				<th>거리(Km)</th>
				<th>관리번호</th>
				<th>자치구</th>
				<th>와이파이명</th>
				<th>도로명주소</th>
				<th>상세주소</th>
				<th>설치위치(층)</th>
				<th>설치유형</th>
				<th>설치기관</th>
				<th>서비스구분</th>
				<th>망종류</th>
				<th>설치년도</th>
				<th>실내외구분</th>
				<th>WIFI 접속환경</th>
				<th>위도</th>
				<th>경도</th>
				<th>작업일자</th>
			</tr>
			
			<%! 
				public String toggleEven(boolean toggle) {
					if(toggle) return "class='even'";
					else return "";
				}
			%>
			
			<%
				ResultSet rs = (ResultSet) request.getAttribute("result");
				DecimalFormat df = new DecimalFormat("#,###.##");
				
				if (rs != null) {
					try {
						boolean toggle = false;
						while (rs.next()) { 
							String inOrOut = (rs.getBoolean("X_SWIFI_INOUT_DOOR") == true) ? "실내" : "실외";
			%>
							<tr <%= toggleEven(toggle) %>>
								<td><%= df.format(rs.getDouble("DISTANCE")) + "km" %></td>
								<td><%= rs.getString("X_SWIFI_MGR_NO") %></td>
								<td><%= rs.getString("X_SWIFI_WRDOFC") %></td>
								<td><%= rs.getString("X_SWIFI_MAIN_NM") %></td>
								<td><%= rs.getString("X_SWIFI_ADRES1") %></td>
								<td><%= rs.getString("X_SWIFI_ADRES2") %></td>
								<td><%= rs.getString("X_SWIFI_INSTL_FLOOR") %></td>
								<td><%= rs.getString("X_SWIFI_INSTL_TY") %></td>
								<td><%= rs.getString("X_SWIFI_INSTL_MBY") %></td>
								<td><%= rs.getString("X_SWIFI_SVC_SE") %></td>
								<td><%= rs.getString("X_SWIFI_CMCWR") %></td>
								<td><%= rs.getString("X_SWIFI_CNSTC_YEAR") %></td>
								<td><%= inOrOut %></td>
								<td><%= rs.getString("X_SWIFI_REMARS3") %></td>
								<td><%= rs.getDouble("LAT") %></td>
								<td><%= rs.getDouble("LNT") %></td>
								<td><%= rs.getTimestamp("WORK_DTTM") %></td>
			<% 
						toggle = !toggle;
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			%>
		</table>
	</body>
</html>