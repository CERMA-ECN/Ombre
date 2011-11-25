package fr.ecn.ombre.core.shadows;

import fr.ecn.common.core.geometry.Geometry;
import fr.ecn.common.core.geometry.Line;
import fr.ecn.common.core.geometry.Point;


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
	public ShadowDrawingFace drawShadow(ShadowDrawingFace face) {
		Couple[] couples = new Couple[2];
		for (int i=0; i<2; i++) {
			Point topPoint = face.getCouples()[i].getPointAir();
			double b = topPoint.getY() - this.a * topPoint.getX();
			Line rayon = new Line(this.a, b);
			
			Line horizontal = new Line(0, face.getCouples()[i].getPointSol().getY());
			
			Point shadowPoint = Geometry.intersection(horizontal, rayon);
			
			couples[i] = new Couple(shadowPoint, face.getCouples()[i].getPointSol());
		}
		return new ShadowDrawingFace(couples[0], couples[1], false);
	}

}
