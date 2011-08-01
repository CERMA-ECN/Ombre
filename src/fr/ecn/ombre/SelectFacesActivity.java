/**
 * 
 */
package fr.ecn.ombre;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import fr.ecn.ombre.model.ImageInfos;
import fr.ecn.ombre.scissor.ScissorController;

/**
 * @author jerome
 *
 */
public class SelectFacesActivity extends Activity {

	private static final int MENU_ADDFACE   = Menu.FIRST;
	private static final int MENU_RESETFACE = Menu.FIRST + 1;
	private static final int MENU_VALIDATE  = Menu.FIRST + 2;
	
	protected ScissorController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.select_faces);
		
		ImageView image = (ImageView) findViewById(R.id.image);
		
		Bundle extras = getIntent().getExtras();
		final ImageInfos imageInfos = (ImageInfos) extras.getSerializable("ImageInfos");
		
		ScissorController controller = (ScissorController) this.getLastNonConfigurationInstance();
		if (controller == null) {
			controller = new ScissorController(imageInfos);
		}
		
		this.controller = controller;
		controller.setUp(image);
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
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
