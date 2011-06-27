package fr.ecn.ombre;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.GpsDescriptor;
import com.drew.metadata.exif.GpsDirectory;

import fr.ecn.ombre.model.ImageInfos;

public class OmbreActivity extends Activity {
	private static final int ACTIVITY_LOAD = 0;
	
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
				}
				break;
		}
	}
}	