package fr.ecn.ombre.android;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import fr.ecn.common.android.Dialog;
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
	@SuppressWarnings("unchecked")
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
				} catch (final ExecutionException e) {
					if (e.getCause() instanceof ShadowDrawingException) {
						runOnUiThread(new Runnable() {
							public void run() {
								Dialog.errorDialog(ResultActivity.this, e.getCause().getMessage());
							}
						});
					} else {
						Log.w("Ombre", e);
						
						runOnUiThread(new Runnable() {
							public void run() {
								Dialog.errorDialog(ResultActivity.this, "Une erreur c'est produite lors du calcul du résultat");
							}
						});
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
		this.setContentView(R.layout.image);
		
		ImageView imageView = (ImageView) findViewById(R.id.image);
		
		Drawable[] drawables = {new BitmapDrawable(this.controller.getBitmap()), new ResultDrawable(this.controller)}; 
		imageView.setImageDrawable(new LayerDrawable(drawables));
	}

}
