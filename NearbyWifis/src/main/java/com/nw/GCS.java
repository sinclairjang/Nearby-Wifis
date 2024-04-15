package com.nw;
// The numbers are in decimal degrees format 
// and range from -90 to 90 for latitude and -180 to 180 for longitude
public class GCS implements Comparable<GCS> {

	private final String id;
	private final double latitude;
    private final double longitude;
    
    public GCS(String id, double latitude, double longitude) {
        this.id = id;
    	this.latitude = latitude; 
    	this.longitude = longitude;
    }

    public String getId() { return id; }

	public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public int compareTo(GCS o) {
        Double d1 = this.calcDistance();
        Double d2 = o.calcDistance();
        return d1.compareTo(d2);
    }
    
    private static Double pivotLatitude;
    private static Double pivotLongitude;
    public	static void setPivot(double pivotLatitude, double pivotLongitude) {
    	GCS.pivotLatitude = pivotLatitude;
    	GCS.pivotLongitude = pivotLongitude;
    }
    
    public double calcDistance() {
    	if (pivotLatitude == null || pivotLongitude == null) {
    		throw new RuntimeException("No pivot latitude and/or longitude to calculate distance with!\n "
    				+ "[Suggestion] see 'setPivot' method");	
    	}
    	
    	// Author: Kshirsagar (source: geeksforgeeks| article: program-distance-two-points-earth)
    	// The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
    	double lon1 = Math.toRadians(this.longitude);
        double lon2 = Math.toRadians(GCS.pivotLongitude);
        double lat1 = Math.toRadians(this.latitude);
        double lat2 = Math.toRadians(GCS.pivotLatitude);
 
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

    @Override
    public String toString() {
        return "(latitude: " + latitude + " " + "longitude: " + longitude + ")";
    }
    
}
