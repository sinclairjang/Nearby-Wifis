package com.test;

public class HaverSineTest {

	public static double distance(double lat1, double lat2, double lon1,double lon2) {
		// The math module contains a function
		// named toRadians which converts from
		// degrees to radians.
		lon1 = Math.toRadians(lon1);
		lon2 = Math.toRadians(lon2);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
		
		// Haversine formula 
		double dlon = lon2 - lon1; 
		double dlat = lat2 - lat1;
		double a = Math.pow(Math.sin(dlat / 2), 2)
		        + Math.cos(lat1) * Math.cos(lat2)
		        * Math.pow(Math.sin(dlon / 2),2);
		    
		double c = 2 * Math.asin(Math.sqrt(a));
		
		// Radius of earth in kilometers. Use 3956 
		// for miles
		double r = 6371;
		
		// calculate the result
		return(c * r);
	} 

	// driver code
	public static void main(String[] args) {
		double lat1 =  0.32055555555556;
		double lat2 = 0.;
		double lon1 = -178.564712111111;
		double lon2 = -178;
		System.out.println(distance(lat2, lat1, lon2, lon1) + " K.M");
		
		double _lat1 =  -1.31861111111111;
		double _lat2 = 0.;
		double _lon1 = 1.38271211111;
		double _lon2 = -178;
		System.out.println(distance(_lat2, _lat1, _lon2, _lon1) + " K.M");
	}

}
