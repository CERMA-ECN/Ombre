/**
 * 
 */
package fr.ecn.ombre;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import fr.ecn.ombre.activities.result.ResultActivity;
import fr.ecn.ombre.core.model.ImageInfos;

/**
 * @author Jérôme Vasseur
 * 
 */
public class DateActivity extends Activity {

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
		
		final DateActivity activity = this;

		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
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
}
