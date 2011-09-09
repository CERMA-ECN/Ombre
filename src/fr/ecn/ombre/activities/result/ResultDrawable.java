package fr.ecn.ombre.activities.result;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import fr.ecn.ombre.android.Drawing;
import fr.ecn.ombre.core.model.Face;

public class ResultDrawable extends Drawable {

	protected ResultController controller;

	/**
	 * @param controller
	 */
	public ResultDrawable(ResultController controller) {
		super();
		this.controller = controller;
	}

	@Override
	public void draw(Canvas canvas) {
		// Drawing sun if he is in the image
		if (this.controller.getSunPosition() != null && this.controller.getSunPosition().getX() > 0
				&& this.controller.getSunPosition().getX() < this.controller.getBitmap().getWidth()
				&& this.controller.getSunPosition().getY() > 0
				&& this.controller.getSunPosition().getY() < this.controller.yHorizon) {
			Paint paint = new Paint();
			paint.setColor(Color.YELLOW);
			
			canvas.drawCircle((int) this.controller.getSunPosition().getX(), (int) this.controller.getSunPosition().getY(), 10, paint);
		}
		
		//Drawing shadows
		Paint paint = new Paint();
		paint.setColor(Color.argb(50, 0, 0, 255));
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		for (Face fOmbre : this.controller.getShadows()) {
			Drawing.drawFace(fOmbre, canvas, paint);
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
