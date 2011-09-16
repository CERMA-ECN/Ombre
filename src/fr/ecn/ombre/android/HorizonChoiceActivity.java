/**
 * 
 */
package fr.ecn.ombre.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import fr.ecn.ombre.core.model.ImageInfos;

/**
 * @author jerome
 *
 */
public class HorizonChoiceActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		final ImageInfos imageInfos = (ImageInfos) extras.getSerializable("ImageInfos");
		
		this.setContentView(R.layout.horizon_choice);
		
		Button auto = (Button) this.findViewById(R.id.auto);
		Button manual = (Button) this.findViewById(R.id.manual);
		
		auto.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				Intent i = new Intent(HorizonChoiceActivity.this, VanishingPointsActivity.class);
				i.putExtra("ImageInfos", imageInfos);
				
				startActivity(i);
			}
		});
		
		manual.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				Intent i = new Intent(HorizonChoiceActivity.this, HorizonActivity.class);
				i.putExtra("ImageInfos", imageInfos);
				
				startActivity(i);
			}
		});
	}

}
