/**
 * 
 */
package fr.ecn.ombre;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import fr.ecn.ombre.model.ImageInfos;

/**
 * Activity used to compute and select segments groups and vanishing points
 * 
 * @author jerome
 * 
 */
public class VanishingPointsActivity extends Activity {

	protected VanishingPointsController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.vanishing_points);

		ImageView image = (ImageView) findViewById(R.id.image);

		Bundle extras = getIntent().getExtras();
		final ImageInfos imageInfos = (ImageInfos) extras
				.getSerializable("ImageInfos");

		VanishingPointsController controller = (VanishingPointsController) this
				.getLastNonConfigurationInstance();
		if (controller == null) {
			controller = new VanishingPointsController(imageInfos);
		}

		this.controller = controller;
		controller.setUp(image);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		return this.controller;
	}
}