package fr.ecn.ombre.android;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;

import fr.ecn.common.core.geometry.Point;
import fr.ecn.ombre.android.utils.ImageLoader;
import fr.ecn.ombre.core.model.ImageInfos;
import fr.ecn.ombre.core.shadows.ShadowDrawingFace;

public class FacesSimpleController {
	
	protected Bitmap bitmap;
	
	// We explicitly need a LinkedList here because we need the capacity to
	// remove the last element of the list
	// In fact what we need is only something that implements the Deque and the
	// List interfaces
	protected LinkedList<ShadowDrawingFace> faces = new LinkedList<ShadowDrawingFace>();
	
	protected List<Point> points = null;
	
	protected ShadowDrawingFace face = null;
	protected Point point = null;

	public FacesSimpleController(ImageInfos imageInfos) {
		this.bitmap = ImageLoader.loadResized(imageInfos.getPath(), 600);
	}
	
	/**
	 * @return true if the controller isn't in face creation or edition mode
	 */
	public boolean isIdle() {
		return this.points == null && this.face == null;
	}
	
	/**
	 * @return true if the controller is in face creation mode
	 */
	public boolean isCreate() {
		return this.points != null;
	}
	
	/**
	 * @return true if the controller is in face edition mode
	 */
	public boolean isEdit() {
		return this.face != null;
	}
	
	/**
	 * Start creation mode
	 */
	public void startFace() {
		this.points = new ArrayList<Point>(4);
	}

	public void addPoint(float x, float y) {
		this.points.add(new Point(x, y));
		
		if (this.points.size() == 4) {
			this.faces.add(new ShadowDrawingFace(this.points.get(0), this.points.get(1), this.points.get(2), this.points.get(3)));
			this.points = null;
		}
	}
	
	/**
	 * Cancel the current face
	 */
	public void cancelFace() {
		this.points = null;
	}
	
	/**
	 * Start edition mode
	 */
	public void editLastFace() {
		this.face = this.faces.getLast();
	}
	
	/**
	 * End the current Face
	 */
	public void endFace() {
		this.face = null;
		this.point = null;
	}
	
	public void selectPoint(float x, float y) {
		for (Point point : this.face.getPoints()) {
			double delta = 25;
			if (point.getX() < x + delta && point.getX() > x - delta
					&& point.getY() < y + delta && point.getY() > y - delta) {
				this.point = point;
			}
		}
	}
	
	public void deselectPoint() {
		this.point = null;
	}
	
	public void movePoint(float x, float y) {
		if (this.point == null) {
			return;
		}
		
		this.point.setX(x);
		this.point.setY(y);
	}
	
	/**
	 * Remove the last face added
	 */
	public void removeLastFace() {
		this.faces.removeLast();
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
	public List<ShadowDrawingFace> getFaces() {
		return faces;
	}

	/**
	 * @return the face
	 */
	public ShadowDrawingFace getFace() {
		return face;
	}

	/**
	 * @return the points
	 */
	public List<Point> getPoints() {
		return points;
	}

}
