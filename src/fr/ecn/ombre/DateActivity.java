/**
 * 
 */
package fr.ecn.ombre;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import fr.ecn.ombre.activities.result.ResultActivity;
import fr.ecn.ombre.model.ImageInfos;

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
		final Button okButton = (Button) findViewById(R.id.okButton);
		
		timePicker.setIs24HourView(true);

		Bundle extras = getIntent().getExtras();
		final ImageInfos imageInfos = (ImageInfos) extras
				.getSerializable("ImageInfos");
		
		final DateActivity activity = this;

		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Calendar time = new GregorianCalendar();
				time.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
				time.set(Calendar.MONTH, datePicker.getMonth());
				time.set(Calendar.YEAR, datePicker.getYear());
				time.set(Calendar.HOUR, timePicker.getCurrentHour());
				time.set(Calendar.MINUTE, timePicker.getCurrentMinute());
				
				Intent i = new Intent(activity, ResultActivity.class);
				i.putExtra("ImageInfos", imageInfos);
				i.putExtra("Time", time);
				startActivity(i);
			}
		});
	}

	protected void alertBox(String message) {
		new AlertDialog.Builder(this).setTitle("Error").setMessage(message)
				.setNeutralButton("Close", null).show();
	}
}
