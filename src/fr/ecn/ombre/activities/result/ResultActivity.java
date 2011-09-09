package fr.ecn.ombre.activities.result;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import fr.ecn.ombre.R;
import fr.ecn.ombre.core.model.ImageInfos;
import fr.ecn.ombre.core.shadows.ShadowDrawingException;

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

			final Context context = this;
			
			new Thread(new Runnable() {
				public void run() {
					try {
						createController();
						
						runOnUiThread(new Runnable() {
							public void run() {
								setUp();
							}
						});
					} catch (final ShadowDrawingException e) {
						runOnUiThread(new Runnable() {
							public void run() {
								new AlertDialog.Builder(context)
									.setTitle("Error")
									.setMessage(e.getMessage())
									.setNeutralButton("Close", new OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											finish();
										}
									})
									.show();
							}
						});
					}
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
	
	protected void createController() throws ShadowDrawingException {
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
