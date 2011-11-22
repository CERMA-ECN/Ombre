package fr.ecn.ombre.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import fr.ecn.common.android.DialogHelper;
import fr.ecn.ombre.core.model.ImageInfos;
import fr.ecn.ombre.core.shadows.ShadowDrawingException;

public class ResultActivity extends Activity {
	
	private static final int MENU_HOME = Menu.FIRST;
	private static final int MENU_SAVE = Menu.FIRST + 1;
	private static final int MENU_EVOLUTION = Menu.FIRST + 2;
	
	private static final int DIALOG_SELECT_TIME_STEP = 0;
	
	protected ResultController controller;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.controller = (ResultController) this.getLastNonConfigurationInstance();
		
		//We create a new Controller
		if (this.controller == null) {
			Bundle extras = getIntent().getExtras();
			ImageInfos imageInfos = (ImageInfos) extras.getSerializable("ImageInfos");
			Calendar time = (Calendar) extras.getSerializable("Time");
			boolean shadowsOnWalls = extras.getBoolean("ShadowsOnWalls");
			boolean expandToStreet = extras.getBoolean("ExpandToStreet");
			
			this.controller = new ResultController(imageInfos, time, shadowsOnWalls, expandToStreet);
		}
		
		this.setContentView(R.layout.result);
		
		ImageButton back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				controller.stepBackward();
				waitComputation();
			}
		});
		
		ImageButton forward = (ImageButton) findViewById(R.id.forward);
		forward.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				controller.stepForward();
				waitComputation();
			}
		});
		
		if (!this.controller.isEvolution()) {
			back.setVisibility(View.INVISIBLE);
			forward.setVisibility(View.INVISIBLE);
		}
		
		this.waitComputation();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		return this.controller;
	}
	
	public void waitComputation() {
		final TextView textView = (TextView) findViewById(R.id.date);
		textView.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm").format(controller.getTime().getTime()));
		
		final LinearLayout placeholder = (LinearLayout) this.findViewById(R.id.placeholder);
		
		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		
		placeholder.removeAllViews();
		placeholder.addView(progressBar);
		
		new Thread(new Runnable() {
			public void run() {
				try {
					controller.waitComputation();
					
					runOnUiThread(new Runnable() {
						public void run() {
							ImageView imageView = new ImageView(ResultActivity.this);
							Drawable[] drawables = {new BitmapDrawable(controller.getBitmap()), new ResultDrawable(controller)}; 
							imageView.setImageDrawable(new LayerDrawable(drawables));
							imageView.setLayoutParams(new ViewGroup.LayoutParams(
									ViewGroup.LayoutParams.FILL_PARENT,
									ViewGroup.LayoutParams.FILL_PARENT));
							
							placeholder.removeAllViews();
							placeholder.addView(imageView);
						}
					});
				} catch (final ExecutionException e) {
					if (e.getCause() instanceof ShadowDrawingException) {
						runOnUiThread(new Runnable() {
							public void run() {
								TextView textView = new TextView(ResultActivity.this);
								textView.setText(e.getCause().getMessage());
								textView.setLayoutParams(new ViewGroup.LayoutParams(
										ViewGroup.LayoutParams.WRAP_CONTENT,
										ViewGroup.LayoutParams.WRAP_CONTENT));
							
								placeholder.removeAllViews();
								placeholder.addView(textView);
							}
						});
					} else {
						Log.w("Ombre", e);
						
						runOnUiThread(new Runnable() {
							public void run() {
								DialogHelper.errorDialog(ResultActivity.this, "Une erreur c'est produite lors du calcul du résultat");
							}
						});
					}
				}
			}
		}).start();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_EVOLUTION, 0, R.string.time_evolution);
		menu.add(0, MENU_HOME, 0, R.string.return_home);
		menu.add(0, MENU_SAVE, 0, R.string.save_image);
		return result;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_EVOLUTION:
			this.showDialog(DIALOG_SELECT_TIME_STEP);
			return true;
		case MENU_HOME:
			Intent i = new Intent(this, OmbreActivity.class);
			this.startActivity(i);
			return true;
		case MENU_SAVE:
			Bitmap bitmap = Bitmap.createBitmap(this.controller.getBitmap());
			Canvas canvas = new Canvas(bitmap);
			
			//Draw the shadows
			new ResultDrawable(this.controller).draw(canvas);
			
			//Draw the time of the simulation
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setTextSize(24);
			canvas.drawText(new SimpleDateFormat("MM/dd/yyyy HH:mm").format(this.controller.getTime().getTime()), 5, 29, paint);
			
			MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Result", "Result");
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_SELECT_TIME_STEP:
			final Dialog dialog = new Dialog(this);

			dialog.setContentView(R.layout.select_time_step);
			dialog.setTitle("Sélectionnez le pas de temps voulu");
			
			final Spinner spinner = (Spinner) dialog.findViewById(R.id.time_step_type);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
					R.array.time_step_type, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			
			final TextView textView = (TextView) dialog.findViewById(R.id.time_step_value);

			final Button button = (Button) dialog.findViewById(R.id.ok);
			button.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View arg0) {
					int time_step_value = Integer.parseInt(textView.getText().toString());
					int time_step_field;
					
					String time_step_type = (String) spinner.getSelectedItem();
					if ("Heures".equals(time_step_type)) {
						time_step_field = Calendar.HOUR;
					} else if ("Jours".equals(time_step_type)) {
						time_step_field = Calendar.DAY_OF_MONTH;
					} else if ("Mois".equals(time_step_type)) {
						time_step_field = Calendar.MONTH;
					} else {
						return;
					}
					
					dialog.dismiss();
					
					controller.setTimeStep(time_step_field, time_step_value);
					
					findViewById(R.id.back).setVisibility(View.VISIBLE);
					findViewById(R.id.forward).setVisibility(View.VISIBLE);
				}
			});
			
			return dialog;
		}
		
		return super.onCreateDialog(id);
	}

}
