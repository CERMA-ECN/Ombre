package fr.ecn.ombre.core.shadows;

import fr.ecn.common.core.geometry.Geometry;
import fr.ecn.common.core.geometry.Line;
import fr.ecn.common.core.geometry.Point;

/**
 * ====================================================================
 * =============* Premier Cas: Soleil de face ou de dos
 * ==================
 * ================================================================
 */
public class ShadowDrawingFront extends ShadowDrawing {
	
	protected Couple coupleSoleil;

	/**
	 * @param coupleSoleil
	 */
	public ShadowDrawingFront(Couple coupleSoleil) {
		super();
		this.coupleSoleil = coupleSoleil;
	}

	@Override
	public ShadowDrawingFace drawShadow(ShadowDrawingFace face) {
		// calcul des points de la face ( hors sol )
		Couple[] couples = new Couple[2];
		for (int i = 0; i < 2; i++) { // rayon
			Line rayon = new Line(coupleSoleil.getPointAir(), face.getCouples()[i].getPointAir());
			// fuyante au sol
			Line fuyante = new Line(coupleSoleil.getPointSol(), face.getCouples()[i].getPointSol());
			// on vérifie que la pente du rayon est plus grande que la pente de
			// la fuyante! sinon, ça fait des choses bizarres...
//			if (face.isLeft()) {
//				if (rayon.a > fuyante.a) {
//					throw new ShadowDrawingException("Soleil trop bas! Essayez une autre heure...");
//				}
//			} else {
//				if (rayon.a < fuyante.a) {
//					throw new ShadowDrawingException("Soleil trop bas! Essayez une autre heure...");
//				}
//			}

			// intersection
			Point pointOmbre = Geometry.intersection(rayon, fuyante);
			// ajout du point a la face de l'ombre, couplé avec le pt au sol
			// initial correspondant
			couples[i] = new Couple(pointOmbre, face.getCouples()[i].getPointSol());
		}
		return new ShadowDrawingFace(couples[0], couples[1], false);
	}

}
