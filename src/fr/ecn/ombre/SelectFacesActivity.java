/**
 * 
 */
package fr.ecn.ombre;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import fr.ecn.ombre.cissor.CissorController;
import fr.ecn.ombre.model.ImageInfos;

/**
 * @author jerome
 *
 */
public class SelectFacesActivity extends Activity {

	private static final int MENU_ADDFACE     = Menu.FIRST;
	private static final int MENU_REMOVEFACES = Menu.FIRST + 1;
	private static final int MENU_VALIDATE    = Menu.FIRST + 2;
	
	CissorController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.select_faces);
		
		ImageView image = (ImageView) findViewById(R.id.image);
		
		Bundle extras = getIntent().getExtras();
		final ImageInfos imageInfos = (ImageInfos) extras.getSerializable("ImageInfos");
		
		controller = new CissorController(imageInfos);
		controller.setUp(image);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ADDFACE, 0, R.string.menu_addface);
		menu.add(0, MENU_REMOVEFACES, 0, R.string.menu_removefaces);
		menu.add(0, MENU_VALIDATE, 0, R.string.menu_validate);
		return result;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ADDFACE:
			//TODO: do something with intelligent scissors
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
