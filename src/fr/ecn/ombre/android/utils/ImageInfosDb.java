/**
 * 
 */
package fr.ecn.ombre.android.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import fr.ecn.ombre.core.model.ImageInfos;

/**
 * @author jerome
 *
 */
public class ImageInfosDb extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "ImageInfos";
	private static final int DATABASE_VERSION = 1;

	public ImageInfosDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE infos (_id integer primary key autoincrement, path TEXT UNIQUE, orientation REAL);");
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
	
	public void loadInfos(ImageInfos imageInfos) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(
				"infos", new String[] { "orientation" }, "path = ?", new String[] {imageInfos.getPath()},
				null, null, null);
		
		if (cursor != null && cursor.moveToFirst()) { 
			imageInfos.setOrientation(cursor.getDouble(0));
		}
		
		db.close();
	}
	
	public void saveInfos(ImageInfos imageInfos) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put("path", imageInfos.getPath());
		cv.put("orientation", imageInfos.getOrientation());
		db.replace("infos", null, cv);
		
		db.close();
	}

}
