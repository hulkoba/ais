package org.beuthhochschule.geo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import de.leuchtetgruen.CollectionUtils;
import de.leuchtetgruen.F;

/**
 * Instances of this class contain an unordered list of POIs (as opposed to POIList where POIs
 * are in order). The can be either built from a JSON-File or a GPX-file.
 * 
 *  
 * @author walz
 *
 */
public class POIDatabase implements Iterable<POI> {
	
	/**
	 * This builder class will build a POIDatabase from a JSON file. This file needs to contain an array
	 * of objects, which need to have a "lat" and "lon" property and can have a "description" property. Here's an example:
	 * 
	 * [
	 * 	{
	 * 	 lat : 52.544267,
	 *   lon : 13.353002,
	 *   description : "Beuth HS"
	 *  },
	 * 	{
	 *   lat : 52.500676, 
	 *   lon : 13.359214,
	 *  }
	 * ]
	 * 
	 * @author walz
	 *
	 */
	public static class JSONBuilder {
		private InputStream in; 
		
		public JSONBuilder(InputStream in) {
			this.in = in;
		}
		
		public JSONBuilder(String filename, Context ctx) throws IOException {
			in = ctx.getAssets().open(filename);
		}
		
		public POIDatabase build() {
			HashSet<POI> pois = new HashSet<POI>();
			try {
                BufferedReader bRead = new BufferedReader(new InputStreamReader(in));
				StringBuilder sbJSON = new StringBuilder();
				String line;
				while ((line = bRead.readLine()) != null) {
				    sbJSON.append(line);
				}	
				
				JSONArray jsArr = new JSONArray(sbJSON.toString());
				
				for (int i=0; i < jsArr.length(); i++) {
					JSONObject jsCoordinate = jsArr.getJSONObject(i);
					LatLng ll = new LatLng(jsCoordinate.getDouble("lat"), jsCoordinate.getDouble("lon"));
					POI poi = new POI(ll, jsCoordinate.optString("description"));
					pois.add(poi);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return new POIDatabase(pois);
		}
		
	}
	

	/**
	 * This builder class will build a POIDatabase from a GPX file. See http://www.topografix.com/gpx.asp
	 * for the specs of the GPX file format. 
	 * Waypoints in the GPX file will be interpreted as POIs and thus be included in the database. 
	 * 
	 * @author walz
	 *
	 */
	public static class GPXBuilder {
		private InputStream in;
		
		public GPXBuilder(InputStream in) {
			this.in = in;
		}
		
		public GPXBuilder(String filename, Context ctx) throws IOException {
			in = ctx.getAssets().open(filename);
		}
		
		public POIDatabase build() {
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
			
			ArrayList<POI> pois = new ArrayList<POI>();
			NodeList lsGeoPoints = doc.getElementsByTagName("wpt");
			for (int i=0; i < lsGeoPoints.getLength(); i++) {
				Node wpt = lsGeoPoints.item(i);
				double lat = Double.parseDouble(wpt.getAttributes().getNamedItem("lat").getNodeValue());
				double lng = Double.parseDouble(wpt.getAttributes().getNamedItem("lon").getNodeValue());
				
				String description = "";
				for (int j=0; j < wpt.getChildNodes().getLength(); j++) {
					Node n = wpt.getChildNodes().item(j);
					if (n.getNodeName().equals("name")) description = n.getTextContent();
					//else if (n.getNodeName().equals("desc")) description = n.getTextContent();
					else if (n.getNodeName().equals("description")) description = n.getTextContent();
					
					if (!description.isEmpty()) break;
				}
				
				LatLng ll = new LatLng(lat, lng);
				
				pois.add(new POI(ll, description));
			}
			
			return new POIDatabase(new HashSet<POI>(pois));
		}
	}
	
	private HashSet<POI> pois;

	public POIDatabase(HashSet<POI> pois) {
		super();
		this.pois = pois;
	}
	
	public POIDatabase() {
		this.pois = new HashSet<POI>();
	}

	public void addPoi(POI poi) {
		pois.add(poi);
	}
	
	public void add(POIList pois) {
		for (POI poi : pois) {
			addPoi(poi);
		}
	}
	
	public void add(POIDatabase pois) {
		for (POI poi : pois) {
			addPoi(poi);
		}
	}
	
	public void setTypeForAll(int type) {
		for (POI poi : pois) {
			poi.setType(type);
		}
	}

	
	public boolean isEmpty() {
		return pois.isEmpty();
	}
	
	@Override
	public String toString() {
		return new CollectionUtils<POI>(pois).join("\n");
	}
	
	@Override
	public Iterator<POI> iterator() {
		return pois.iterator();
	}
	
	public POIDatabase poisInBounds(final LatLngBounds bounds) {
		List<POI> filtered = F.filter(pois, new F.Decider<POI>() {

			@Override
			public boolean decide(POI poi) {
				return bounds.contains(poi.getPosition());
			}
			
		});
		return new POIDatabase(new HashSet<POI>(filtered));
	}
	
	/**
	 * This will return a list of all POIs from the database that are within the
	 * range of the given LatLng. The returned list will be ordered by distance to the
	 * given LatLng.
	 * 
	 * @param point
	 * @param maxDistance
	 * @return
	 */
	public List<POI> poisCloseToLatLng(final LatLng point, final float maxDistance) {
		List<POI> filtered = F.filter(pois, new F.Decider<POI>() {

			@Override
			public boolean decide(POI poi) {
				return (SphericalUtil.computeDistanceBetween(point, poi.getPosition()) <= maxDistance);
			}
		});
		Collections.sort(filtered, new Comparator<POI>() {

			@Override
			public int compare(POI lhs, POI rhs) {
				double distLhs = SphericalUtil.computeDistanceBetween(point, lhs.getPosition());
				double distRhs = SphericalUtil.computeDistanceBetween(point, rhs.getPosition());
				return F.Utils.doubleCompare(distLhs, distRhs);
			}
		});
		return filtered;
	}

	
	
}
