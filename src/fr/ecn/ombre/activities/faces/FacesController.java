package fr.ecn.ombre.activities.faces;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;

import jjil.android.RgbImageAndroid;
import fr.ecn.ombre.core.model.Face;
import fr.ecn.ombre.core.model.ImageInfos;
import fr.ecn.ombre.core.model.Point;
import fr.ecn.ombre.image.utils.ImageLoader;
import fr.ecn.ombre.scissor.Polygon;
import fr.ecn.ombre.scissor.SCISSOR_STATE;
import fr.ecn.ombre.scissor.Scissor;
import fr.ecn.ombre.scissor.ScissorLine;

public class FacesController {

	protected Bitmap bitmap;

	protected Scissor scissor;
	
	protected List<Face> faces = new LinkedList<Face>();
	
	public static enum State {
		IDLE, DRAWING, VALIDATION
	}
	
	protected State state = State.IDLE;
	
	protected ScissorLine currentLine;
	protected Face currentFace;

	/**
	 * @param imageInfos
	 */
	public FacesController(ImageInfos imageInfos) {
		super();

		this.bitmap = ImageLoader.loadResized(imageInfos.getPath(), 600);

		this.scissor = new Scissor(RgbImageAndroid.toRgbImage(bitmap));
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * Tell this controller to generate a new scissor line.
	 */
	public void startLine() {
		if (this.currentLine != null) {
			if (this.currentLine.getState() != SCISSOR_STATE.HOLD) {
				this.currentLine.endScissor();
			}
			
			Face face = this.convertLineToFace(this.currentLine);
			
			if (face != null) {
				this.faces.add(face);
			}
		}

		this.currentLine = new ScissorLine(this.scissor);

		this.currentLine.setActive();
		
		this.state = State.DRAWING;
	}

	public void resetLine() {
		this.currentLine.reset();
		this.currentLine.setActive();
	}
	
	public void validateLine() {
		if (this.currentLine.getState() != SCISSOR_STATE.HOLD) {
			this.currentLine.endScissor();
		}
		
		this.currentFace = this.convertLineToFace(this.currentLine);
		
		this.currentLine = null;
		
		this.state = State.VALIDATION;
	}
	
	public void resetFace() {
		this.currentFace = null;
		
		this.currentLine = new ScissorLine(this.scissor);
		this.currentLine.setActive();
		
		this.state = State.DRAWING;
	}
	
	public void validateFace() {
		this.faces.add(this.currentFace);
		
		this.currentFace = null;
		
		this.state = State.IDLE;
	}
	
	/**
	 * Convert a scissorLine to a Face object
	 * 
	 * @param line
	 * @return
	 */
	protected Face convertLineToFace(ScissorLine line) {
		//Get all points from scissorLine
		List<Point> points = new LinkedList<Point>();
		for (Polygon polygon : line.getScissorLine()) {
			for (int i=0; i<polygon.npoints; i++) {
				points.add(new Point(polygon.xpoints[i], polygon.ypoints[i]));
			}
		}
		
		//Not enouth points to make a face
		if (points.size() < 10) {
			return null;
		}
		
		return new FaceExctractor().exctractFace(points);
	}

	/**
	 * Tell current scissor line to add a new key point.
	 * 
	 * @param x
	 *            int x coordinate
	 * @param y
	 *            int y coordinate
	 */
	public void addNewKeyPoint(int x, int y) {
		if (this.currentLine != null)
			this.currentLine.addNewKeyPoint(x, y);
	}

	/**
	 * Tell current scissor line that the cursor is moving to (x,y).
	 * 
	 * @param x
	 *            int x coordinate
	 * @param y
	 *            int y coordinate
	 */
	public void setMovePoint(int x, int y) {
		if (this.currentLine != null)
			this.currentLine.setMovePoint(x, y);
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
}
