/**
 * 
 */
package fr.ecn.ombre;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;

import fr.ecn.ombre.model.ImageInfos;

/**
 * Activity used to compute and select segments groups and vanishing points
 * 
 * @author jerome
 * 
 */
public class VanishingPointsActivity extends Activity {

	private static final int MENU_RECOMPUTE   = Menu.FIRST;
	
	protected ImageInfos imageInfos;

	protected VanishingPointsController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		final ImageInfos imageInfos = (ImageInfos) extras
				.getSerializable("ImageInfos");
		
		this.imageInfos = imageInfos;

		this.controller = (VanishingPointsController) this
				.getLastNonConfigurationInstance();
		
		if (this.controller == null) {
			this.setContentView(R.layout.computing);
			
			new Thread(new Runnable() {
				public void run() {
					controller = new VanishingPointsController(imageInfos);
					
					runOnUiThread(new Runnable() {
						public void run() {
							setUp();
						}
					});
				}
			}).start();
		} else {
			setUp();
		}
	}
	
	/**
	 * set up views based on controller infos
	 */
	protected void setUp() {
		this.setContentView(R.layout.vanishing_points);
		
		ImageView imageView = (ImageView) this.findViewById(R.id.image);
		
		Drawable[] drawables = {new BitmapDrawable(this.controller.getBitmap()), new VanishingPointsDrawable(this.controller)};
		imageView.setImageDrawable(new LayerDrawable(drawables));
		
		this.createSelectList();
		
		Button valid = (Button) this.findViewById(R.id.valid);
		valid.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				imageInfos.setVanishingPoints(controller.getSelectedPoints());
				
				Intent i = new Intent();
				i.putExtra("ImageInfos", imageInfos);
				setResult(RESULT_OK, i);
				finish();
			}
		});
	}
	
	/**
	 * create the list of CheckBox for group selection
	 */
	protected void createSelectList() {
		LinearLayout selectLayout = (LinearLayout) this.findViewById(R.id.selectLayout);
		
		int nbGroup = this.controller.getGroups().length;
		
		selectLayout.removeAllViews();
		for (int i=0; i<nbGroup; i++) {
			final int id = i;
			
			CheckBox box = new CheckBox(this);
			box.setText("Group " + i);
			box.setTextColor(VanishingPointsDrawable.colorMap[i]);
			box.setChecked(true);
			
			box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					controller.setGroupSelected(id, isChecked);
					findViewById(R.id.image).invalidate();
				}
			});
			
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
		menu.add(0, MENU_RECOMPUTE, 0, "Recompute groups");
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