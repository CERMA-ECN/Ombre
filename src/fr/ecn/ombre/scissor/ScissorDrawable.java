/**
 * 
 */
package fr.ecn.ombre.scissor;

import fr.ecn.ombre.scissor.algo.ScissorLine;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

/**
 * @author jerome
 *
 */
public class ScissorDrawable extends Drawable {
	
	protected ScissorController scissor;

	/**
	 * @param cissorController
	 */
	public ScissorDrawable(ScissorController scissorController) {
		super();
		this.scissor = scissorController;
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {
		for (ScissorLine scissorLine : this.scissor.scissorLines) {
			
		}
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#getOpacity()
	 */
	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#setAlpha(int)
	 */
	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#setColorFilter(android.graphics.ColorFilter)
	 */
	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub

	}

}
