package fr.ecn.ombre.cissor;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import fr.ecn.ombre.model.ImageInfos;

public class CissorController implements View.OnTouchListener {
	
	protected ImageInfos imageInfos;
	protected ImageView  imageView;

	/**
	 * @param imageInfos
	 */
	public CissorController(ImageInfos imageInfos) {
		super();
		this.imageInfos = imageInfos;
	}

	public void setUp(ImageView imageView) {
		this.imageView = imageView;
		
		final Drawable[] drawables = {new BitmapDrawable(BitmapFactory.decodeFile(imageInfos.getPath())), new CissorDrawable()}; 
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
		
		System.out.println(point[0] + ":" + point[1]);
		
		return true;
	}

}
