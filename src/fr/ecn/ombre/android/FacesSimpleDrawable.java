package fr.ecn.ombre.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import fr.ecn.common.core.imageinfos.Face;
import fr.ecn.ombre.android.utils.Drawing;

public class FacesSimpleDrawable extends Drawable {

	protected FacesSimpleController controller;

	/**
	 * @param controller
	 */
	public FacesSimpleDrawable(FacesSimpleController controller) {
		super();
		this.controller = controller;
	}

	@Override
	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setStyle(Paint.Style.STROKE);
		
		Paint currentPaint = new Paint();
		currentPaint.setColor(Color.RED);
		currentPaint.setStyle(Paint.Style.STROKE);

		for (Face face : this.controller.getFaces()) {
			if (face == this.controller.getCurrentFace()) {
				Drawing.drawFace(face, canvas, currentPaint);
			} else {
				Drawing.drawFace(face, canvas, paint);
			}
		}
		
		if (this.controller.isCreate()) {
			Drawing.drawFace(this.controller.getCurrentFace(), canvas, currentPaint);
		}
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAlpha(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColorFilter(ColorFilter arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#getIntrinsicHeight()
	 */
	@Override
	public int getIntrinsicHeight() {
		return this.controller.getBitmap().getHeight();
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#getIntrinsicWidth()
	 */
	@Override
	public int getIntrinsicWidth() {
		// TODO Auto-generated method stub
		return this.controller.getBitmap().getWidth();
	}

}
