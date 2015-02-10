package org.beuthhochschule.geo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import de.leuchtetgruen.F;

/**
 * A track (synonym route, way, ...) is an ordered
 * list of LatLng objects.
 * 
 * It can be shown as a polyline on a GoogleMap or
 * used to do certain calculations.
 * 
 * @author walz
 *
 */
public class Track implements Iterable<LatLng> {
	
	
	/**
	 * This builder class builds a Track from a GPX file. It
	 * takes all its trackpoints (trkpt) and puts them into the
	 * list that forms the track.
	 * 
	 * See http://www.topografix.com/gpx.asp for the specs of the GPX file format.
	 *  
	 * @author walz
	 *
	 */
	public static class GpxBuilder {
		
		private InputStream in;
		
		public GpxBuilder(InputStream in) {
			this.in = in;
		}
		
		public GpxBuilder(String filename, Context ctx) throws IOException {
			in = ctx.getAssets().open(filename);
		}
		
		public Track build() {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			Document doc = null;
			try {
				dBuilder = dbFactory.newDocumentBuilder();
				doc = dBuilder.parse(in);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (doc==null) return null;

			doc.getDocumentElement().normalize();
			
			ArrayList<LatLng> points = new ArrayList<LatLng>();
			NodeList lsGeoPoints = doc.getElementsByTagName("trkpt");
			for (int i=0; i < lsGeoPoints.getLength(); i++) {
				Node trkpt = lsGeoPoints.item(i);
				double lat = Double.parseDouble(trkpt.getAttributes().getNamedItem("lat").getNodeValue());
				double lng = Double.parseDouble(trkpt.getAttributes().getNamedItem("lon").getNodeValue());
				points.add(new LatLng(lat, lng));
			}
			
			return new Track(points);
		}
		
		
	}
	
	private static final float MAX_DISTANCE_TO_TRACK = 8f;	
	
	private List<LatLng> points;
	
	public Track() {
		this.points = new ArrayList<LatLng>();
	}
	
	public Track(List<LatLng> points) {
		this.points = points;
	}
	
	public void append(LatLng pt) {
		points.add(pt);
	}
	
	public void prepend(LatLng ll) {
		points.add(0, ll);
	}
	
	public int size() {
		return points.size();
	}


	/**
	 * Returns the bounding rect of the track.
	 * 
	 * @return
	 */
	public LatLngBounds bounds() {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (LatLng ll : points) {
			builder.include(ll);
		}
		return builder.build();
	}

	/**
	 * Transforms the track into a list of locations. The locations will each have
	 * a bearing that contains the heading to the next point. This can be used
	 * for Google StreetView and for rotating a Map according to the pathway of the
	 * route.
	 * 
	 * @return
	 */
	public List<Location> toLocationsWithBearingToNextPoint() {
		ArrayList<Location> ret = new ArrayList<Location>();
		
		for (int i=0; i < points.size(); i++) {
			boolean hasNext = ((points.size() - 1) > (i + 1));
			
			LatLng cur = points.get(i);
			
			Location l = new Location(Integer.toString((int) Math.random() * Integer.MAX_VALUE)); // location with any name
			l.setLatitude(cur.latitude);
			l.setLongitude(cur.longitude);
			
			if (hasNext) {
				LatLng next = points.get(i + 1);
				double bearing = SphericalUtil.computeHeading(cur,  next);
				l.setBearing((float) bearing);
			}
			ret.add(l);
		}
		return ret;
	}
	
	/**
	 * 
	 * This will convert a list of POIs to Locations on this track with bearings/headings to the
	 * POIs
	 * 
	 * @param pois
	 * @param minDistanceToPoi
	 * @return
	 */
	public List<Location> convertPoisToLocationsWithBearingToPoi(List<LatLng> pois, final double minDistanceToPoi) {
		return F.map(pois, new F.Mapper<LatLng, Location>() {

			@Override
			public Location map(LatLng o) {
				
				return latLngToLocationOnTrackWithBearingToLatLng(o, minDistanceToPoi);
			}
		});
	}
	
	/**
	 * Finds all POIs from the given database that are close to this track. The distance to this
	 * track is defined by the MAX_DISTANCE_TO_TRACK constant.
	 * 
	 * @param db
	 * @return
	 */
	public List<POI> poisAlongThisTrack(POIDatabase db) {
		ArrayList<POI> ret = new ArrayList<POI>();

		POIDatabase subset = db.poisInBounds(bounds());

		for (LatLng latLng : toTrackWithRegularDistances(10)) {
			List<POI> closePoints= subset.poisCloseToLatLng(latLng, MAX_DISTANCE_TO_TRACK);
			if (!closePoints.isEmpty()) {
				ret.add(closePoints.get(0));
			}

		}
		return ret;
	}
	
	/**
	 * Converts a given LatLng to a location. The location is on this track and has a bearing / heading to
	 * the given LatLng. This can be used to get the location w/ bearing of a traffic light on this track.
	 * 
	 * @param ll
	 * @param minDistanceToPoi
	 * @return
	 */
	public Location latLngToLocationOnTrackWithBearingToLatLng(final LatLng ll, double minDistanceToPoi) {
		Location l = new Location(Integer.toString((int) Math.random() * Integer.MAX_VALUE));
		
		LatLng closest = F.min(points, new F.Comparator<LatLng>() {

			@Override
			public int compare(LatLng lhs, LatLng rhs) {
				double distLhs = SphericalUtil.computeDistanceBetween(ll, lhs);
				double distRhs = SphericalUtil.computeDistanceBetween(ll, rhs);
				return F.Utils.doubleCompare(distLhs, distRhs);
			}
		});
		
	
		LatLng locationBeforeClosest = findLocationBeforeLocationWithMinimumDistance(closest, minDistanceToPoi);
		if (locationBeforeClosest == null) {
			l.setLatitude(closest.latitude);
			l.setLongitude(closest.longitude);
			return l;
		}
		
		LatLng povLocation = GeoUtils.latLngAlongLineWithMinimumDistanceToTarget(locationBeforeClosest, closest, minDistanceToPoi);
		
		float bearing = (float) SphericalUtil.computeHeading(povLocation, closest);
		if (bearing < 0) {
			bearing = 360 + bearing;
		}
		l.setBearing(bearing);
		l.setLatitude(povLocation.latitude);
		l.setLongitude(povLocation.longitude);
		return l;
	}
	


	@Override
	public Iterator<LatLng> iterator() {
		return points.iterator();
	}
	

	/**
	 * Returns a segment of this track from the given from LatLng
	 * to the given to LatLng
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public Track trackFromTo(LatLng from, LatLng to) {
		LatLng fromOnRoute = getClosestPointOnTrack(from);
		LatLng toOnRoute = getClosestPointOnTrack(to);
		
		final int idxFrom = points.indexOf(fromOnRoute);
		final int idxTo = points.indexOf(toOnRoute);
		if ((idxFrom < 0) || (idxTo < 0)) return new Track();
		if (idxTo < idxFrom) return new Track();
		
		
		Track p = new Track(F.filter(points, new F.Decider<LatLng>() {
			public boolean decide(LatLng o) {
				int i = points.indexOf(o);
				return ((i >= idxFrom) && (i <= idxTo));
			}
		}));
		p.prepend(from);
		if (p.size()==0) p.append(fromOnRoute);
		return p;
	}
	
	/**
	 * Returns the LatLng on this track that is closest to the targetPoint.
	 * This method checks all points in the list of LatLngs that form this track
	 * for their distance to the given target point and return the one with
	 * the minimum distance. It does not return points that are not part of the list.
	 * 
	 * @param targetPoint
	 * @return
	 */
	public LatLng getClosestPointOnTrack(final LatLng targetPoint) {
		
		return F.min(points, new F.Comparator<LatLng>() {
			public int compare(LatLng lhs, LatLng rhs) {
				double d1	= SphericalUtil.computeDistanceBetween(targetPoint, lhs);
				double d2	= SphericalUtil.computeDistanceBetween(targetPoint, rhs);
				return F.Utils.doubleCompare(d1, d2);
			}
		});		
	}
	
	/**
	 * This method returns the length of the track in meters
	 * 
	 * @return
	 */
	public double length() {
		return SphericalUtil.computeLength(points);
	}
	
	public boolean hasPoints() {
		return !points.isEmpty(); 
	}
	
	public List<LatLng> getPoints() {
		return points;
	}
	
	public void setPoints(List<LatLng> points) {
		this.points = points;
	}
	
	/**
	 * This methods returns all turns on this track as POIs. 
	 * Turns have angles of at least 30 degrees. The description
	 * of the POI will indicate the direction of the turn (↰ or ↱) 
	 * 
	 * @return
	 */
	public List<POI> findTurns() {
		ArrayList<POI> ret = new ArrayList<POI>();
		double lastBearing = 0;
		double lastOrigBearing = 0;
		
		for (int i=0; i < (points.size() - 2); i++) {
			LatLng curPt = points.get(i);
			LatLng nxtPt = points.get(i + 1);
			
			int j=2;
			while (GeoUtils.latLngsDescribeSamePosition(curPt, nxtPt)) {
				if ((i + j) >= (points.size())) break;
				nxtPt = points.get(i + j);
				j++;
			}
			
			double bearing = SphericalUtil.computeHeading(curPt, nxtPt);
			double origBearing = bearing;
			if (bearing < 0) {
				bearing = 360 + bearing;
			}
			
			
			
			if ((Math.abs(lastBearing - bearing)) > 30) {
				LatLng routePt = GeoUtils.latLngAlongLineWithDistanceToStart(curPt, nxtPt, 10);
				
				String turnDescr =  (lastOrigBearing < origBearing) ? "↰" : "↱";
				String description = turnDescr;

				
				
				POI poi = new POI(curPt, description);
				poi.setType(POI.TYPE_TURN);
				ret.add(poi);
				
			}
			lastBearing = bearing;
			lastOrigBearing = origBearing;
		}
		return ret;
	}
	
	
	/**
	 * This method converts the track into a track that describes the same way / route. The returned
	 * track will have regular distances (like always 50m) between the LatLngs that form the track.
	 * 
	 * @param desiredDistanceBetweenSteps
	 * @return
	 */
	public Track toTrackWithRegularDistances(int desiredDistanceBetweenSteps) {
		ArrayList<LatLng> returnList = new ArrayList<LatLng>();
		for (int i=0; i < points.size() - 1; i++) {
			LatLng curPt = points.get(i);
			LatLng lastPt = returnList.isEmpty() ? null : returnList.get(returnList.size() - 1);
			
			//TODO calculate distance between curPt And nextPt
			if (lastPt != null) {
				while ((i < (points.size() - 2)) && 
					   (SphericalUtil.computeDistanceBetween(lastPt, curPt) < desiredDistanceBetweenSteps) &&
					   (SphericalUtil.computeDistanceBetween(lastPt, points.get(i+1)) <= desiredDistanceBetweenSteps)
					   ) {
					i++;
					curPt = points.get(i);
				}	
			}
			
			LatLng nextPt = points.get(i + 1);
			
			LatLng insertPt = curPt;
			while (!insertPt.equals(nextPt)) {
				returnList.add(insertPt);
				insertPt = GeoUtils.latLngAlongLineWithDistanceToStart(insertPt, nextPt, desiredDistanceBetweenSteps);
			}

		}
		return new Track(returnList);
	}
	
	public LatLng first() {
		if (points.size() > 0) return points.get(0);
		else throw new NoSuchElementException();
	}
	
	public LatLng last() {
		if (points.size() > 0) return points.get(points.size() - 1);
		else throw new NoSuchElementException();
	}
	
	/**
	 * This method will calculate the distance between two points along this track.
	 * To be specific it uses the points that are closest on the track to those points
	 * and will calculate the distance between those along the track.
	 * 
	 * @param ll1
	 * @param ll2
	 * @return
	 */
	public double calculateDistanceAlongTrack(LatLng ll1, LatLng ll2) {
		LatLng closestFrom = getClosestPointOnTrack(ll1);
		LatLng closestTo = getClosestPointOnTrack(ll2);
		
		double distToClosestFrom = SphericalUtil.computeDistanceBetween(ll1, closestFrom);
		double distToClosestTo = SphericalUtil.computeDistanceBetween(ll2, closestTo);
		
		LatLng lastPt = closestFrom;
		double distBetween = distToClosestFrom;
		for (int i=points.indexOf(closestFrom); i < points.indexOf(closestTo); i++) {
			LatLng curPt = points.get(i);
			distBetween += SphericalUtil.computeDistanceBetween(lastPt, curPt);
			lastPt = curPt;
		}
		distBetween += distToClosestTo;
		
		return distBetween;
	}
	
	public boolean containsCoordinate(LatLng coordinate) {
		LatLng lastCoordinate = null;
		for (LatLng curCoordinate : points) {
			if (lastCoordinate != null) {
				if (GeoUtils.lineContainsCoordinate(lastCoordinate, curCoordinate, coordinate) ||
					curCoordinate.equals(coordinate)) 
				{
					return true;
				}
			}
			lastCoordinate = curCoordinate;
		}
		return false;
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public String toString() {
		return String.format("%d Points, %.1f m length", points.size(), length());
	}

	// -- internal methods --
	private LatLng findLocationBeforeLocationWithMinimumDistance(LatLng ll, double minDistance) {
		int idxLocation = points.indexOf(ll);
		
		if (idxLocation == -1) return null;
		
		
		for (int idxFound=idxLocation; idxFound > 0; idxFound--) {
			LatLng llFound = points.get(idxFound);
			if (SphericalUtil.computeDistanceBetween(llFound, ll) >= minDistance) return llFound;
		}
		return null;
	}
	
}
