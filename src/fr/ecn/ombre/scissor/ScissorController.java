package fr.ecn.ombre.scissor;

import java.util.ArrayList;
import java.util.List;

import jjil.android.RgbImageAndroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import fr.ecn.ombre.model.ImageInfos;
import fr.ecn.ombre.scissor.algo.SCISSOR_STATE;
import fr.ecn.ombre.scissor.algo.Scissor;
import fr.ecn.ombre.scissor.algo.ScissorLine;

public class ScissorController implements View.OnTouchListener {
	
	protected ImageView  imageView;
	
	protected Bitmap bitmap;
	
	protected Scissor scissor;
	protected ScissorLine currentScissorLine;
	protected List<ScissorLine> scissorLines = new ArrayList<ScissorLine>();
	
	protected Matrix matrix = null;

	/**
	 * @param imageInfos
	 */
	public ScissorController(ImageInfos imageInfos) {
		super();
		
		Bitmap bitmap = BitmapFactory.decodeFile(imageInfos.getPath());
		
		//Auto resize
		if (bitmap.getHeight() > 1000 || bitmap.getWidth() > 1000) {
			Matrix matrix = new Matrix();
			//TODO: calculate scale values
			matrix.postScale(0.25f, 0.25f);
			
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}
		
		this.bitmap = bitmap;
		
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
		this.imageView.invalidate();
	}

	public void setUp(ImageView imageView) {
		this.imageView = imageView;
		this.matrix = null;
		
		Drawable[] drawables = {new BitmapDrawable(bitmap), new ScissorDrawable(this)}; 
		imageView.setImageDrawable(new LayerDrawable(drawables));
		imageView.setOnTouchListener(this);
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (this.currentScissorLine == null) {
			return true;
		}
		
		//System.out.println("Touch : " + event);
		
		float[] point = {event.getX(), event.getY()};
		
		//Converting the point in image coordinate system
		this.getInvMatrix().mapPoints(point);
		
		//System.out.println(point[0] + " " + point[1]);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			this.currentScissorLine.addNewKeyPoint((int) Math.round(point[0]), (int) Math.round(point[1]));
		} else {
			this.currentScissorLine.setMovePoint((int) Math.round(point[0]), (int) Math.round(point[1]));
		}
		
		this.imageView.invalidate();
		
		return true;
	}
	
	/**
	 * Creates and return the transformation matrix from screen coordinates to image coordinates.
	 * 
	 * @return
	 */
	public Matrix getInvMatrix() {
		if (this.matrix == null)  {
			this.matrix = new Matrix();
			this.imageView.getImageMatrix().invert(this.matrix);
		}
		
		return this.matrix;
	}

}
