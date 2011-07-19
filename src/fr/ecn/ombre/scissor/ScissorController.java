package fr.ecn.ombre.scissor;

import java.util.ArrayList;
import java.util.List;

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
	
	protected ImageInfos imageInfos;
	protected ImageView  imageView;
	
	protected Scissor scissor;
	protected ScissorLine currentScissorLine;
	protected List<ScissorLine> scissorLines = new ArrayList<ScissorLine>();

	/**
	 * @param imageInfos
	 */
	public ScissorController(ImageInfos imageInfos) {
		super();
		this.imageInfos = imageInfos;
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
	}

	public void setUp(ImageView imageView) {
		this.imageView = imageView;
		
		Bitmap bitmap = BitmapFactory.decodeFile(imageInfos.getPath());
		
		//this.scissor = new Scissor();
		
		Drawable[] drawables = {new BitmapDrawable(bitmap), new ScissorDrawable(this)}; 
		imageView.setImageDrawable(new LayerDrawable(drawables));
		imageView.setOnTouchListener(this);
	}

	public boolean onTouch(View v, MotionEvent event) {
		System.out.println("Touch : " + event);
		
		//Creating the transform matrix from screen coordinates to image coordinates
		Matrix matrix = new Matrix();
		this.imageView.getImageMatrix().invert(matrix);
		
		float[] point = {event.getX(), event.getY()};
		
		//Converting the point in image coordinate system
		matrix.mapPoints(point);
		
		this.currentScissorLine.setMovePoint((int) point[0], (int) point[1]);
		
		return true;
	}

}
