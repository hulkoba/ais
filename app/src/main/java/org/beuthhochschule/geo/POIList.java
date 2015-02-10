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

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import de.leuchtetgruen.CollectionUtils;
import de.leuchtetgruen.F;

/**
 * A POIList is an ordered List of POIs. It can be built from a GPX file
 * 
 * @author walz
 *
 */
public class POIList implements Iterable<POI> {

	/**
	 * This builder class will build a POIList from a GPX file. See http://www.topografix.com/gpx.asp
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

		public POIList build() {
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
			
			return new POIList(pois);
		}
		
	}
	
	private List<POI> pois;

	public POIList(List<POI> pois) {
		super();
		this.pois = pois;
	}
	
	public POIList(POIList poiList) {
		this.pois = F.map(poiList, new F.Mapper<POI, POI>() {

			@Override
			public POI map(POI poi) {
				return new POI(poi);
			}
		});
	}
	
	public POIList() {
		pois = new ArrayList<POI>();
	}

	public void addPoi(POI poi) {
		pois.add(poi);
	}
	
	public void removeFirst() {
		if (pois.isEmpty()) throw new NoSuchElementException("The list of POIs is empty. There is no first POI to be removed.");
		pois.remove(0);
	}
	
	public void removeLast() {
		if (pois.isEmpty()) throw new NoSuchElementException("The list of POIs is empty. There is no last POI to be removed.");
		pois.remove(pois.size() - 1);
	}
	
	public void removeUntil(POI poi) {
		int idx = pois.indexOf(poi) - 1;
		
		if (idx < 0) throw new NoSuchElementException("There is no element like the POI.");
		
		for (int i=0; i < idx; i++) {
			removeFirst();
		}
	}
	
	public POI first() {
		if (pois.isEmpty()) return null;
		return pois.get(0);
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
	
	public void append(POI poi) {
		pois.add(poi);
	}
	
	public void prepend(POI poi) {
		pois.add(0, poi);
	}
	
}
