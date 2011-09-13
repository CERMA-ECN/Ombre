package fr.ecn.ombre.android;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import fr.ecn.ombre.core.model.ImageInfos;
import fr.ecn.ombre.core.shadows.ShadowDrawingException;

public class ResultActivity extends Activity {
	
	protected Future<ResultController> futureController;
	
	protected ResultController controller;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.futureController = (Future<ResultController>) this.getLastNonConfigurationInstance();
		
		//We create a Callable that will create the ResultController
		if (this.futureController == null) {
			Bundle extras = getIntent().getExtras();
			ImageInfos imageInfos = (ImageInfos) extras.getSerializable("ImageInfos");
			Calendar time = (Calendar) extras.getSerializable("Time");
			boolean shadowsOnWalls = extras.getBoolean("ShadowsOnWalls");
			boolean expandToStreet = extras.getBoolean("ExpandToStreet");
			
			ExecutorService executor = Executors.newSingleThreadExecutor();
			this.futureController = executor.submit(new ResultController.ResultCallable(imageInfos, time, shadowsOnWalls, expandToStreet));
		}
		
		this.setContentView(R.layout.computing);

		final Context context = this;
		
		new Thread(new Runnable() {
			public void run() {
				try {
					controller = futureController.get();
					
					runOnUiThread(new Runnable() {
						public void run() {
							setUp();
						}
					});
				} catch (InterruptedException e) {
					Log.w("Ombre", e);
				} catch (ExecutionException e) {
					if (e.getCause() instanceof ShadowDrawingException) {
						final ShadowDrawingException sde = (ShadowDrawingException) e.getCause();
						
						runOnUiThread(new Runnable() {
							public void run() {
								new AlertDialog.Builder(context)
									.setTitle("Erreur")
									.setMessage(sde.getMessage())
									.setNeutralButton("Fermer", new OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											finish();
										}
									})
									.show();
							}
						});
					} else {
						Log.w("Ombre", e);
					}
				}
			}
		}).start();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		return this.futureController;
	}
	
	protected void setUp() {
		this.setContentView(R.layout.select_faces);
		
		ImageView imageView = (ImageView) findViewById(R.id.image);
		
		Drawable[] drawables = {new BitmapDrawable(this.controller.getBitmap()), new ResultDrawable(this.controller)}; 
		imageView.setImageDrawable(new LayerDrawable(drawables));
	}

}
