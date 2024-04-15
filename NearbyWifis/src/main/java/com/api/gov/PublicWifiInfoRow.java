package com.api.gov;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PublicWifiInfoRow {
	private String X_SWIFI_MGR_NO; 		// referenceCode(1)
	private String X_SWIFI_WRDOFC; 		// district(2)
	private String X_SWIFI_MAIN_NM;		// networkID(3)
	private String X_SWIFI_ADRES1; 		// address1(4)
	private String X_SWIFI_ADRES2; 		// address2(5)
	private String X_SWIFI_INSTL_FLOOR;	// setUpLocation(6)
	private String X_SWIFI_INSTL_TY;	// setUpSite(7)
	private String X_SWIFI_INSTL_MBY;	// setUpOrganization(8)
	private String X_SWIFI_SVC_SE;		// serviceType(9)
	private String X_SWIFI_CMCWR;		// networkType(10)
	private String X_SWIFI_CNSTC_YEAR;	// setUpYear(11)
	private String X_SWIFI_INOUT_DOOR;	// isIndoor(12)
	private String X_SWIFI_REMARS3;		// accessEnvironment(13)
	private String LAT;					// latitude(14)
	private String LNT;					// longitude(15)
	private String WORK_DTTM;			// setUpDate(16)
}
