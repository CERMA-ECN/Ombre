/**
 * 
 */
package fr.ecn.ombre.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import fr.ecn.ombre.core.model.Coordinate;
import fr.ecn.ombre.core.model.ImageInfos;

/**
 * Activity that request informations from the sensors (GPS Coordinates and
 * azimuth) and wait for them
 * 
 * @author jerome
 * 
 */
public class SensorActivity extends Activity implements LocationListener, SensorEventListener {
	
	protected ImageInfos imageInfos;
	
	protected boolean hasLocation = false;
	protected boolean hasAzimuth = false;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle extras = getIntent().getExtras();
		this.imageInfos = (ImageInfos) extras.getSerializable("ImageInfos");
		
		this.setContentView(R.layout.computing);
		
		LocationManager lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		
		SensorManager sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
		
		super.onCreate(savedInstanceState);
	}
	
	protected void checkInformations() {
		if (this.hasLocation && this.hasAzimuth) {
			Intent i = new Intent(this, ImageInfosActivity.class);
			i.putExtra("ImageInfos", this.imageInfos);
			this.startActivity(i);
		}
	}
	
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
	
	public void onProviderEnabled(String arg0) {}
	
	public void onProviderDisabled(String arg0) {}
	
	public void onLocationChanged(Location location) {
		Log.i("Ombre", "Location");
		
		this.imageInfos.setLatitude(Coordinate.fromDouble(location.getLatitude(), "N"));
		this.imageInfos.setLongitude(Coordinate.fromDouble(location.getLongitude(), "E"));
		
		this.hasLocation = true;
		
		LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		lm.removeUpdates(this);
		
		this.checkInformations();
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {}

	public void onSensorChanged(SensorEvent event) {
		Log.i("Ombre", "Sensor : Azimuth = " + event.values[0]);
		
		this.imageInfos.setOrientation((double) event.values[0]);
		
		this.hasAzimuth = true;
		
		SensorManager sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		sm.unregisterListener(this);
		
		this.checkInformations();
	}

}
