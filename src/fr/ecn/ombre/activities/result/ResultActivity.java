package fr.ecn.ombre.activities.result;

import java.util.Calendar;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import fr.ecn.ombre.R;
import fr.ecn.ombre.model.ImageInfos;

public class ResultActivity extends Activity {
	
	protected ResultController controller;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.controller = (ResultController) this
				.getLastNonConfigurationInstance();
		
		if (this.controller == null) {
			this.setContentView(R.layout.computing);
			
			new Thread(new Runnable() {
				public void run() {
					createController();
					
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
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		return this.controller;
	}
	
	protected void createController() {
		Bundle extras = getIntent().getExtras();
		ImageInfos imageInfos = (ImageInfos) extras.getSerializable("ImageInfos");
		Calendar time = (Calendar) extras.getSerializable("Time");
		boolean shadowsOnWalls = extras.getBoolean("ShadowsOnWalls");
		boolean expandToStreet = extras.getBoolean("ExpandToStreet");
		
		this.controller = new ResultController(imageInfos, time, shadowsOnWalls, expandToStreet);
	}
	
	protected void setUp() {
		this.setContentView(R.layout.select_faces);
		
		ImageView imageView = (ImageView) findViewById(R.id.image);
		
		Drawable[] drawables = {new BitmapDrawable(this.controller.getBitmap()), new ResultDrawable(this.controller)}; 
		imageView.setImageDrawable(new LayerDrawable(drawables));
	}

}
