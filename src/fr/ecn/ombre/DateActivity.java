/**
 * 
 */
package fr.ecn.ombre;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

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

		Bundle extras = getIntent().getExtras();
		final ImageInfos imageInfos = (ImageInfos) extras
				.getSerializable("ImageInfos");

		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//TODO
				
				Intent i = new Intent();
				i.putExtra("ImageInfos", imageInfos);
				setResult(RESULT_OK, i);
				finish();
			}
		});
	}

	protected void alertBox(String message) {
		new AlertDialog.Builder(this).setTitle("Error").setMessage(message)
				.setNeutralButton("Close", null).show();
	}
}
