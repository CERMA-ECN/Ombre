package fr.ecn.ombre.activities.facessimple;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import fr.ecn.ombre.model.Face;
import fr.ecn.ombre.model.Point;

public class FacesSimpleDrawable extends Drawable {

	protected FaceSimpleController controller;

	/**
	 * @param controller
	 */
	public FacesSimpleDrawable(FaceSimpleController controller) {
		super();
		this.controller = controller;
	}

	@Override
	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);

		for (Face face : this.controller.getFaces()) {
			this.drawFace(face, canvas, paint);
		}

		paint.setColor(Color.RED);
		
		List<Point> points = this.controller.getPoints();

		if (points != null) {
			if (points.size() == 1) {
				canvas.drawPoint((int) points.get(0).getX(), (int) points
						.get(0).getY(), paint);
			} else {
				for (int i = 1; i < points.size(); i++) {
					canvas.drawLine((int) points.get(i - 1).getX(),
							(int) points.get(i - 1).getY(), (int) points.get(i)
									.getX(), (int) points.get(i).getY(), paint);
				}
			}
		}
	}

	public void drawFace(Face face, Canvas canvas, Paint paint) {
		Point[] points = face.getPoints();

		for (int i = 0; i < 4; i++) {
			Point p1 = points[i];
			Point p2 = points[(i + 1) % 4];

			canvas.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(),
					(int) p2.getY(), paint);
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

}
