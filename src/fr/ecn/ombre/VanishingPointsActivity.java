/**
 * 
 */
package fr.ecn.ombre;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import fr.ecn.ombre.model.ImageInfos;

/**
 * Activity used to compute and select segments groups and vanishing points
 * 
 * @author jerome
 * 
 */
public class VanishingPointsActivity extends Activity {

	private static final int MENU_RECOMPUTE   = Menu.FIRST;

	protected VanishingPointsController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.vanishing_points);

		Bundle extras = getIntent().getExtras();
		final ImageInfos imageInfos = (ImageInfos) extras
				.getSerializable("ImageInfos");

		VanishingPointsController controller = (VanishingPointsController) this
				.getLastNonConfigurationInstance();
		if (controller == null) {
			controller = new VanishingPointsController(imageInfos);
		}

		this.controller = controller;
		
		this.setUp();
	}
	
	/**
	 * set up views based on controller infos
	 */
	protected void setUp() {
		ImageView imageView = (ImageView) this.findViewById(R.id.image);
		
		Drawable[] drawables = {new BitmapDrawable(this.controller.getBitmap()), new VanishingPointsDrawable(this.controller)};
		imageView.setImageDrawable(new LayerDrawable(drawables));
		
		this.createSelectList();
	}
	
	/**
	 * create the list of CheckBox for group selection
	 */
	protected void createSelectList() {
		LinearLayout selectLayout = (LinearLayout) this.findViewById(R.id.selectLayout);
		
		int nbGroup = this.controller.getGroups().length;
		
		selectLayout.removeAllViews();
		for (int i=0; i<nbGroup; i++) {
			CheckBox box = new CheckBox(this);
			box.setText("Group " + i);
			box.setTextColor(VanishingPointsDrawable.colorMap[i]);
			
			selectLayout.addView(box);
		}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_RECOMPUTE, 0, "Recompute");
		return result;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_RECOMPUTE:
			this.controller.computeGroups();
			this.findViewById(R.id.image).invalidate();
			this.createSelectList();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}