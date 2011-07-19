/**
 * 
 */
package fr.ecn.ombre.scissor;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import fr.ecn.ombre.scissor.algo.Scissor;

/**
 * @author jerome
 *
 */
public class CissorDrawable extends Drawable {
	
	protected Scissor scissor;

	/**
	 * @param scissor
	 */
	public CissorDrawable(Scissor scissor) {
		super();
		this.scissor = scissor;
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub

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
