package fr.ecn.ombre.android.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import fr.ecn.ombre.core.model.Face;
import fr.ecn.ombre.core.model.Point;

public class Drawing {
	public static void drawFace(Face face, Canvas canvas, Paint paint) {
		Point[] points = face.getPoints();
		
		Path path = new Path();
		path.moveTo((float) points[points.length-1].getX(), (float) points[points.length-1].getY());

		for (int i = 0; i < points.length; i++) {
			Point p = points[i];
			
			path.lineTo((float) p.getX(), (float) p.getY());
		}
		
		canvas.drawPath(path, paint);
	}
}
