package org.beuthhochschule.geo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

/**
 * This class wraps the Geocoder class into an asynctask for use in an android
 * environment. You will provide it with an address string and will receive
 * an address object containing a geocoordinate in the callback listener.
 * 
 * @author walz
 *
 */
public class AsyncGeocodingTask extends AsyncTask<String, Integer, Address> {

	private GeocodingListener geocodingListener;
	private Geocoder geocoder;
	
	/**
	 * This interface is used to inform the caller of the async task about the
	 * result of the request. It can either fail with noPositionFound or respond
	 * with the address found. The address object will  then contain the 
	 * geocoordinate of the requested address.
	 * 
	 * @author walz
	 *
	 */
	public interface GeocodingListener {
		public void receivedPosition(Address address);
		public void noPositionFound();
	}
	
	public AsyncGeocodingTask(Context ctx, String description, GeocodingListener listener) {
		geocoder = new Geocoder(ctx);
		
		this.geocodingListener = listener;
		
		this.execute(description);
	}
	
	@Override
	protected Address doInBackground(String... params) {
		String description = params[0];
		
		List<Address> addresses = new ArrayList<Address>();
		try {
			addresses = geocoder.getFromLocationName(description, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ((addresses == null) || addresses.isEmpty()) ? null : addresses.get(0);
	}
	
	@Override
	protected void onPostExecute(Address result) {
		if (result == null) {
			if (Debug.ENABLED) geocodingListener.receivedPosition(Debug.getRandomDebuggingAddress());
			else geocodingListener.noPositionFound();
			return;
		}
		geocodingListener.receivedPosition(result);
		
		super.onPostExecute(result);
	}

}
