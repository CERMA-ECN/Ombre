/**
 * 
 */
package fr.ecn.ombre.android;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import fr.ecn.ombre.android.utils.ImageLoader;
import fr.ecn.ombre.core.model.ImageInfos;
import fr.ecn.ombre.core.model.Point;

/**
 * An activity to check and edit the position of the horizon line
 * 
 * @author jerome
 *
 */
public class HorizonActivity extends Activity implements View.OnTouchListener {

	private static final int MENU_LOCK_UNLOCK = Menu.FIRST;
	private static final int MENU_VALIDATE = Menu.FIRST + 1;

	protected ImageInfos imageInfos;
	
	/**
	 * The matrix for transformation from screen coordinates to image coordinates.
	 */
	protected Matrix matrix = null;
	
	protected boolean locked = true;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			this.imageInfos = (ImageInfos) savedInstanceState.getSerializable("ImageInfos");
		} else {
			Bundle extras = getIntent().getExtras();
			this.imageInfos = (ImageInfos) extras.getSerializable("ImageInfos");
		}
		
		ImageInfos imageInfos = this.imageInfos;
		
		Bitmap bitmap = ImageLoader.loadResized(imageInfos.getPath(), 600);
		
		//We get yHorizon from the vanishing points if possible
		if (imageInfos.getYHorizon() == null) {
			List<Point> vanishingPoints = imageInfos.getVanishingPoints();
			
			if (vanishingPoints != null && vanishingPoints.size() > 0) {
				double y = 0;
				
				for (Point p : vanishingPoints) {
					y += p.getY();
				}
				
				y /= vanishingPoints.size();
				
				imageInfos.setYHorizon(y);
			} else {
				// If we don't have informations on the vanishing points we just set
				// the horizon line in the middle of the image.
				imageInfos.setYHorizon((double)bitmap.getHeight()/2);

				// If the horizon line was set by default we directly start in
				// edition mode
				this.locked = false;
			}
		}
		
		//Setting layout
		this.setContentView(R.layout.image);
		
		ImageView image = (ImageView) this.findViewById(R.id.image);
		
		image.setImageDrawable(new HorizonDrawable(bitmap, imageInfos));
		image.setOnTouchListener(this);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putSerializable("ImageInfos", this.imageInfos);
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (this.locked) {
			return false;
		}
		
		ImageView image = (ImageView) this.findViewById(R.id.image);
		
		if (this.matrix == null) {
			this.matrix = new Matrix();
			
			image.getImageMatrix().invert(this.matrix);
		}
		
		float[] point = {event.getX(), event.getY()};
		
		//Converting the point in image coordinate system
		this.matrix.mapPoints(point);
		
		this.imageInfos.setYHorizon((double)point[1]);
		
		image.invalidate();
		
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_LOCK_UNLOCK, 0, R.string.menu_edit_horizon);
		menu.add(0, MENU_VALIDATE, 0, R.string.menu_validate);
		return result;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(MENU_LOCK_UNLOCK).setEnabled(this.locked);
		return super.onPrepareOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_LOCK_UNLOCK:
			this.locked = false;
			return true;
		case MENU_VALIDATE:
			Intent i = new Intent(this, FacesChoiceActivity.class);
			i.putExtra("ImageInfos", this.imageInfos);
			this.startActivity(i);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	

}
