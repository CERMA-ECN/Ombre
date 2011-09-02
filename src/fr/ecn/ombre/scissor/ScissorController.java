package fr.ecn.ombre.scissor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.widget.ImageView;

import jjil.android.RgbImageAndroid;
import fr.ecn.ombre.image.utils.ImageLoader;
import fr.ecn.ombre.model.Face;
import fr.ecn.ombre.model.ImageInfos;
import fr.ecn.ombre.scissor.algo.SCISSOR_STATE;
import fr.ecn.ombre.scissor.algo.Scissor;
import fr.ecn.ombre.scissor.algo.ScissorLine;

public class ScissorController {

	protected Bitmap bitmap;

	protected Scissor scissor;
	protected ScissorLine currentScissorLine;
	protected List<ScissorLine> scissorLines = new ArrayList<ScissorLine>();
	
	protected List<Face> faces = new LinkedList<Face>();

	/**
	 * @param imageInfos
	 */
	public ScissorController(ImageInfos imageInfos) {
		super();

		this.bitmap = ImageLoader.loadResized(imageInfos.getPath(), 600);

		this.scissor = new Scissor(RgbImageAndroid.toRgbImage(bitmap));
	}

	/**
	 * Tell this controller to generate a new scissor line.
	 */
	public void newLine() {
		if (this.currentScissorLine != null) {
			if (this.currentScissorLine.getState() != SCISSOR_STATE.HOLD) {
				this.currentScissorLine.endScissor();
			}
		}

		this.currentScissorLine = new ScissorLine(this.scissor);

		this.scissorLines.add(this.currentScissorLine);

		this.currentScissorLine.setActive();
	}

	public void resetCurrentLine() {
		this.currentScissorLine.reset();
		this.currentScissorLine.setActive();
	}

	public boolean hasLine() {
		return this.currentScissorLine != null;
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
		if (this.currentScissorLine != null)
			this.currentScissorLine.addNewKeyPoint(x, y);
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
		if (this.currentScissorLine != null)
			this.currentScissorLine.setMovePoint(x, y);
	}

	/**
	 * @return the bitmap
	 */
	public Bitmap getBitmap() {
		return bitmap;
	}
}
