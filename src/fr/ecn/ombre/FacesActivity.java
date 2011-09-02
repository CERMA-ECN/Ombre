/**
 * 
 */
package fr.ecn.ombre;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import fr.ecn.ombre.model.ImageInfos;
import fr.ecn.ombre.scissor.ScissorController;
import fr.ecn.ombre.scissor.ScissorDrawable;

/**
 * @author jerome
 *
 */
public class FacesActivity extends Activity implements OnTouchListener {

	private static final int MENU_ADDFACE   = Menu.FIRST;
	private static final int MENU_RESETFACE = Menu.FIRST + 1;
	private static final int MENU_VALIDATE  = Menu.FIRST + 2;
	
	protected ScissorController controller;
	
	protected Matrix matrix;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		final ImageInfos imageInfos = (ImageInfos) extras
				.getSerializable("ImageInfos");

		this.controller = (ScissorController) this
				.getLastNonConfigurationInstance();
		
		if (this.controller == null) {
			this.setContentView(R.layout.computing);
			
			new Thread(new Runnable() {
				public void run() {
					controller = new ScissorController(imageInfos);
					
					runOnUiThread(new Runnable() {
						public void run() {
							setUp();
						}
					});
				}
			}).start();
		} else {
			setUp();
		}
	}

	/**
	 * set up views based on controller infos
	 */
	protected void setUp() {
		this.setContentView(R.layout.select_faces);
		
		ImageView imageView = (ImageView) findViewById(R.id.image);
		
		this.matrix = null;
		
		Drawable[] drawables = {new BitmapDrawable(this.controller.getBitmap()), new ScissorDrawable(this.controller)}; 
		imageView.setImageDrawable(new LayerDrawable(drawables));
		imageView.setOnTouchListener(this);
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (!this.controller.hasLine()) {
			return true;
		}
		
		if (this.matrix == null) {
			this.matrix = new Matrix();
			
			((ImageView) findViewById(R.id.image)).getImageMatrix().invert(this.matrix);
		}
		
		float[] point = {event.getX(), event.getY()};
		
		//Converting the point in image coordinate system
		this.matrix.mapPoints(point);
		
		//System.out.println(point[0] + " " + point[1]);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			this.controller.addNewKeyPoint((int) Math.round(point[0]), (int) Math.round(point[1]));
		} else {
			this.controller.setMovePoint((int) Math.round(point[0]), (int) Math.round(point[1]));
		}
		
		this.findViewById(R.id.image).invalidate();
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		return this.controller;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ADDFACE, 0, R.string.menu_addface);
		menu.add(0, MENU_RESETFACE, 0, R.string.menu_resetface);
		menu.add(0, MENU_VALIDATE, 0, R.string.menu_validate);
		return result;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ADDFACE:
			this.controller.newLine();
			return true;
		case MENU_RESETFACE:
			this.controller.resetCurrentLine();
			this.findViewById(R.id.image).invalidate();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}