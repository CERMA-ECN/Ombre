package fr.ecn.ombre.image.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageLoader {
	
	/**
	 * Load an image and resize it if its height or width is greater than maxDim
	 * 
	 * @param path
	 * @param maxDim
	 * @return
	 */
	public static Bitmap loadResized(String path, int maxDim) {
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		
		int height = bitmap.getHeight();
		int width  = bitmap.getWidth();
		
		if (height <= maxDim && width <= maxDim) {
			return bitmap;
		} else {
			float scale = Math.min((float)maxDim / height, (float)maxDim / width);
			
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			
			//Recycle bitmap to free memory
			bitmap.recycle();
			
			return resizedBitmap;
		}
	}
	
	/**
	 * Load an image and resize it if its height or width is greater than maxDim
	 * 
	 * @param path
	 * @param maxDim
	 * @return
	 */
	public static Bitmap loadResizedApprox(String path, int maxDim) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, o);

		int height = o.outHeight;
		int width = o.outWidth;

		int scale = 1;

		while (height > maxDim || width > maxDim) {
			scale *= 2;
			height /= 2;
			width /= 2;
		}

		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		o2.inTempStorage = new byte[64 * 1024];
		o2.inPurgeable = true;
		return BitmapFactory.decodeFile(path, o2);
	}
}