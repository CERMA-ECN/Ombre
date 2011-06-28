package fr.ecn.ombre;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import fr.ecn.ombre.model.ImageInfos;
import fr.ecn.ombre.utils.ExifReader;

public class OmbreActivity extends Activity {
	private static final int ACTIVITY_LOAD = 0;
	private static final int ACTIVITY_INFOS = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        
        Button loadButton = (Button) findViewById(R.id.load_image);
        loadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), ACTIVITY_LOAD);
            }
        });
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ACTIVITY_LOAD:
			if (resultCode == Activity.RESULT_OK) {
				//Finding the image absolute file path
				Cursor cursor = this.getContentResolver().query(data.getData(), null, null, null, null);
				cursor.moveToFirst();
				int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
				String absoluteFilePath = cursor.getString(idx);
				
				ImageInfos imageInfos = new ImageInfos(absoluteFilePath);
				
				//Read data from the exif
				ExifReader.readExif(imageInfos);
				
				Log.i("Ombre", imageInfos.toString());
				
				Intent i = new Intent(this, ImageInfosActivity.class);
				i.putExtra("ImageInfos", imageInfos);
				this.startActivityForResult(i, ACTIVITY_INFOS);
			}
			break;
		case ACTIVITY_INFOS:
			if (resultCode == Activity.RESULT_OK) {
				Bundle extras = data.getExtras();
				ImageInfos imageInfos = (ImageInfos) extras.getSerializable("ImageInfos");

				Log.i("Ombre", imageInfos.toString());
			}
			break;
		}
	}
}	