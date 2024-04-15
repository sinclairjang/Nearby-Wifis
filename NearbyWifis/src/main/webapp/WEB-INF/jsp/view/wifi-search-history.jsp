<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE>
<html>
	<head>
		<title>Nearby Wifis</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resource/style/wifi-proxy-table.css" />
	</head>
		
	<body>
		<h1>${pageContext.request.contextPath} <img id=logo src="${pageContext.request.contextPath}/resource/image/wifi_icon.png" width="42" height="42"></h1>
			<a class="prev" href="${pageContext.request.contextPath}/wifi-info">돌아가기</a>
		<form method="GET" id="my_form"></form>
		<table>
			<tr>
				<th>색인</th>
				<th>위도</th>
				<th>경도</th>
				<th>조회일자</th>
				<th></th>
			</tr>
			
			<%! 
				public String toggleEven(boolean toggle) {
					if(toggle) return "class='even'";
					else return "";
				}
			%>
			
			<%
				ResultSet rs = (ResultSet) request.getAttribute("result");
				DecimalFormat decimalFormat = new DecimalFormat("#,###.#####");
				DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				if (rs != null) {
					try {
						boolean toggle = false;
						while (rs.next()) { 
			%>
							<tr <%= toggleEven(toggle) %>>
								<td><%= rs.getInt("id") %></td>
								<td><%= decimalFormat.format(rs.getDouble("latitude")) %></td>
								<td><%= decimalFormat.format(rs.getDouble("longitude")) %></td>
								<td><%= datetimeFormat.format(rs.getTimestamp("access_date").toLocalDateTime()) %></td>
								<td><img src="/NearbyWifis/resource/image/delete-button.svg" 
								width="20" height="20" alt="삭제" id=<%= rs.getInt("id") %> class="delete">
            					</td>
							</tr>
			<% 		
						toggle = !toggle;
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			%>
		</table>
		<script src="${pageContext.request.contextPath}/resource/js/delete-history.js"></script>
	</body>
</html>