/**
 * 
 */
package fr.ecn.ombre.android;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import fr.ecn.common.core.imageinfos.ImageInfos;

/**
 * @author Jérôme Vasseur
 * 
 */
public class OptionsActivity extends Activity {

	private static final int MENU_SPECIAL_DATE = Menu.FIRST;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.select_date);

		final DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
		final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
		final CheckBox shadowsOnWalls = (CheckBox) findViewById(R.id.shadowsOnWalls);
		final CheckBox expandToStreet = (CheckBox) findViewById(R.id.expandToStreet);
		final Button okButton = (Button) findViewById(R.id.okButton);
		
		final Calendar time = new GregorianCalendar();
		
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				time.set(Calendar.HOUR_OF_DAY, hourOfDay);
				time.set(Calendar.MINUTE, minute);
			}
		});
		
		timePicker.setIs24HourView(true);
		
		Bundle extras = getIntent().getExtras();
		final ImageInfos imageInfos = (ImageInfos) extras
				.getSerializable("ImageInfos");
		
		final OptionsActivity activity = this;

		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Dirty trick to let the timePicker widget update his hour value
				datePicker.requestFocus();
				
				time.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
				time.set(Calendar.MONTH, datePicker.getMonth());
				time.set(Calendar.YEAR, datePicker.getYear());
				
				Intent i = new Intent(activity, ResultActivity.class);
				i.putExtra("ImageInfos", imageInfos);
				i.putExtra("Time", time);
				i.putExtra("ExpandToStreet", expandToStreet.isChecked());
				i.putExtra("ShadowsOnWalls", shadowsOnWalls.isChecked());
				startActivity(i);
			}
		});
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_SPECIAL_DATE, 0, R.string.special_dates);
		return result;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SPECIAL_DATE:
			final CharSequence[] items = {"Solstice d'été", "Solstice d'hiver", "Équinoxe de mars", "Équinoxe de sept."};
			final int[] monthOfYear = {5, 11, 2, 8};
			final int[] dayOfMonth = {21, 21, 20, 22};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Jour");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
			    	datePicker.updateDate(datePicker.getYear(), monthOfYear[item], dayOfMonth[item]);
				}
			});
			builder.create().show();
					
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
