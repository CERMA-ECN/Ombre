/**
 * 
 */
package fr.ecn.ombre;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import fr.ecn.ombre.model.Coordinate;
import fr.ecn.ombre.model.ImageInfos;

/**
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
			if (imageInfos.getLatitude() != null) {
				editLat.setText(imageInfos.getLatitude().getCoordinate());
				latitudeRefSpinner.setSelection(latitudeAdapter.getPosition(imageInfos.getLatitude().getRef()));
			}
	    	
	    	if (imageInfos.getLongitude() != null) {
	    		editLong.setText(imageInfos.getLongitude().getCoordinate());
				longitudeRefSpinner.setSelection(longitudeAdapter.getPosition(imageInfos.getLongitude().getRef()));
	    	}
	    	
	    	if (imageInfos.getOrientation() != null) {
	    		editOrient.setText(imageInfos.getOrientation().toString());
	    	}
	    }
		
		okButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				imageInfos.setLatitude(new Coordinate(editLat.getText().toString(), (String) latitudeRefSpinner.getSelectedItem()));
				imageInfos.setLongitude(new Coordinate(editLong.getText().toString(), (String) longitudeRefSpinner.getSelectedItem()));
				imageInfos.setOrientation(Double.parseDouble(editOrient.getText().toString()));
				
				Intent i = new Intent();
				i.putExtra("ImageInfos", imageInfos);
				setResult(RESULT_OK, i);
				finish();
			}
		});
	}

}
