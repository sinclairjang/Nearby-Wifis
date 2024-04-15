package com.api.gov;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;

public class OpenAPIService {
	
	public static String fetchData(int start, int end) throws IOException {
		StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088");
		urlBuilder.append("/" +  URLEncoder.encode("5447434f75676f6d35316569576841", "UTF-8") );
		urlBuilder.append("/" +  URLEncoder.encode("json", "UTF-8") );
		urlBuilder.append("/" + URLEncoder.encode("TbPublicWifiInfo", "UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode(String.format("%d", start), "UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode(String.format("%d", end),"UTF-8"));
		
		
		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/xml");
		//System.out.println("Response code: " + conn.getResponseCode());
		BufferedReader rd;

		if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
				sb.append(line);
		}
		rd.close();
		conn.disconnect();
		return sb.toString();
	}

}
