/**
 * 
 */
package fr.ecn.ombre.activities.faces;

import android.app.Activity;
import android.content.Intent;
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

import fr.ecn.ombre.R;
import fr.ecn.ombre.model.ImageInfos;

/**
 * @author jerome
 *
 */
public class FacesActivity extends Activity implements OnTouchListener {

	private static final int MENU_START_FACE = Menu.FIRST;
	private static final int MENU_VALIDATE = Menu.FIRST + 1;
	private static final int MENU_RESET_LINE = Menu.FIRST + 2;
	private static final int MENU_VALIDATE_LINE = Menu.FIRST + 3;
	private static final int MENU_RESET_FACE = Menu.FIRST + 4;
	private static final int MENU_VALIDATE_FACE = Menu.FIRST + 5;
	
	protected ImageInfos imageInfos;
	
	protected FacesController controller;
	
	protected Matrix matrix;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		final ImageInfos imageInfos = (ImageInfos) extras
				.getSerializable("ImageInfos");
		this.imageInfos = imageInfos;

		this.controller = (FacesController) this
				.getLastNonConfigurationInstance();
		
		if (this.controller == null) {
			this.setContentView(R.layout.computing);
			
			new Thread(new Runnable() {
				public void run() {
					controller = new FacesController(imageInfos);
					
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
		
		Drawable[] drawables = {new BitmapDrawable(this.controller.getBitmap()), new FacesDrawable(this.controller)}; 
		imageView.setImageDrawable(new LayerDrawable(drawables));
		imageView.setOnTouchListener(this);
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (this.controller.getState() != FacesController.State.DRAWING) {
			return false;
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
		menu.add(0, MENU_START_FACE, 0, R.string.menu_addface);
		menu.add(0, MENU_VALIDATE, 0, R.string.menu_validate);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		switch (this.controller.getState()) {
		case IDLE:
			menu.add(0, MENU_START_FACE, 0, R.string.menu_addface);
			menu.add(0, MENU_VALIDATE, 0, R.string.menu_validate);
			break;
		case DRAWING:
			menu.add(0, MENU_RESET_LINE, 0, R.string.menu_reset_face);
			menu.add(0, MENU_VALIDATE_LINE, 0, R.string.menu_validate_line);
			break;
		case VALIDATION:
			menu.add(0, MENU_RESET_FACE, 0, R.string.menu_reset_face);
			menu.add(0, MENU_VALIDATE_FACE, 0, R.string.menu_validate_face);
			break;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_START_FACE:
			this.controller.startLine();
			this.findViewById(R.id.image).invalidate();
			return true;
		case MENU_VALIDATE:
			this.imageInfos.setFaces(this.controller.getFaces());
			Intent i = new Intent();
			i.putExtra("ImageInfos", this.imageInfos);
			setResult(RESULT_OK, i);
			finish();
			return true;
		case MENU_RESET_LINE:
			this.controller.resetLine();
			this.findViewById(R.id.image).invalidate();
			return true;
		case MENU_VALIDATE_LINE:
			this.controller.validateLine();
			this.findViewById(R.id.image).invalidate();
			return true;
		case MENU_RESET_FACE:
			this.controller.resetFace();
			this.findViewById(R.id.image).invalidate();
			return true;
		case MENU_VALIDATE_FACE:
			this.controller.validateFace();
			this.findViewById(R.id.image).invalidate();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}