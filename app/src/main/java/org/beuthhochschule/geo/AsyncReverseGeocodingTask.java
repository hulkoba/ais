package org.beuthhochschule.geo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

/**
 * 
 * This class wraps the Geocoder class into an asynctask for use in an android
 * environment. You will provide it with a LatLng object and will receive the
 * corresponding address as a result.
 * 
 * @author walz
 *
 */
public class AsyncReverseGeocodingTask extends AsyncTask<LatLng, Integer, Address>{

	private Geocoder geoCoder;
	private ReverseGeocodingListener reverseGeocodingListener;
	
	/**
	 * This interface is used to inform the caller of the async task about the
	 * result of the request. It can either fail with noDescriptionFound or
	 * provide you with the found address.
	 * 
	 * @author walz
	 *
	 */
	public interface ReverseGeocodingListener {
		public void foundAddress(Address address);
		public void noDescriptionFound();
	}
	
	public AsyncReverseGeocodingTask(Context ctx, LatLng ll, ReverseGeocodingListener reverseGeocodingListener) {
		this.geoCoder = new Geocoder(ctx);
		this.reverseGeocodingListener = reverseGeocodingListener;
		this.execute(ll);
	}
	
	@Override
	protected Address doInBackground(LatLng... params) {
		LatLng ll = params[0];
		
		List<Address> addresses = new ArrayList<Address>();
		try {
			addresses = geoCoder.getFromLocation(ll.latitude, ll.longitude, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ((addresses == null) || addresses.isEmpty()) ? null : addresses.get(0);
	}

	@Override
	protected void onPostExecute(Address result) {
		if (result == null) {
			if (Debug.ENABLED) reverseGeocodingListener.foundAddress(Debug.getRandomDebuggingAddress());
			else reverseGeocodingListener.noDescriptionFound();
			return;
		}
		reverseGeocodingListener.foundAddress(result);
		super.onPostExecute(result);
	}
	
	
}
