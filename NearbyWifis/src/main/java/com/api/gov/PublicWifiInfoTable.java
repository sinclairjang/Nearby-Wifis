package com.api.gov;

import java.util.List;

import lombok.Getter;

@Getter
public class PublicWifiInfoTable {
	private PublicWifiInfo TbPublicWifiInfo;
	
	public String toString() {
		return TbPublicWifiInfo.getRow().toString();
	}
	
	@Getter
	public class PublicWifiInfo {
		private int list_total_count;
		ResponseStatus RESULT;
		List<PublicWifiInfoRow> row;
		
		@Getter
		private class ResponseStatus {
			private String CODE;
			private String MESSAGE;
		}
	}
}
