package fr.ecn.ombre.core.shadows;

import fr.ecn.ombre.core.model.Couple;
import fr.ecn.ombre.core.model.Droite;
import fr.ecn.ombre.core.model.Face;
import fr.ecn.ombre.core.model.Point;


/**
 * ====================================================================
 * =============* Deuxième Cas: Soleil de côté (ensoleillement frontal)
 * 
 * Construction de l'ombre de base facile ( cf. site internet de
 * référence) Faisable si quelqu'un veut reprendre ce cas...
 * ============
 * ==========================================================
 * ============
 */
public class ShadowDrawingSide extends ShadowDrawing {

	protected double a;
	
	public ShadowDrawingSide(double relativeAzimut, double altitude) {
		this.a = Math.signum(relativeAzimut) * Math.tan(altitude);
	}

	@Override
	public Face drawShadow(Face face) {
		Couple[] couples = new Couple[2];
		for (int i=0; i<2; i++) {
			Point topPoint = face.getCouples()[i].getPointAir();
			double b = topPoint.getY() - this.a * topPoint.getX();
			Droite rayon = new Droite(this.a, b);
			
			Droite horizontal = new Droite(0, face.getCouples()[i].getPointSol().getY());
			
			Point shadowPoint = horizontal.intersection(rayon);
			
			couples[i] = new Couple(shadowPoint, face.getCouples()[i].getPointSol());
		}
		return new Face(couples[0], couples[1], false);
	}

}
