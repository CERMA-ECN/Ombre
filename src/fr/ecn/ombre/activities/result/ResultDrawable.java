package fr.ecn.ombre.activities.result;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import fr.ecn.ombre.model.Face;

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
		// DESSIN DU SOLEIL s'il est sur l'image ( soleil de face uniquement,
		// donc au dessus de l'horizon...)
		if ((this.controller.getSunPosition().getX() > 0) && (this.controller.getSunPosition().getX() < this.controller.getBitmap().getWidth())
				&& (this.controller.getSunPosition().getY() > 0) && (this.controller.getSunPosition().getY() < this.controller.yHorizon)) {
			Paint paint = new Paint();
			paint.setColor(Color.YELLOW);
			
			canvas.drawCircle((int) this.controller.getSunPosition().getX(), (int) this.controller.getSunPosition().getY(), 10, paint);
		}
		
		Paint paint = new Paint();
		paint.setColor(Color.argb(50, 0, 0, 255));
		paint.setStyle(Paint.Style.FILL_AND_STROKE);

		// ===========================================================================//
		// Tracé de l'ombre:
		// -Remplissage de polygones : image.remplirFace(face)
		// -Si a case est coch�e, on agrandi a la rue et on trace les nouvelles
		// faces
		// TEMPS A GAGNER ICI!!! ==>AMELIORER méthode remplissage ombres...
		// - ne pas reparcourir a chaque fois l'image pour remplir une face,
		// - mais plutot par exemple parcourir une fois et remplir toutes les
		// faces en m�me temps...
		// --> m�thode de remplisage a modifier...
		// ===========================================================================//
		for (Face f : this.controller.getSelectedFaces()) {
			f.draw(canvas, paint);
		}
		paint.setColor(Color.argb(50, 100, 0, 255));
		for (Face fOmbre : this.controller.getShadows()) {
			// les ombres ajoutées ne sont que celles qui sont dans le bon sens,
			// donc on les remplit toutes:
			fOmbre.draw(canvas, paint);
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
