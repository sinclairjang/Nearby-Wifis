package com.test;

import java.io.IOException;

import com.api.gov.OpenAPIService;
import com.api.gov.PublicWifiInfoTable;
import com.google.gson.Gson;

public class GsonTest {

	public static void main(String[] args) {
		String data = "";
		
		try {
			data = OpenAPIService.fetchData(1, 1000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Gson gson = new Gson();
		
		PublicWifiInfoTable wifiDB = gson.fromJson(data, PublicWifiInfoTable.class);
		System.out.println(wifiDB);
	}
}
