/**
 * 
 */
package fr.ecn.ombre;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import fr.ecn.ombre.model.ImageInfos;

/**
 * @author jerome
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
		
		Button okButton = (Button) findViewById(R.id.image_infos_ok);
		
		Bundle extras = getIntent().getExtras();
		final ImageInfos imageInfos = (ImageInfos) extras.getSerializable("ImageInfos");
		
	    if (imageInfos != null) {
	    	if (imageInfos.getLatitude() != null) {
	    		editLat.setText(imageInfos.getLatitude().toString());
	    	}
	    	
	    	if (imageInfos.getLongitude() != null) {
	    		editLong.setText(imageInfos.getLongitude().toString());
	    	}
	    	
	    	if (imageInfos.getOrientation() != null) {
	    		editOrient.setText(imageInfos.getOrientation().toString());
	    	}
	    }
		
		okButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				imageInfos.setLatitude(Double.parseDouble(editLat.getText().toString()));
				imageInfos.setLongitude(Double.parseDouble(editLong.getText().toString()));
				imageInfos.setOrientation(Double.parseDouble(editOrient.getText().toString()));
				
				Intent i = new Intent();
				i.putExtra("ImageInfos", imageInfos);
				setResult(RESULT_OK, i);
				finish();
			}
		});
	}

}
