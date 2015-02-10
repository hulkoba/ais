package org.beuthhochschule.geo;

import java.util.ArrayList;
import java.util.List;

import android.location.Address;

/**
 * This class is used for debugging purposes.
 * 
 * @author walz
 *
 */
public class Debug {
	public static boolean ENABLED = true;
	
	private static List<Address> sampleAddresses;
	
	public static Address getRandomDebuggingAddress() {
		buildAddresses();
		
		int rdIdx = (int) (Math.random() * sampleAddresses.size());
		return sampleAddresses.get(rdIdx);
	}
	
	private static void buildAddresses() {
		if ((sampleAddresses != null) && (!sampleAddresses.isEmpty())) return;
		
		sampleAddresses = new ArrayList<Address>();
		
		Address adr1 = new Address(null);
		adr1.setAddressLine(0, "Luxemburger Str. 10");
		adr1.setAddressLine(1, "13353 Berlin");
		adr1.setLatitude(52.544267);
		adr1.setLongitude(13.353002);
		sampleAddresses.add(adr1);
		
		Address adr2 = new Address(null);
		adr2.setAddressLine(0, "Seestraße 64");
		adr2.setAddressLine(1, "13347 Berlin");
		adr2.setLatitude(52.553924); 
		adr2.setLongitude(13.359456);
		sampleAddresses.add(adr2);
		
		
		Address adr3 = new Address(null);
		adr3.setAddressLine(0, "Kurfürstenstraße 141");
		adr3.setAddressLine(1, "10785 Berlin");
		adr3.setLatitude(52.500676); 
		adr3.setLongitude(13.359214);
		sampleAddresses.add(adr3);
	}
}
