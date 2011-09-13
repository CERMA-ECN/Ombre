package fr.ecn.ombre.android;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;

import fr.ecn.ombre.android.utils.ImageLoader;
import fr.ecn.ombre.core.model.Face;
import fr.ecn.ombre.core.model.ImageInfos;
import fr.ecn.ombre.core.model.Point;

public class FacesSimpleController {
	
	protected Bitmap bitmap;
	
	protected List<Face> faces = new LinkedList<Face>();
	
	protected List<Point> points = null;

	public FacesSimpleController(ImageInfos imageInfos) {
		this.bitmap = ImageLoader.loadResized(imageInfos.getPath(), 600);
	}
	
	public void startFace() {
		this.points = new ArrayList<Point>(4);
	}

	public void addPoint(float x, float y) {
		this.points.add(new Point(x, y));
		
		if (this.points.size() == 4) {
			this.faces.add(new Face(this.points.get(0), this.points.get(1), this.points.get(2), this.points.get(3)));
			this.points = null;
		}
	}

	/**
	 * @return the bitmap
	 */
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	/**
	 * @return the faces
	 */
	public List<Face> getFaces() {
		return faces;
	}

	/**
	 * @return the points
	 */
	public List<Point> getPoints() {
		return points;
	}

}
