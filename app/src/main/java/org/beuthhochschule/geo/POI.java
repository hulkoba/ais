package org.beuthhochschule.geo;


import java.util.List;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

/**
 * A POI describes a LatLng with a certain description and optionally a certain type.
 * When adding additional types keep in mind that the type is a bitfield so new constants
 * have to be powers of two as well. (32, 64, ...)
 * 
 * @author walz
 *
 */
public class POI {

	public static final int TYPE_TRAFFIC_SIGNAL = 1;
	public static final int TYPE_BUS_STOP = 2;
	public static final int TYPE_TURN = 4;
	public static final int TYPE_START = 8;
	public static final int TYPE_TARGET = 16;
	
	private LatLng position;
	private String description;
	private int type;
	
	
	
	public POI(LatLng position, String description) {
		this.position = position;
		this.description = description;
	}
	
	public POI(LatLng position) {
		this.position = position;
		this.description = "";
	}

	public POI(POI poi) {
		this.description = poi.getDescription();
		this.position = poi.getPosition();
	}

	public LatLng getPosition() {
		return position;
	}

	public String getDescription() {
		return description;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType(int type) {
		return this.type;
	}
	
	public boolean isOfType(int type) {
		return ((this.type & type) > 0);
	}
	
	@Override
	public String toString() {
		return description + " @ " + position.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!o.getClass().equals(this.getClass())) return false;
		
		POI p = (POI) o;
		return ((p.getDescription().equals(description)) && (p.getPosition().equals(position)));
	}
	
	public double distanceTo(LatLng to) {
		return SphericalUtil.computeDistanceBetween(position, to);
	}
	
	/**
	 * Returns a type specific distance from which the POI can be best seen. (Used for Google
	 *  StreetView)
	 *  
	 * @return
	 */
	public double getDesiredDistance() {
		double ret = 10;
		if (isOfType(TYPE_TURN)) ret = 50;
		if (isOfType(TYPE_TRAFFIC_SIGNAL)) ret = 50;
		if (isOfType(TYPE_BUS_STOP)) ret = 20;
		
		return ret;
	}
	
	/**
	 * Returns a type specific icon (Used for Google Maps)
	 * @return
	 */
	public BitmapDescriptor getIcon() {
		int rid = R.drawable.questionmark;
		if (isOfType(TYPE_BUS_STOP)) rid = R.drawable.busstop;
		else if (isOfType(TYPE_TRAFFIC_SIGNAL)) rid = R.drawable.trafficlight;
		else if (isOfType(TYPE_TURN)) rid = R.drawable.direction_split;
		else if (isOfType(TYPE_START)) rid = R.drawable.start;
		else if (isOfType(TYPE_TARGET)) rid = R.drawable.finish;
		return BitmapDescriptorFactory.fromResource(rid);
	}
	

}
