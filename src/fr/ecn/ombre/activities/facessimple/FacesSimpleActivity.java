package fr.ecn.ombre.activities.facessimple;

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

public class FacesSimpleActivity extends Activity implements OnTouchListener {

	private static final int MENU_START_FACE = Menu.FIRST;
	private static final int MENU_VALIDATE = Menu.FIRST + 1;

	protected ImageInfos imageInfos;

	protected FaceSimpleController controller;

	protected Matrix matrix;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		final ImageInfos imageInfos = (ImageInfos) extras
				.getSerializable("ImageInfos");
		this.imageInfos = imageInfos;

		this.controller = new FaceSimpleController(imageInfos);

		this.setUp();
	}

	/**
	 * set up views based on controller infos
	 */
	protected void setUp() {
		this.setContentView(R.layout.select_faces);

		ImageView imageView = (ImageView) findViewById(R.id.image);

		this.matrix = null;

		Drawable[] drawables = {
				new BitmapDrawable(this.controller.getBitmap()),
				new FacesSimpleDrawable(this.controller) };
		imageView.setImageDrawable(new LayerDrawable(drawables));
		imageView.setOnTouchListener(this);
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (this.controller.getPoints() == null) {
			return false;
		}
		
		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return false;
		}

		if (this.matrix == null) {
			this.matrix = new Matrix();

			((ImageView) findViewById(R.id.image)).getImageMatrix().invert(
					this.matrix);
		}

		float[] point = { event.getX(), event.getY() };

		// Converting the point in image coordinate system
		this.matrix.mapPoints(point);

		this.controller.addPoint(point[0], point[1]);

		this.findViewById(R.id.image).invalidate();

		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		if (this.controller.getPoints() == null) {
			menu.add(0, MENU_START_FACE, 0, R.string.menu_addface);
		} else {
			menu.add(0, MENU_START_FACE, 0, R.string.menu_reset_face);
		}
		menu.add(0, MENU_VALIDATE, 0, R.string.menu_validate);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_START_FACE:
			this.controller.startFace();
			this.findViewById(R.id.image).invalidate();
			return true;
		case MENU_VALIDATE:
			this.imageInfos.setFaces(this.controller.getFaces());
			Intent i = new Intent();
			i.putExtra("ImageInfos", this.imageInfos);
			setResult(RESULT_OK, i);
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
