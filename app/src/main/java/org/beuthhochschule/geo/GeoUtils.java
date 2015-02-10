package org.beuthhochschule.geo;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

/**
 * This class contains multiple static public methods mainly used to convert one object 
 * into another or transform business objects in this package.
 * 
 * 
 * @author walz
 *
 */
public class GeoUtils {
	
	private static final double THRESHOLD = 0.0001;
	
	/**
	 * Transforms a Location object into a LatLng object.
	 * @param l
	 * @return
	 */
	public static LatLng locationToLatLng(Location l) {
		return new LatLng(l.getLatitude(), l.getLongitude());
	}

	/**
	 * Given a line from ll1 to ll2 this method will find a point along the line that is not more than distanceToTarget
	 * meters away from the endpoint of this line.  
	 * 
	 * @param ll1
	 * @param ll2
	 * @param distanceToTarget
	 * @return
	 */
	public static LatLng latLngAlongLineWithMinimumDistanceToTarget(LatLng ll1, LatLng ll2, double distanceToTarget) {

		if (SphericalUtil.computeDistanceBetween(ll1, ll2) < distanceToTarget) return ll1;

		double x1 = ll1.latitude;
		double x2 = ll2.latitude;
		double y1 = ll1.longitude;
		double y2 = ll2.longitude;

		double dx = x2 - x1;
		double dy = y2 - y1;



		// Approximation
		double nSteps = 50;
		for (int i=1 ; i < nSteps; i++) {
			double xT = x1 + (i/nSteps) * dx;
			double yT = y1 + (i/nSteps) * dy;
			LatLng lT = new LatLng(xT, yT);
			double dist = SphericalUtil.computeDistanceBetween(lT, ll2);
			if (dist < distanceToTarget) return lT;
		}


		return ll1;
	}
	
	/**
	 * Given a line from ll1 to ll2 this method will find a point along the line that is not more than distanceToTarget
	 * meters away from the startpoint of this line.  
	 * 
	 * @param ll1
	 * @param ll2
	 * @param distanceToStart
	 * @return
	 */
	public static LatLng latLngAlongLineWithDistanceToStart(LatLng ll1, LatLng ll2, double distanceToStart) {
		if (SphericalUtil.computeDistanceBetween(ll1, ll2) < distanceToStart) return ll2;

		double x1 = ll1.latitude;
		double x2 = ll2.latitude;
		double y1 = ll1.longitude;
		double y2 = ll2.longitude;

		double dx = x2 - x1;
		double dy = y2 - y1;



		// Approximation
		double nSteps = 50;
		for (int i=1 ; i < nSteps; i++) {
			double xT = x1 + (i/nSteps) * dx;
			double yT = y1 + (i/nSteps) * dy;
			LatLng lT = new LatLng(xT, yT);
			double dist = SphericalUtil.computeDistanceBetween(lT, ll1);
			//TODO this one might be difficult
			if (dist > distanceToStart) return lT;
		}


		return ll2;
	}
	
	/**
	 * Converts a LatLng object into a Location object. The resulting Location object
	 * will not contain an altitude or a bearing etc. It will only contain a latitude
	 * and a longitude
	 * 
	 * @param ll
	 * @return
	 */
	public static Location latLngToLocation(LatLng ll) {
		Location l = new Location(Integer.toString((int) Math.random() * Integer.MAX_VALUE)); // location with any name
		l.setLatitude(ll.latitude);
		l.setLongitude(ll.longitude);
		return l;
	}
	
	/**
	 * Non-referential .equals for two LatLng objects
	 * 
	 * @param ll1
	 * @param ll2
	 * @return
	 */
	public static boolean latLngsDescribeSamePosition(LatLng ll1, LatLng ll2) {
		return ((ll1.latitude == ll2.latitude) &&
				(ll1.longitude == ll2.longitude));
	}
	

	/**
	 * Determines wether or not a coordinate is part of a line by building
	 * a very small bounding rect around it and checking wether that contains
	 * the coordinate.
	 * 
	 * @param p1
	 * @param p2
	 * @param coordinate
	 * @return
	 */
	public static boolean lineContainsCoordinate(LatLng p1, LatLng p2, LatLng coordinate) {
		
		double minLat = ((p1.latitude < p2.latitude) ? p1.latitude : p2.latitude) - THRESHOLD;
		double minLon = ((p1.longitude < p2.longitude) ? p1.longitude: p2.longitude) - THRESHOLD;
		double maxLat = ((p1.latitude > p2.latitude) ? p1.latitude : p2.latitude) + THRESHOLD;
		double maxLon = ((p1.longitude > p2.longitude) ? p1.longitude: p2.longitude) + THRESHOLD;
		
		// If minimum bounding rectangle around current stroke of segment contains coordinate - return true
		if ((coordinate.latitude >= minLat) &&
			(coordinate.latitude <= maxLat) &&
			(coordinate.longitude >= minLon) &&
			(coordinate.longitude <= maxLon))
			return true;
		else return false;
	}
}
