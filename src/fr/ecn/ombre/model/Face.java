package fr.ecn.ombre.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.Shape;

public class Face extends Shape implements Serializable {

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
	public Face(Point p1, Point p2, Point p3, Point p4) {
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
	 * @param couples
	 * @param isBuilding
	 */
	public Face(Couple couple1, Couple couple2, boolean isBuilding) {
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

	@Override
	public void draw(Canvas canvas, Paint paint) {
		Point[] points = this.getPoints();
		
		Path path = new Path();
		path.moveTo((float) points[points.length-1].getX(), (float) points[points.length-1].getY());

		for (int i = 0; i < points.length; i++) {
			Point p = points[i];
			
			path.lineTo((float) p.getX(), (float) p.getY());
		}
		
		canvas.drawPath(path, paint);
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
	private boolean isLeft() {
		int i = this.getPlusProche();
		if (this.couples[i].pointSol.x < this.couples[(i + 1) % 2].pointSol.x) {
			return true;
		} else
			return false;
	}

	/**
	 * Expand face in our direction...
	 */
	public Face expandToStreet(Bitmap image) {
		int i = this.getPlusProche(); // avec deux couples de points par face,
		// i=0 ou 1.
		Droite dSol = new Droite(this.couples[0].getPointSol(), this.couples[1].getPointSol());
		Droite d = new Droite(this.couples[0].getPointAir(), this.couples[1].getPointAir());

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
		Face newFace = new Face(couples[i], newCouple, this.isBuilding);

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
		Droite sol = new Droite(this.couples[0].pointSol, this.couples[1].pointSol);
		// on prend les coordonnées du premier point d'intersection calculé
		// (point projeté sur le sol)
		double x = this.couples[0].pointAir.getX();
		double y = this.couples[0].pointAir.getY();
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

	/**
	 * Méthode de calcul de l'ombre de la face sur le sol.
	 * 
	 * @param coupleSoleil
	 * @return
	 */
	public Face calculOmbreDirect(Couple coupleSoleil) {
		// calcul des points de la face ( hors sol )
		Couple[] couples = new Couple[2];
		for (int i = 0; i < 2; i++) { // rayon
			Droite rayon = new Droite(coupleSoleil.pointAir, this.couples[i].getPointAir());
			// fuyante au sol
			Droite fuyante = new Droite(coupleSoleil.pointSol, this.couples[i].getPointSol());
			// on vérifie que la pente du rayon est plus grande que la pente de
			// la fuyante! sinon, ça fait des choses bizarres...
			if (this.isLeft()) {
				if (rayon.a > fuyante.a) {
					//throw new RuntimeException("Soleil trop bas! Essayez une autre heure...");
				}
			} else {
				if (rayon.a < fuyante.a) {
					//throw new RuntimeException("Soleil trop bas! Essayez une autre heure...");
				}
			}
			// message d'erreur dans la classe CalculOmbre si testPente=false...

			// intersection
			Point pointOmbre = rayon.intersection(fuyante);
			// ajout du point a la face de l'ombre, couplé avec le pt au sol
			// initial correspondant
			couples[i] = new Couple(pointOmbre, this.couples[i].getPointSol());
		}
		return new Face(couples[0], couples[1], false);
	}

	/**
	 * Méthode calculOmbreMur Cette méthode détermine les points ayant leur
	 * ombre sur le mur du bétiment F2. Elle prend pour entrée: - le vecteur des
	 * points sur le mur qui sera modifié - la face des ombres de la face f
	 * initialement - la face f2 Elle ne retourne rien mais modifie le vecteur
	 * des points sur le mur
	 */
	public void calculOmbreMur(List<Couple> vectPointOmbreF2, Face f2, Face faceOmbre) {
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
			Point intersectionSegments = s1.intersection(s2);

			// On regarde si ce point d'intersection appartient bien au segment
			if ((s2.xmin < ((int) intersectionSegments.getX()))
					&& (s2.xmax > (int) intersectionSegments.getX())
					&& (s1.xmin < ((int) intersectionSegments.getX()))
					&& (s1.xmax > (int) intersectionSegments.getX())) {

				// Dans ce cas l'ombre est effectivement derrière la face, on
				// modifie donc le point intersection au vecteur ombre
				// On calcule alors le nouveau point correspondant sur le mur:
				// projeté de l'ombre sur le bâtiment
				Droite rayonSoleil = new Droite(pointOmbre, this.getCouples()[i].getPointAir());

				Point projeteOmbre = rayonSoleil.calculY((int) intersectionSegments.getX());

				// On rentre ce point dans un nouveau vecteur
				Couple ombreBatiment = new Couple(projeteOmbre, intersectionSegments);
				ombreBatiment.setIndice(i);

				// On rajoute ce couple dans le vecteur ombre au sol
				vectPointOmbreF2.add(ombreBatiment);

			}
		}
	}

	/*
	 * M�thode deuxPointsMur Cette m�thode permet de calculer les diff�rentes
	 * faces de l'ombre lorsque toute la face f est projet�e sur la face f2 Elle
	 * prend donc pour entr�e: - le vecteur des points sur le mur - la g�om�trie
	 * de l'ombre Elle ne retourne rien mais modifie la g�om�trie de l'ombre
	 */

	private void deuxPointsMur(List<Couple> vectPointOmbreF2, List<Face> ombre) {
		// On rentre les faces correspondantes à la géométrie de l'ombre

		{// Ombre sur le mur:
			Couple couple1 = new Couple(vectPointOmbreF2.get(0).getPointAir(), vectPointOmbreF2
					.get(0).getPointSol());
			Couple couple2 = new Couple(vectPointOmbreF2.get(1).getPointAir(), vectPointOmbreF2
					.get(1).getPointSol());
			ombre.add(new Face(couple1, couple2, true));
		}

		{// Ombre sur le sol:
			int i = vectPointOmbreF2.get(0).getIndice();

			Couple couple1 = new Couple(vectPointOmbreF2.get(0).getPointSol(), this.getCouples()[i]
					.getPointSol());
			Couple couple2 = new Couple(vectPointOmbreF2.get(1).getPointSol(),
					this.getCouples()[(i + 1) % 2].getPointSol());
			ombre.add(new Face(couple1, couple2, false));
		}
	}

	/*
	 * M�thode premierCas Cette m�thode s'applique dans le cas o� l'ombre n'est
	 * que sur une petite partie du b�timent. Elle prend pour entr�e: - le
	 * vecteur des points sur le mur - la face des ombres initiale - la face f2
	 * - la g�om�trie de l'ombre - le point du Soleil Elle ne retourne rien mais
	 * modifie la g�om�trie de l'ombre en lui rajoutant les faces ainsi
	 * calcul�es
	 */

	private void premierCas(List<Couple> vectPointOmbreF2, Face faceOmbre, Face f2,
			List<Face> ombre, Couple coupleSoleil) {

		// Définissons les points dont nous aurons besoin pour ce programme:
		// Point vraieOmbreMur: coordonnées du point de l'ombre projeté sur le
		// mur si il n'y avait pas le batiment F2
		int i = vectPointOmbreF2.get(0).getIndice();
		Point vraieOmbreMur = faceOmbre.getCouples()[i].getPointAir();

		// Point ombreSol: deuxième point de l'ombre qui lui se trouve au sol
		Point ombreSol = faceOmbre.getCouples()[(i + 1) % 2].getPointAir();
		// Point de l'ombre sur le mur
		Point ombreMur = vectPointOmbreF2.get(0).getPointAir();

		// point de l'ombre sur le mur projeté au sol de f2
		Point ombreMurSol = vectPointOmbreF2.get(0).getPointSol();

		// point du sol de f donnant son ombre sur le mur
		Point departOmbreMurBas = this.getCouples()[i].getPointSol();
		// point du sol de f donnant son ombre sur le sol
		Point departOmbreSolBas = this.getCouples()[(i + 1) % 2].getPointSol();

		// d�finissons certaines droites dont nous aurons besoin par la suite
		Droite droiteF2Sol = new Droite(f2.getCouples()[0].getPointSol(), f2.getCouples()[1]
				.getPointSol());
		Droite batimentFHaut = new Droite(this.getCouples()[0].getPointAir(), this.getCouples()[1]
				.getPointAir());
		Droite batimentFBas = new Droite(this.getCouples()[0].getPointSol(), this.getCouples()[1]
				.getPointSol());

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
		ombre.add(new Face(new Couple(ombreMur, ombreMurSol), new Couple(dernierPointMur,
				dernierPointMur), true));

		// face de l'ombre devant le mur
		ombre.add(new Face(new Couple(ombreMurSol, departOmbreMurBas), new Couple(dernierPointMur,
				pointEquivalentBas), false));

		// face de l'ombre sur le sol
		ombre.add(new Face(new Couple(ombreSol, departOmbreSolBas), new Couple(dernierPointMur, pointEquivalentBas), false));

	}

	/*
	 * Méthode deuxiemeCas Cette méthode s'applique dans le cas o� l'ombre va
	 * jusqu'� un bout de la face, et est oblig�e de retourner sur le Sol car
	 * face termin�e Elle prend pour entr�e: - le vecteur des points sur le mur
	 * - la face des ombres initiale - la face f2 - la g�om�trie de l'ombre - le
	 * point du Soleil Elle ne retourne rien mais modifie la g�om�trie de
	 * l'ombre en lui rajoutant les faces ainsi calcul�es
	 */

	private void deuxiemeCas(List<Couple> vectPointOmbreF2, Face faceOmbre, Face f2,
			List<Face> ombre, Couple coupleSoleil) {

		// On doit tout d'abord trouv� le dernier point d'ombre sur le mur.
		// pour cela, on calcule tout d'abord le projet� du point d'ombre au Sol
		// sur la face f2 augment�e.
		int i = vectPointOmbreF2.get(0).getIndice();
		// On calcule la droite rayon du Soleil permettant la cr�ation du point
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
		// On calcule alors son projet� sur le rayon du soleil
		Point pointVirtuel = rayon.calculY((int) pointVirtuelBas.getX());
		// On calcule la droite reliant le point virtuel sur le mur et l'ombre
		// sur le mur
		Droite ligneOmbreMur = new Droite(pointVirtuel, vectPointOmbreF2.get(0).getPointAir());

		// Ceci nous permet alors de calculer le dernier point de l'ombre sur le
		// mur! On a alors deux cas:
		// 1� cas: l'ombre sur le mur est "en bas" ie proche de nous.
		// 2� cas: elle est "en haut" ie loin de nous.
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

		Point dernierPointMur = ligneOmbreMur.calculY(f2.getCouples()[j].pointAir.x);
		// On doit � pr�sent calculer son �quivalent sur F:
		// On calcule le rayon du Soleil pour ce point:
		Droite rayonDernierPoint = new Droite(coupleSoleil.getPointAir(), dernierPointMur);
		Droite batimentFHaut = new Droite(this.getCouples()[0].getPointAir(), this.getCouples()[1]
				.getPointAir());
		Droite batimentFBas = new Droite(this.getCouples()[0].getPointSol(), this.getCouples()[1]
				.getPointSol());
		Point departHautDernierPointMur = batimentFHaut.intersection(rayonDernierPoint);
		Point departBasDernierPointMur = batimentFBas.calculY((int) departHautDernierPointMur
				.getX());
		// On peut alors rajouter la g�om�trie � l'ombre

		// Face sur le sol devant le mur:
		Face faceSol1 = new Face(new Couple(f2.getCouples()[j].getPointSol(),
				departBasDernierPointMur), new Couple(vectPointOmbreF2.get(0).getPointSol(), this
				.getCouples()[i].getPointSol()), false);

		// Face sur le mur:
		Face ombreMur = new Face(new Couple(vectPointOmbreF2.get(0).getPointAir(), vectPointOmbreF2
				.get(0).getPointSol()), new Couple(dernierPointMur, f2.getCouples()[j]
				.getPointSol()), true);

		// Face sur le sol plus loin que le mur:
		// On ne l'affiche enti�re que si l'ombre est devant le batiment
		// Sinon on n'en affiche qu'une partie
		Face faceSol2;
		if (j == (f2.getPlusProche())) {
			faceSol2 = new Face(new Couple(f2.getCouples()[j].getPointSol(),
					departBasDernierPointMur), new Couple(faceOmbre.getCouples()[(i + 1) % 2]
					.getPointAir(), faceOmbre.getCouples()[(i + 1) % 2].getPointSol()), false);
		} else {
			// Il y a deux possibilités:
			// soit la droite entre l'ombre au sol du haut et le cot� de f le
			// plus �loign� croise le cot� de f2 le plus �loign�
			// soit c'est la ligne d'ombre au sol qui le croise!

			// On calcule l'intersection entre le rayon au sol et le cot� du
			// haut du batiment
			Segment droiteSolHaute = new Segment(faceOmbre.couples[(i + 1) % 2].pointAir,
					faceOmbre.couples[(i + 1) % 2].pointSol);
			Point intersection1 = droiteSolHaute
					.calculY(f2.couples[(f2.getPlusProche() + 1) % 2].pointSol.x);

			if ((intersection1.x > droiteSolHaute.xmin) && (intersection1.x < droiteSolHaute.xmax)) {
				// Alors on est dans le premier cas
				faceSol2 = new Face(new Couple(intersection1,
						this.couples[(this.getPlusProche() + 1) % 2].pointSol), new Couple(f2
						.getCouples()[j].getPointSol(), departBasDernierPointMur), false);
			} else {
				Droite ombreSol = new Droite(faceOmbre.couples[0].pointAir,
						faceOmbre.couples[1].pointAir);
				Point intersection2 = ombreSol
						.calculY(f2.couples[(f2.getPlusProche() + 1) % 2].pointSol.x);
				// on calcule le point de d�part de cette ombre
				Droite rayonIntersection = new Droite(coupleSoleil.pointSol, intersection2);
				Point pointDepartIntersection = rayonIntersection.intersection(batimentFBas);
				
				faceSol2 = new Face(new Couple(faceOmbre.couples[(i + 1) % 2].pointAir,
						faceOmbre.couples[(i + 1) % 2].pointSol), new Couple(intersection2,
						pointDepartIntersection), false);
				
				Couple couple1 = new Couple(intersection2, pointDepartIntersection);
				Couple couple2 = new Couple(f2.getCouples()[j].getPointSol(), departBasDernierPointMur);
				ombre.add(new Face(couple1, couple2, false));
			}

		}

		ombre.add(faceSol2);
		ombre.add(faceSol1);
		ombre.add(ombreMur);
	}

	/*
	 * M�thode zeroPoints Cette m�thode s'applique lorsque les points extr�maux
	 * des faces n'ont pas d'ombre sur f2 mais lorsque certains autres points en
	 * ont. Il nous faut alors pour entr�e: - vectPointOmbreF2 - faceOmbre - f2
	 * - la g�om�trie de l'ombre - coupleSoleil Cette m�thode ne retourne rien �
	 * part une modification de la g�om�trie de l'ombre
	 */

	private void zeroPoints(List<Couple> vectPointOmbreF2, Face faceOmbre, Face f2,
			List<Face> ombre, Couple coupleSoleil) {
		// On renomme les points dont nous aurons besoin
		Point pointOmbreHaut = faceOmbre.couples[(this.getPlusProche() + 1) % 2].pointAir;

		Point pointOmbreBas = faceOmbre.couples[this.getPlusProche()].pointAir;

		Droite ombreSol = new Droite(pointOmbreHaut, pointOmbreBas);

		Droite f2Sol = new Droite(f2.couples[0].pointSol, f2.couples[1].pointSol);

		// On regarde maintenant si ces intersections ce situe bien dans le
		// segment ombreSol
		// Il suffit de ne tester qu'un point!
		if ((pointOmbreHaut.y < f2Sol.calculY(pointOmbreHaut.x).y)
				& (pointOmbreBas.y < f2Sol.calculY(pointOmbreBas.x).y)) {
			// Dans ce cas, on a bien l'ombre sur le batiment
			// On calcule alors les points ayant leur ombre sur ce batiment!

			Droite rayonSolHaut = new Droite(coupleSoleil.pointSol,
					f2.couples[(f2.getPlusProche() + 1) % 2].pointSol);
			Point pointSolOmbreHaut = rayonSolHaut.intersection(ombreSol);

			Droite rayonSolBas = new Droite(coupleSoleil.pointSol,
					f2.couples[f2.getPlusProche()].pointSol);
			Point pointSolOmbreBas = rayonSolBas.intersection(ombreSol);

			// On calcule alors leur �quivalent sur le batiment F2:
			Point pointF2SolHaut = f2.couples[(f2.getPlusProche() + 1) % 2].pointSol;
			Point pointF2SolBas = f2.couples[f2.getPlusProche()].pointSol;

			Droite fSol = new Droite(this.couples[0].pointSol, this.couples[1].pointSol);
			Point departSolHaut = fSol.intersection(rayonSolHaut);
			Point departSolBas = fSol.intersection(rayonSolBas);

			Droite fHaut = new Droite(this.couples[0].pointAir, this.couples[1].pointAir);
			Point departHaut = fHaut.calculY(departSolHaut.x);
			Point departBas = fHaut.calculY(departSolBas.x);

			Droite rayonHaut = new Droite(departHaut, pointSolOmbreHaut);
			Droite rayonBas = new Droite(departBas, pointSolOmbreBas);

			Point pointF2MurHaut = rayonHaut
					.calculY(f2.couples[(f2.getPlusProche() + 1) % 2].pointSol.x);
			Point pointF2MurBas = rayonBas.calculY(f2.couples[f2.getPlusProche()].pointSol.x);

			// On peut alors tracer les différentes faces
			Face face1 = new Face(new Couple(pointF2SolHaut, departSolHaut), new Couple(pointF2SolBas, departSolBas), false);

			Face face2 = new Face(new Couple(pointF2MurHaut, pointF2SolHaut), new Couple(pointF2MurBas, pointF2SolBas), true);

			Face face3 = new Face(new Couple(pointF2SolBas, departSolBas), new Couple(faceOmbre.couples[this.getPlusProche()].pointAir,
					this.couples[this.getPlusProche()].pointSol), false);

			Face face4;
			// Il y a deux possibilit�s:
			// soit la droite entre l'ombre au sol du haut et le cot� de f le
			// plus �loign� croise le cot� de f2 le plus �loign�
			// soit c'est la ligne d'ombre au sol qui le croise!

			Segment droiteSolHaute = new Segment(
					this.couples[(this.getPlusProche() + 1) % 2].pointSol, pointOmbreHaut);
			Point intersection1 = droiteSolHaute
					.calculY(f2.couples[(f2.getPlusProche() + 1) % 2].pointSol.x);

			if ((intersection1.x > droiteSolHaute.xmin) && (intersection1.x < droiteSolHaute.xmax)) {
				// Alors on est dans le premier cas
				face4 = new Face(
						new Couple(intersection1, this.couples[(this.getPlusProche() + 1) % 2].pointSol),
						new Couple(pointF2SolHaut, departSolHaut),
						false);
			} else {
				Point intersection2 = ombreSol
						.calculY(f2.couples[(f2.getPlusProche() + 1) % 2].pointSol.x);
				// on calcule le point de d�part de cette ombre
				Droite rayonIntersection = new Droite(coupleSoleil.pointSol, intersection2);
				Point pointDepartIntersection = rayonIntersection.intersection(fSol);
				face4 = new Face(
						new Couple(pointOmbreHaut, this.couples[(this.getPlusProche() + 1) % 2].pointSol),
						new Couple(intersection2, pointDepartIntersection),
						false);

				ombre.add(new Face(
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

	/*
	 * M�thode determinationOmbre Cette méthode permet de calculer l'ombre dans
	 * le cas o� on souhaite vérifier si elle se cogne sur un batiment ou non
	 * Elle prend pour entrée ce dont on a eu besoin dans les m�thodes du
	 * dessus! Elle ne retourne rien si ce n'est une modification de la
	 * g�om�trie de l'ombre
	 */

	public void determinationOmbreMur(List<Couple> vectPointOmbreF2, Face faceOmbre, Face f2,
			List<Face> ombre, Couple coupleSoleil) {
		int taille = vectPointOmbreF2.size();
		if (taille == 2) {

			this.deuxPointsMur(vectPointOmbreF2, ombre);
		} else if (taille == 1) {
			// Nous distinguons alors deux cas comme décrit auparavant!
			int i = (vectPointOmbreF2.get(0).getIndice() + 1) % 2;
			// Nous déterminons le point de l'ombre au sol
			Point ombreSol = faceOmbre.getCouples()[i].getPointAir();
			// On calcule également l'ordonné du point de la droite F2
			// d'abscisse le point de l'ombre au sol
			Droite droiteF2Sol = new Droite(f2.getCouples()[0].getPointSol(), f2.getCouples()[1]
					.getPointSol());
			double y = droiteF2Sol.calculY(ombreSol.getX()).y;

			if (ombreSol.getY() > y) {
				// 1er cas: l'ombre n'est que sur une partie du batiment

				this.premierCas(vectPointOmbreF2, faceOmbre, f2, ombre, coupleSoleil);
			} else {
				// 2eme cas: l'ombre est sur une grande partie du batiment et va
				// jusqu'� un cot� du batiment

				this.deuxiemeCas(vectPointOmbreF2, faceOmbre, f2, ombre, coupleSoleil);
			}

		} else if (taille == 0) {
			this.zeroPoints(vectPointOmbreF2, faceOmbre, f2, ombre, coupleSoleil);
		}

	}
}
