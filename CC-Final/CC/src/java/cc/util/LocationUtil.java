/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cc.util;

public class LocationUtil {
    public static final char UNIT_KM = 'K';
	public static final char UNIT_MILES = 'M';
	public static final char UNIT_NAUTICAL_MILES = 'N';
	
	/*
	 * unit=> M-Miles, K-Kilometers, N-Nautical Miles
	 */
	public static double getDistance(double lat1, double lon1, double lat2, double lon2, char unit) {
                  if(lat1 == 0 || lon1 == 0 || lat2 == 0 || lon2==0) return 0.0;
		  double theta = lon1 - lon2;
		  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		  dist = Math.acos(dist);
		  dist = rad2deg(dist);
		  dist = dist * 60 * 1.1515;
		  if (unit == 'K') {
		    dist = dist * 1.609344;
		  } else if (unit == 'N') {
		    dist = dist * 0.8684;
		    }
		  return (dist);
		}

		/*::  This function converts decimal degrees to radians             :*/
		
		private static double deg2rad(double deg) {
		  return (deg * Math.PI / 180.0);
		}
		
		
		/*::  This function converts radians to decimal degrees             :*/
		private static double rad2deg(double rad) {
		  return (rad * 180 / Math.PI);
		}
}
