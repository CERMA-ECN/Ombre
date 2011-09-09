/**
 * 
 */
package fr.ecn.ombre.android.image;

import android.graphics.Bitmap;

import fr.ecn.ombre.core.image.Image;

/**
 * A class that wrap a Bitmap object in an Image type
 * 
 * @author jerome
 *
 */
public class BitmapImage extends Image {
	
	public BitmapImage(Bitmap bitmap) {
		super(bitmap.getWidth(), bitmap.getHeight());
	}
	
}
