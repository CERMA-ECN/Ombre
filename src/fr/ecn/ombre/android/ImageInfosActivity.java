/**
 * 
 */
package fr.ecn.ombre.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import fr.ecn.ombre.android.utils.ImageInfosDb;
import fr.ecn.ombre.android.utils.ValidationException;
import fr.ecn.ombre.core.model.Coordinate;
import fr.ecn.ombre.core.model.ImageInfos;

/**
 * An activity to check and edit the informations of the image
 * 
 * @author Jérôme Vasseur
 *
 */
public class ImageInfosActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.image_infos);
		
		final EditText editLat    = (EditText) findViewById(R.id.image_infos_lat);
		final EditText editLong   = (EditText) findViewById(R.id.image_infos_long);
		final EditText editOrient = (EditText) findViewById(R.id.image_infos_orient);
		
		final Spinner latitudeRefSpinner = (Spinner) findViewById(R.id.image_infos_latitude_ref);
		ArrayAdapter<CharSequence> latitudeAdapter = ArrayAdapter.createFromResource(
				this, R.array.latitude_refs, android.R.layout.simple_spinner_item);
		latitudeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		latitudeRefSpinner.setAdapter(latitudeAdapter);
		
		final Spinner longitudeRefSpinner = (Spinner) findViewById(R.id.image_infos_longitude_ref);
		ArrayAdapter<CharSequence> longitudeAdapter = ArrayAdapter.createFromResource(
				this, R.array.longitude_refs, android.R.layout.simple_spinner_item);
		longitudeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		longitudeRefSpinner.setAdapter(longitudeAdapter);
		
		Button okButton = (Button) findViewById(R.id.image_infos_ok);
		
		Bundle extras = getIntent().getExtras();
		final ImageInfos imageInfos = (ImageInfos) extras.getSerializable("ImageInfos");
		
	    if (imageInfos != null) {
			Cursor cursor = new ImageInfosDb(ImageInfosActivity.this).getReadableDatabase().query(
					"infos", new String[] { "orientation" }, "path = ?", new String[] {imageInfos.getPath()},
					null, null, null);
			if (cursor != null && cursor.moveToFirst()) { 
				imageInfos.setOrientation(cursor.getDouble(0));
			}
	    	
			if (imageInfos.getLatitude() != null) {
				editLat.setText(imageInfos.getLatitude().getDMSString());
				latitudeRefSpinner.setSelection(latitudeAdapter.getPosition(imageInfos.getLatitude().getRef()));
			}
	    	
	    	if (imageInfos.getLongitude() != null) {
	    		editLong.setText(imageInfos.getLongitude().getDMSString());
				longitudeRefSpinner.setSelection(longitudeAdapter.getPosition(imageInfos.getLongitude().getRef()));
	    	}
	    	
	    	if (imageInfos.getOrientation() != null) {
	    		editOrient.setText(imageInfos.getOrientation().toString());
	    	}
	    }
		
		final ImageInfosActivity activity = this;
	    
		okButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				try {
					imageInfos.setLatitude(this.validateLatitude(editLat.getText().toString(), (String) latitudeRefSpinner.getSelectedItem()));
					imageInfos.setLongitude(this.validateLongitude(editLong.getText().toString(), (String) longitudeRefSpinner.getSelectedItem()));
					imageInfos.setOrientation(this.validateOrientation(editOrient.getText().toString()));
					
					ContentValues cv = new ContentValues();
					cv.put("path", imageInfos.getPath());
					cv.put("orientation", imageInfos.getOrientation());
					new ImageInfosDb(ImageInfosActivity.this).getWritableDatabase().insert("infos", null, cv);
					
					Intent i = new Intent(activity, HorizonChoiceActivity.class);
					i.putExtra("ImageInfos", imageInfos);
					startActivity(i);
					
				} catch (ValidationException e) {
					alertBox(e.getMessage());
				}
			}
			
			protected Coordinate validateLatitude(String coordinate, String ref) throws ValidationException {
				if ("".equals(coordinate)) {
					throw new ValidationException("Latitude must be set");
				}
				try {
					return Coordinate.fromString(coordinate, ref);
				} catch (IllegalArgumentException e) {
					throw new ValidationException("Invalid latitude", e);
				}
			}
			
			protected Coordinate validateLongitude(String coordinate, String ref) throws ValidationException {
				if ("".equals(coordinate)) {
					throw new ValidationException("Longitude must be set");
				}
				try {
					return Coordinate.fromString(coordinate, ref);
				} catch (IllegalArgumentException e) {
					throw new ValidationException("Invalid longitude", e);
				}
			}
			
			protected Double validateOrientation(String orientationString) throws ValidationException {
				if ("".equals(orientationString)) {
					throw new ValidationException("Orientation must be set");
				}
				
				try {
					return Double.parseDouble(orientationString);
				} catch (NumberFormatException e) {
					throw new ValidationException("Invalid orientation", e);
				}
			}
		});
	}

	protected void alertBox(String message) {
		new AlertDialog.Builder(this).setTitle("Error").setMessage(message).setNeutralButton("Close", null).show(); 
	}
}
