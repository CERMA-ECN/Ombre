package fr.ecn.ombre.android;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;

import fr.ecn.common.core.geometry.Point;
import fr.ecn.common.core.imageinfos.Face;
import fr.ecn.ombre.android.utils.ImageLoader;
import fr.ecn.ombre.core.model.ImageInfos;

public class FacesSimpleController {
	
	protected Bitmap bitmap;
	
	// We explicitly need a LinkedList here because we need the capacity to
	// remove the last element of the list
	// In fact what we need is only something that implements the Deque and the
	// List interfaces
	protected LinkedList<Face> faces = new LinkedList<Face>();
	
	protected Face currentFace = null;
	protected Point currentPoint = null;
	
	protected int mode = MODE_IDLE;
	
	public static final int MODE_IDLE = 0;
	public static final int MODE_EDIT = 1;
	public static final int MODE_CREATE = 2;

	public FacesSimpleController(ImageInfos imageInfos) {
		this.bitmap = ImageLoader.loadResized(imageInfos.getPath(), 600);
	}
	
	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @return true if the controller isn't in face creation or edition mode
	 */
	public boolean isIdle() {
		return this.mode == MODE_IDLE;
	}
	
	/**
	 * @return true if the controller is in face creation mode
	 */
	public boolean isCreate() {
		return this.mode == MODE_CREATE;
	}
	
	/**
	 * @return true if the controller is in face edition mode
	 */
	public boolean isEdit() {
		return this.mode == MODE_EDIT;
	}
	
	/**
	 * Start creation mode
	 */
	public void startFace() {
		this.mode = MODE_CREATE;
		this.currentFace = new Face();
	}
	
	/**
	 * Start edition mode
	 */
	public void editLastFace() {
		this.mode = MODE_EDIT;
		this.currentFace = this.faces.getLast();
	}
	
	/**
	 * Cancel edition or creation of the current face
	 */
	public void cancelFace() {
		this.mode = MODE_IDLE;
		this.currentFace = null;
		this.currentPoint = null;
	}
	
	/**
	 * End the current Face
	 */
	public void endFace() {
		//If we are in creation mode, add the face to the list of faces
		if (this.isCreate()) {
			this.faces.add(this.currentFace);
		}
		
		this.mode = MODE_IDLE;
		this.currentFace = null;
		this.currentPoint = null;
	}

	/**
	 * Add a point to the current face
	 * 
	 * @param x
	 * @param y
	 */
	public void addPoint(float x, float y) {
		this.currentFace.getPoints().add(new Point(x, y));
		
		//Auto-end face if it as 4 points
		if (this.currentFace.getPoints().size() == 4) {
			this.endFace();
		}
	}
	
	/**
	 * Select a point on the current face based on x and y coordinates 
	 * 
	 * @param x
	 * @param y
	 */
	public void selectPoint(float x, float y) {
		for (Point point : this.currentFace.getPoints()) {
			double delta = 25;
			if (point.getX() < x + delta && point.getX() > x - delta
					&& point.getY() < y + delta && point.getY() > y - delta) {
				this.currentPoint = point;
			}
		}
	}
	
	/**
	 * Deselect the current point
	 */
	public void deselectPoint() {
		this.currentPoint = null;
	}
	
	/**
	 * Move the current point to the give coordinates
	 * 
	 * @param x
	 * @param y
	 */
	public void movePoint(float x, float y) {
		if (this.currentPoint == null) {
			return;
		}
		
		this.currentPoint.setX(x);
		this.currentPoint.setY(y);
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
	public List<Face> getFaces() {
		return faces;
	}

	/**
	 * @return the face
	 */
	public Face getCurrentFace() {
		return currentFace;
	}

}
