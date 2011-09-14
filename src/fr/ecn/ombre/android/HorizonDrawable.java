/**
 * 
 */
package fr.ecn.ombre.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import fr.ecn.ombre.core.model.ImageInfos;

/**
 * @author jerome
 *
 */
public class HorizonDrawable extends Drawable {
	
	protected Bitmap bitmap;
	protected ImageInfos imageInfos;

	/**
	 * @param bitmap
	 * @param imageInfos
	 */
	public HorizonDrawable(Bitmap bitmap, ImageInfos imageInfos) {
		super();
		this.bitmap = bitmap;
		this.imageInfos = imageInfos;
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(this.bitmap, new Matrix(), null);
		
		if (this.imageInfos.getYHorizon() != null) {
			double yHorizon = this.imageInfos.getYHorizon();
			
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			
			canvas.drawLine(0, (float) yHorizon, this.bitmap.getWidth(), (float) yHorizon, paint );
		}
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#getOpacity()
	 */
	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#setAlpha(int)
	 */
	@Override
	public void setAlpha(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#setColorFilter(android.graphics.ColorFilter)
	 */
	@Override
	public void setColorFilter(ColorFilter arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#getIntrinsicHeight()
	 */
	@Override
	public int getIntrinsicHeight() {
		return this.bitmap.getHeight();
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#getIntrinsicWidth()
	 */
	@Override
	public int getIntrinsicWidth() {
		return this.bitmap.getWidth();
	}

}
