/**
 * 
 */
package fr.ecn.ombre.activities.faces;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import fr.ecn.ombre.model.Face;
import fr.ecn.ombre.model.Point;

/**
 * @author jerome
 *
 */
public class FacesDrawable extends Drawable {
	
	protected FacesController controller;

	/**
	 * @param cissorController
	 */
	public FacesDrawable(FacesController scissorController) {
		super();
		this.controller = scissorController;
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		
		for (Face face : this.controller.faces) {
			this.drawFace(face, canvas, paint);
		}
		
		paint.setColor(Color.RED);
		
		if (this.controller.currentLine != null) {
			this.controller.currentLine.draw(canvas);
		}
		
		if (this.controller.currentFace != null) {
			this.drawFace(this.controller.currentFace, canvas, paint);
		}
	}
	
	public void drawFace(Face face, Canvas canvas, Paint paint) {
		Point[] points = face.getPoints();
		
		for (int i=0; i<4; i++) {
			Point p1 = points[i];
			Point p2 = points[(i+1)%4];
			
			canvas.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY(), paint);
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
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#setColorFilter(android.graphics.ColorFilter)
	 */
	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub

	}

}
