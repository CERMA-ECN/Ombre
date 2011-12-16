package fr.ecn.ombre.core.shadows;

import java.util.LinkedList;
import java.util.List;

import fr.ecn.common.core.geometry.Geometry;
import fr.ecn.common.core.geometry.Line;
import fr.ecn.common.core.geometry.Point;
import fr.ecn.common.core.geometry.Segment;

public abstract class ShadowDrawing {

	/**
	 * Method that compute the shadow of a face on the floor
	 * 
	 * @param face
	 * @return
	 */
	public abstract ShadowDrawingFace drawShadow(ShadowDrawingFace face);
	
	/**
	 * Méthode calculOmbreMur Cette méthode détermine les points ayant leur
	 * ombre sur le mur du bétiment F2. Elle prend pour entrée: - la face f d'ou
	 * est tirée l'ombre - la face des ombres de la face f initialement - la
	 * face f2
	 */
	public Couple[] calculOmbreMur(ShadowDrawingFace f1, ShadowDrawingFace faceOmbre, ShadowDrawingFace f2) {
		Couple[] vectPointOmbreF2 = new Couple[2];

		for (int i = 0; i < 2; i++) {

			// On renomme les points qui nous interesse
			Point pointOmbre = faceOmbre.getCouples()[i].getPointAir();

			Point pointBatimentBasF = faceOmbre.getCouples()[i].getPointSol();

			// On trace le segment qui lie le point au sol et son partenaire
			// ombre
			Segment s1 = new Segment(pointOmbre, pointBatimentBasF);

			// On trace le segment des points au sol de cette face
			Segment s2 = new Segment(f2.getCouples()[0].getPointSol(), f2.getCouples()[1]
					.getPointSol());

			// On calcule l'intersection des deux segments
			Point intersectionSegments = Geometry.intersection(s1, s2);

			// On regarde si ce point d'intersection existe
			if (intersectionSegments != null) {
				// Dans ce cas l'ombre est effectivement derrière la face, on
				// modifie donc le point intersection au vecteur ombre
				// On calcule alors le nouveau point correspondant sur le mur:
				// projeté de l'ombre sur le bâtiment
				Line rayonSoleil = new Line(pointOmbre, f1.getCouples()[i].getPointAir());

				Point projeteOmbre = rayonSoleil.calculY((int) intersectionSegments.getX());

				// On rentre ce point dans un nouveau vecteur
				Couple ombreBatiment = new Couple(projeteOmbre, intersectionSegments);

				// On rajoute ce couple dans le vecteur ombre au sol
				vectPointOmbreF2[i] = ombreBatiment;
			} else {
				vectPointOmbreF2[i] = null;
			}
		}

		return vectPointOmbreF2;
	}

	/**
	 * Mathode determinationOmbre Cette méthode permet de calculer l'ombre dans
	 * le cas où on souhaite vérifier si elle se cogne sur un batiment ou non
	 * Elle prend pour entrée ce dont on a eu besoin dans les méthodes du
	 * dessus! Elle ne retourne rien si ce n'est une modification de la
	 * géométrie de l'ombre
	 */
	public List<ShadowDrawingFace> determinationOmbreMur(ShadowDrawingFace f1, ShadowDrawingFace faceOmbre, ShadowDrawingFace f2,
			Couple[] vectPointOmbreF2, Couple coupleSoleil) {
		List<ShadowDrawingFace> ombre = new LinkedList<ShadowDrawingFace>();
		
		//number of couples
		int taille = 0;
		for (int i=0; i<2; i++) {
			if (vectPointOmbreF2[i] != null)
				taille++;
		}
		
		if (taille == 2) {
			return this.deuxPointsMur(f1, vectPointOmbreF2);
		} else if (taille == 1) {
			//Wich one is not null
			int indice = (vectPointOmbreF2[0] == null) ? 1 : 0;
			
			// Nous distinguons alors deux cas comme décrit auparavant!
			int i = (indice + 1) % 2;
			// Nous déterminons le point de l'ombre au sol
			Point ombreSol = faceOmbre.getCouples()[i].getPointAir();
			// On calcule également l'ordonné du point de la droite F2
			// d'abscisse le point de l'ombre au sol
			Line droiteF2Sol = f2.getBottomLine();
			double y = droiteF2Sol.calculY(ombreSol.getX()).getY();

			if (ombreSol.getY() > y) {
				// 1er cas: l'ombre n'est que sur une partie du batiment
				
				f1.premierCas(vectPointOmbreF2[indice], indice, faceOmbre, f2, ombre, coupleSoleil);
			} else {
				// 2eme cas: l'ombre est sur une grande partie du batiment et va
				// jusqu'à un coté du batiment

				f1.deuxiemeCas(vectPointOmbreF2[indice], indice, faceOmbre, f2, ombre, coupleSoleil);
			}

		} else if (taille == 0) {
			f1.zeroPoints(faceOmbre, f2, ombre, coupleSoleil);
		}
		
		return ombre;
	}
	
	/**
	 * Méthode deuxPointsMur Cette méthode permet de calculer les différentes
	 * faces de l'ombre lorsque toute la face f est projetée sur la face f2 Elle
	 * prend donc pour entrée: - le vecteur des points sur le mur - la géométrie
	 * de l'ombre Elle ne retourne rien mais modifie la géométrie de l'ombre
	 * @return 
	 */
	protected List<ShadowDrawingFace> deuxPointsMur(ShadowDrawingFace f1, Couple[] vectPointOmbreF2) {
		List<ShadowDrawingFace> ombre = new LinkedList<ShadowDrawingFace>();
		// On rentre les faces correspondantes à la géométrie de l'ombre

		// Ombre sur le mur:
		ombre.add(new ShadowDrawingFace(vectPointOmbreF2[0], vectPointOmbreF2[1], true));

		// Ombre sur le sol:
		Couple couple1 = new Couple(vectPointOmbreF2[0].getPointSol(), f1.getCouples()[0].getPointSol());
		Couple couple2 = new Couple(vectPointOmbreF2[1].getPointSol(), f1.getCouples()[1].getPointSol());
		ombre.add(new ShadowDrawingFace(couple1, couple2, false));
		
		return ombre;
	}

}
