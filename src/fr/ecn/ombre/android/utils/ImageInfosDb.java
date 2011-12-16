/**
 * 
 */
package fr.ecn.ombre.android.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import fr.ecn.common.core.imageinfos.ImageInfos;

/**
 * @author jerome
 *
 */
public class ImageInfosDb extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "ImageInfos";
	private static final int DATABASE_VERSION = 2;
	private static final String TABLE_NAME = "infos";

	public ImageInfosDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id integer primary key autoincrement, path TEXT UNIQUE, orientation REAL, yHorizon REAL);");
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	
	/**
	 * Load infos about this image from DB
	 * 
	 * @param imageInfos
	 */
	public void loadInfos(ImageInfos imageInfos) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(
				TABLE_NAME, new String[] { "orientation", "yHorizon" }, "path = ?", new String[] {imageInfos.getPath()},
				null, null, null);
		
		if (cursor != null && cursor.moveToFirst()) { 
			if (!cursor.isNull(0)) {
				imageInfos.setOrientation(cursor.getDouble(0));
			}
			if (!cursor.isNull(1)) {
				imageInfos.setYHorizon(cursor.getDouble(1));
			}
		}
		
		db.close();
	}
	
	/**
	 * Save infos avout this image into DB
	 * 
	 * @param imageInfos
	 */
	public void saveInfos(ImageInfos imageInfos) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put("path", imageInfos.getPath());
		cv.put("orientation", imageInfos.getOrientation());
		cv.put("yHorizon", imageInfos.getYHorizon());
		db.replace(TABLE_NAME, null, cv);
		
		db.close();
	}

}
