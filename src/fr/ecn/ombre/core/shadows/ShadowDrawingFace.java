package fr.ecn.ombre.core.shadows;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import fr.ecn.common.core.geometry.Point;
import fr.ecn.common.core.imageinfos.Face;
import fr.ecn.ombre.core.image.Image;
import fr.ecn.ombre.core.model.Droite;
import fr.ecn.ombre.core.model.Segment;

/**
 * A class that represent a Face for shadow drawing code
 * 
 * @author jerome
 *
 */
public class ShadowDrawingFace implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Couples of points form left to right
	 */
	protected Couple[] couples;

	protected boolean isBuilding = true;

	/**
	 * Create a Face from 4 points.
	 * 
	 * The points will be ordered in an array of 2 couples
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param p4
	 */
	public ShadowDrawingFace(Point p1, Point p2, Point p3, Point p4) {
		Point[] points = { p1, p2, p3, p4 };
		
		//We sort the points by X coordinate
		Arrays.sort(points, new Comparator<Point>() {
			public int compare(Point p1, Point p2) {
				return (int) (p1.getX() - p2.getX());
			}
		});

		// Ordering points
		Point topRightPoint;
		Point bottomRightPoint;
		Point topLeftPoint;
		Point bottomLeftPoint;

		{//The two left points are points[0] and points[1]
			// NOTE: le point est plus haut si ( en coordonnées image ) y est
			// plus petit!
			if (points[0].getY() < points[1].getY()) {
				topLeftPoint = points[0];
				bottomLeftPoint = points[1];
			} else {
				topLeftPoint = points[1];
				bottomLeftPoint = points[0];
			}
		}

		{//The two left points are points[2] and points[3]
			// NOTE: le point est plus haut si ( en coordonnées image ) y est
			// plus petit!
			if (points[2].getY() < points[3].getY()) {
				topRightPoint = points[2];
				bottomRightPoint = points[3];
			} else {
				topRightPoint = points[3];
				bottomRightPoint = points[2];
			}
		}
		
		Couple[] couples = { new Couple(topLeftPoint, bottomLeftPoint),
				new Couple(topRightPoint, bottomRightPoint) };

		this.couples = couples;
	}

	/**
	 * Create a shadowDrawingFace from a Face object
	 * 
	 * @param face
	 */
	public ShadowDrawingFace(Face face) {
		this(face.getPoints().get(0), face.getPoints().get(1), face.getPoints().get(2), face.getPoints().get(3));
	}

	/**
	 * @param couples
	 * @param isBuilding
	 */
	public ShadowDrawingFace(Couple couple1, Couple couple2, boolean isBuilding) {
		super();
		Couple[] couples = { couple1, couple2 };
		this.couples = couples;
		this.isBuilding = isBuilding;
	}

	/**
	 * @return the couples
	 */
	public Couple[] getCouples() {
		return couples;
	}

	/**
	 * @return the isBuilding
	 */
	public boolean isBuilding() {
		return isBuilding;
	}

	/**
	 * Return all points
	 * 
	 * @return the points
	 */
	public Point[] getPoints() {
		Point[] points = new Point[this.couples.length * 2];
		for (int i = 0; i < this.couples.length; i++) {
			points[i] = this.couples[i].getPointSol();
			points[points.length - 1 - i] = this.couples[i].getPointAir();
		}
		return points;
	}
	
	/**
	 * @return the line that goes through the 2 top points of this face
	 */
	public Droite getTopLine() {
		return new Droite(this.couples[0].getPointAir(), this.couples[1].getPointAir());
	}

	
	/**
	 * @return the line that goes through the 2 bottom points of this face
	 */
	public Droite getBottomLine() {
		return new Droite(this.couples[0].getPointSol(), this.couples[1].getPointSol());
	}

	/**
	 * Returns the index of the nearest point in Face.
	 * 
	 * @return returns -1 if Face is empty
	 */
	public int getPlusProche() {
		int y = 0;
		int yCurr;
		int indice = -1;
		if (this.couples.length != 0) {
			for (int i = 0; i < this.couples.length; i++) {
				yCurr = (int) this.couples[i].getPointSol().getY();
				if (yCurr > y) {
					y = yCurr;
					indice = i;
				}
			}
		}
		return indice;
	}

	/**
	 * méthode permettant de dire si la face est à gauche ou a droite ( que pour
	 * les faces verticales ) - méthode utilisée par la méthode expandToStreet.
	 * 
	 * @return
	 */
	public boolean isLeft() {
		int i = this.getPlusProche();
		if (this.couples[i].getPointSol().getX() < this.couples[(i + 1) % 2].getPointSol().getX()) {
			return true;
		} else
			return false;
	}

	/**
	 * Expand face in our direction...
	 */
	public ShadowDrawingFace expandToStreet(Image image) {
		int i = this.getPlusProche(); // avec deux couples de points par face,
		// i=0 ou 1.
		Droite dSol = this.getBottomLine();
		Droite d = this.getTopLine();

		Point newPsol;
		Point newP;

		// différents cas selon que la face est verticale ou horizontale:

		if (this.isBuilding) {// CAS VERTICAL
			// Test sur les pentes des droites:
			boolean test;
			if (this.isLeft()) {
				if (dSol.a < d.a) {
					test = true;
				} else
					test = false;
			} else {
				if (dSol.a > d.a) {
					test = true;
				} else
					test = false;
			}
			// calcul des nouveaux points selon la valeur du test:
			if (test == true) {
				newPsol = dSol.calculX(image.getHeight());
				newP = d.calculY(newPsol.getX());
			} else {
				newPsol = newP = d.intersection(dSol);
			}
		} else { // CAS HORIZONTAL ( ombres uniquement )
			newPsol = dSol.calculX(image.getHeight());
			newP = d.calculX(image.getHeight());
		}

		// Création du nouveau couple de points et création de la nouvelle face:
		Couple newCouple = new Couple(newP, newPsol);
		ShadowDrawingFace newFace = new ShadowDrawingFace(couples[i], newCouple, this.isBuilding);
		
		return newFace;
	}

	/**
	 * Méthode permettant de savoir si l'ombre calculée est dans le bon sens...
	 * c'est a dire que la face projetée n'est pas éclairée, auquel cas, son
	 * ombre irait "dans le batiment"
	 * 
	 * Par la suite on ne dessinera donc que les ombre qui sont effectivement
	 * outside!
	 * 
	 * @return
	 */
	public boolean isOutside() {
		// on recupere la droite formée par les deux points au sol de la face de
		// depart:
		Droite sol = this.getBottomLine();
		// on prend les coordonnées du premier point d'intersection calculé
		// (point projeté sur le sol)
		double x = this.couples[0].getPointAir().getX();
		double y = this.couples[0].getPointAir().getY();
		// on calcule l'ordonnée du point se trouvant sur la droite sol et
		// d'abscisse x
		double yDroite = sol.calculY(x).getY();
		// d'après des considérations géométriques,
		// on a une condition pour dire si l'ombre est dans le bon sens!
		if (y > yDroite) {
			return true;
		} else {
			return false;
		}
		// NOTE: un seul point suffit, on a pris arbitrairement this.GetPoint(0)
	}

	/*
	 * Méthode premierCas Cette méthode s'applique dans le cas où l'ombre n'est
	 * que sur une petite partie du bâtiment. Elle prend pour entrée: - le
	 * vecteur des points sur le mur - la face des ombres initiale - la face f2
	 * - la géométrie de l'ombre - le point du Soleil Elle ne retourne rien mais
	 * modifie la géométrie de l'ombre en lui rajoutant les faces ainsi
	 * calculées
	 */
	public void premierCas(Couple pointOmbreF2, int i, ShadowDrawingFace faceOmbre, ShadowDrawingFace f2,
			List<ShadowDrawingFace> ombre, Couple coupleSoleil) {
		
		// Définissons les points dont nous aurons besoin pour ce programme:
		// Point vraieOmbreMur: coordonnées du point de l'ombre projeté sur le
		// mur si il n'y avait pas le batiment F2
		Point vraieOmbreMur = faceOmbre.getCouples()[i].getPointAir();

		// Point ombreSol: deuxième point de l'ombre qui lui se trouve au sol
		Point ombreSol = faceOmbre.getCouples()[(i + 1) % 2].getPointAir();
		// Point de l'ombre sur le mur
		Point ombreMur = pointOmbreF2.getPointAir();

		// point de l'ombre sur le mur projeté au sol de f2
		Point ombreMurSol = pointOmbreF2.getPointSol();

		// point du sol de f donnant son ombre sur le mur
		Point departOmbreMurBas = this.getCouples()[i].getPointSol();
		// point du sol de f donnant son ombre sur le sol
		Point departOmbreSolBas = this.getCouples()[(i + 1) % 2].getPointSol();

		// définissons certaines droites dont nous aurons besoin par la suite
		Droite droiteF2Sol = f2.getBottomLine();
		Droite batimentFHaut = this.getTopLine();
		Droite batimentFBas = this.getBottomLine();

		Droite d = new Droite(ombreSol, vraieOmbreMur);
		// On calcule le dernier point de l'ombre qui se trouve sur le batiment
		// F2
		Point dernierPointMur = d.intersection(droiteF2Sol);

		// Ce point se trouve alors au sol!
		// Il nous faut calculer son équivalent au sol du batiment F: d'o� part
		// l'ombre de ce dernier point?
		Droite rayonDernierPoint = new Droite(dernierPointMur, coupleSoleil.getPointAir());
		Point pointEquivalentHaut = rayonDernierPoint.intersection(batimentFHaut);

		Point pointEquivalentBas = batimentFBas.calculY((int) pointEquivalentHaut.getX());
		// Dans ce cas, on peut créer et rentrer de nouvelles faces
		// face de l'ombre sur le mur
		ombre.add(new ShadowDrawingFace(new Couple(ombreMur, ombreMurSol), new Couple(dernierPointMur,
				dernierPointMur), true));

		// face de l'ombre devant le mur
		ombre.add(new ShadowDrawingFace(new Couple(ombreMurSol, departOmbreMurBas), new Couple(dernierPointMur,
				pointEquivalentBas), false));

		// face de l'ombre sur le sol
		ombre.add(new ShadowDrawingFace(new Couple(ombreSol, departOmbreSolBas), new Couple(dernierPointMur, pointEquivalentBas), false));

	}

	/*
	 * Méthode deuxiemeCas Cette méthode s'applique dans le cas o� l'ombre va
	 * jusqu'� un bout de la face, et est oblig�e de retourner sur le Sol car
	 * face termin�e Elle prend pour entr�e: - le vecteur des points sur le mur
	 * - la face des ombres initiale - la face f2 - la g�om�trie de l'ombre - le
	 * point du Soleil Elle ne retourne rien mais modifie la g�om�trie de
	 * l'ombre en lui rajoutant les faces ainsi calcul�es
	 */

	public void deuxiemeCas(Couple pointOmbreF2, int indice, ShadowDrawingFace faceOmbre, ShadowDrawingFace f2,
			List<ShadowDrawingFace> ombre, Couple coupleSoleil) {

		// On doit tout d'abord trouver le dernier point d'ombre sur le mur.
		// pour cela, on calcule tout d'abord le projeté du point d'ombre au Sol
		// sur la face f2 augmentée.
		int i = indice;
		// On calcule la droite rayon du Soleil permettant la création du point
		// de l'ombre au sol
		Droite rayon = new Droite(this.getCouples()[(i + 1) % 2].getPointAir(), faceOmbre
				.getCouples()[(i + 1) % 2].getPointAir());
		// On calcule la droite au sol entre l'ombre au sol et le point de f
		// correspondant
		Droite dSol = new Droite(this.getCouples()[(i + 1) % 2].getPointSol(), faceOmbre
				.getCouples()[(i + 1) % 2].getPointAir());
		// On calcule la droite du sol de f2
		Droite dSolF2 = new Droite(f2.getCouples()[0].getPointSol(), f2.getCouples()[1]
				.getPointSol());
		// On calcule l'intersection entre dSol et dSolF2
		Point pointVirtuelBas = dSol.intersection(dSolF2);
		// On calcule alors son projeté sur le rayon du soleil
		Point pointVirtuel = rayon.calculY(pointVirtuelBas.getX());
		// On calcule la droite reliant le point virtuel sur le mur et l'ombre
		// sur le mur
		Droite ligneOmbreMur = new Droite(pointVirtuel, pointOmbreF2.getPointAir());

		// Ceci nous permet alors de calculer le dernier point de l'ombre sur le
		// mur! On a alors deux cas:
		// 1er cas: l'ombre sur le mur est "en bas" ie proche de nous.
		// 2eme cas: elle est "en haut" ie loin de nous.
		// Si l'indice de l'ombre sur le mur est l'indice du point le plus
		// proche de f alors l'ombre est en haut
		// Sinon elle est en bas!

		int j = 0;

		if (i == this.getPlusProche()) {
			// Alors l'ombre sur le mur est "en haut"
			j = (f2.getPlusProche() + 1) % 2;
		} else { // Alors l'ombre sur le mur est "en bas"
			j = f2.getPlusProche();
		}

		Point dernierPointMur = ligneOmbreMur.calculY(f2.getCouples()[j].getPointAir().getX());
		// On doit à présent calculer son équivalent sur F:
		// On calcule le rayon du Soleil pour ce point:
		Droite rayonDernierPoint = new Droite(coupleSoleil.getPointAir(), dernierPointMur);
		Droite batimentFHaut = this.getTopLine();
		Droite batimentFBas = this.getBottomLine();
		Point departHautDernierPointMur = batimentFHaut.intersection(rayonDernierPoint);
		Point departBasDernierPointMur = batimentFBas.calculY((int) departHautDernierPointMur
				.getX());
		// On peut alors rajouter la géométrie à l'ombre

		// Face sur le sol devant le mur:
		ShadowDrawingFace faceSol1 = new ShadowDrawingFace(new Couple(f2.getCouples()[j].getPointSol(),
				departBasDernierPointMur), new Couple(pointOmbreF2.getPointSol(), this
				.getCouples()[i].getPointSol()), false);

		// Face sur le mur:
		ShadowDrawingFace ombreMur = new ShadowDrawingFace(
				new Couple(pointOmbreF2.getPointAir(), pointOmbreF2.getPointSol()), new Couple(
						dernierPointMur, f2.getCouples()[j].getPointSol()), true);

		// Face sur le sol plus loin que le mur:
		// On ne l'affiche entière que si l'ombre est devant le batiment
		// Sinon on n'en affiche qu'une partie
		ShadowDrawingFace faceSol2;
		if (j == (f2.getPlusProche())) {
			faceSol2 = new ShadowDrawingFace(new Couple(f2.getCouples()[j].getPointSol(),
					departBasDernierPointMur), new Couple(faceOmbre.getCouples()[(i + 1) % 2]
					.getPointAir(), faceOmbre.getCouples()[(i + 1) % 2].getPointSol()), false);
		} else {
			// Il y a deux possibilités:
			// soit la droite entre l'ombre au sol du haut et le coté de f le
			// plus éloigné croise le coté de f2 le plus éloigné
			// soit c'est la ligne d'ombre au sol qui le croise!

			// On calcule l'intersection entre le rayon au sol et le cot� du
			// haut du batiment
			Segment droiteSolHaute = new Segment(faceOmbre.couples[(i + 1) % 2].getPointAir(),
					faceOmbre.couples[(i + 1) % 2].getPointSol());
			Point intersection1 = droiteSolHaute
					.calculY(f2.couples[(f2.getPlusProche() + 1) % 2].getPointSol().getX());

			if ((intersection1.getX() > droiteSolHaute.xmin) && (intersection1.getX() < droiteSolHaute.xmax)) {
				// Alors on est dans le premier cas
				faceSol2 = new ShadowDrawingFace(new Couple(intersection1,
						this.couples[(this.getPlusProche() + 1) % 2].getPointSol()), new Couple(f2
						.getCouples()[j].getPointSol(), departBasDernierPointMur), false);
			} else {
				Droite ombreSol = faceOmbre.getTopLine();
				Point intersection2 = ombreSol
						.calculY(f2.couples[(f2.getPlusProche() + 1) % 2].getPointSol().getX());
				// on calcule le point de d�part de cette ombre
				Droite rayonIntersection = new Droite(coupleSoleil.getPointSol(), intersection2);
				Point pointDepartIntersection = rayonIntersection.intersection(batimentFBas);
				
				faceSol2 = new ShadowDrawingFace(new Couple(faceOmbre.couples[(i + 1) % 2].getPointAir(),
						faceOmbre.couples[(i + 1) % 2].getPointSol()), new Couple(intersection2,
						pointDepartIntersection), false);
				
				Couple couple1 = new Couple(intersection2, pointDepartIntersection);
				Couple couple2 = new Couple(f2.getCouples()[j].getPointSol(), departBasDernierPointMur);
				ombre.add(new ShadowDrawingFace(couple1, couple2, false));
			}

		}

		ombre.add(faceSol2);
		ombre.add(faceSol1);
		ombre.add(ombreMur);
	}

	/**
	 * Méthode zeroPoints Cette méthode s'applique lorsque les points extrémaux
	 * des faces n'ont pas d'ombre sur f2 mais lorsque certains autres points en
	 * ont. Il nous faut alors pour entrée: - faceOmbre - f2 - la géométrie de
	 * l'ombre - coupleSoleil Cette méthode ne retourne rien à part une
	 * modification de la géométrie de l'ombre
	 */
	public void zeroPoints(ShadowDrawingFace faceOmbre, ShadowDrawingFace f2,
			List<ShadowDrawingFace> ombre, Couple coupleSoleil) {
		// On renomme les points dont nous aurons besoin
		Point pointOmbreHaut = faceOmbre.couples[(this.getPlusProche() + 1) % 2].getPointAir();

		Point pointOmbreBas = faceOmbre.couples[this.getPlusProche()].getPointAir();

		Droite ombreSol = new Droite(pointOmbreHaut, pointOmbreBas);

		Droite f2Sol = f2.getBottomLine();

		// On regarde maintenant si ces intersections ce situe bien dans le
		// segment ombreSol
		// Il suffit de ne tester qu'un point!
		if ((pointOmbreHaut.getY() < f2Sol.calculY(pointOmbreHaut.getX()).getY())
				&& (pointOmbreBas.getY() < f2Sol.calculY(pointOmbreBas.getX()).getY())) {
			// Dans ce cas, on a bien l'ombre sur le batiment
			// On calcule alors les points ayant leur ombre sur ce batiment!

			Droite rayonSolHaut = new Droite(coupleSoleil.getPointSol(),
					f2.couples[(f2.getPlusProche() + 1) % 2].getPointSol());
			Point pointSolOmbreHaut = rayonSolHaut.intersection(ombreSol);

			Droite rayonSolBas = new Droite(coupleSoleil.getPointSol(),
					f2.couples[f2.getPlusProche()].getPointSol());
			Point pointSolOmbreBas = rayonSolBas.intersection(ombreSol);

			// On calcule alors leur équivalent sur le batiment F2:
			Point pointF2SolHaut = f2.couples[(f2.getPlusProche() + 1) % 2].getPointSol();
			Point pointF2SolBas = f2.couples[f2.getPlusProche()].getPointSol();

			Droite fSol = this.getBottomLine();
			Point departSolHaut = fSol.intersection(rayonSolHaut);
			Point departSolBas = fSol.intersection(rayonSolBas);

			Droite fHaut = this.getTopLine();
			Point departHaut = fHaut.calculY(departSolHaut.getX());
			Point departBas = fHaut.calculY(departSolBas.getX());
			
			Droite rayonHaut = new Droite(departHaut, pointSolOmbreHaut);
			Droite rayonBas = new Droite(departBas, pointSolOmbreBas);

			Point pointF2MurHaut = rayonHaut
					.calculY(f2.couples[(f2.getPlusProche() + 1) % 2].getPointSol().getX());
			Point pointF2MurBas = rayonBas.calculY(f2.couples[f2.getPlusProche()].getPointSol().getX());

			// On peut alors tracer les différentes faces
			ShadowDrawingFace face1 = new ShadowDrawingFace(new Couple(pointF2SolHaut, departSolHaut), new Couple(pointF2SolBas, departSolBas), false);

			ShadowDrawingFace face2 = new ShadowDrawingFace(new Couple(pointF2MurHaut, pointF2SolHaut), new Couple(pointF2MurBas, pointF2SolBas), true);

			ShadowDrawingFace face3 = new ShadowDrawingFace(new Couple(pointF2SolBas, departSolBas), new Couple(faceOmbre.couples[this.getPlusProche()].getPointAir(),
					this.couples[this.getPlusProche()].getPointSol()), false);

			ShadowDrawingFace face4;
			// Il y a deux possibilités:
			// soit la droite entre l'ombre au sol du haut et le coté de f le
			// plus éloigné croise le coté de f2 le plus éloigné
			// soit c'est la ligne d'ombre au sol qui le croise!

			Segment droiteSolHaute = new Segment(
					this.couples[(this.getPlusProche() + 1) % 2].getPointSol(), pointOmbreHaut);
			Point intersection1 = droiteSolHaute
					.calculY(f2.couples[(f2.getPlusProche() + 1) % 2].getPointSol().getX());

			if ((intersection1.getX() > droiteSolHaute.xmin) && (intersection1.getX() < droiteSolHaute.xmax)) {
				// Alors on est dans le premier cas
				face4 = new ShadowDrawingFace(
						new Couple(intersection1, this.couples[(this.getPlusProche() + 1) % 2].getPointSol()),
						new Couple(pointF2SolHaut, departSolHaut),
						false);
			} else {
				Point intersection2 = ombreSol
						.calculY(f2.couples[(f2.getPlusProche() + 1) % 2].getPointSol().getX());
				// on calcule le point de d�part de cette ombre
				Droite rayonIntersection = new Droite(coupleSoleil.getPointSol(), intersection2);
				Point pointDepartIntersection = rayonIntersection.intersection(fSol);
				face4 = new ShadowDrawingFace(
						new Couple(pointOmbreHaut, this.couples[(this.getPlusProche() + 1) % 2].getPointSol()),
						new Couple(intersection2, pointDepartIntersection),
						false);

				ombre.add(new ShadowDrawingFace(
						new Couple(intersection2, pointDepartIntersection),
						new Couple(pointF2SolHaut, departSolHaut),
						false));
			}

			ombre.add(face4);
			ombre.add(face3);
			ombre.add(face2);
			ombre.add(face1);

		} else {

			ombre.add(faceOmbre);
		}

	}
}
