package fr.ecn.ombre.model;

/**
 * 
 * @author Claire Cervera
 * @author Julien Bardonnet
 */
public class Droite {

	// =============================================================================//
	// Attributs:
	// on représente les droites non verticales par l'equation y=ax+b,
	// les attributs d'une droite seront donc les coefficients a et b.
	// =============================================================================//
	// POUR L'INSTANT CES ATTRIBUTS SONT PUBLICS POUR NE PAS AVOIR BESOIN DE
	// CREER DES METHODES POUR Y ACCEDER OU LES MODIFIER
	public double a;
	public double b;

	// =============================================================================//
	// Constructeurs
	// =============================================================================//
	/**
	 * Construction d'une droite a partir de deux points
	 * 
	 * @param P1
	 * @param P2
	 */
	public Droite(Point P1, Point P2) {
		this.a = (double) ((int) P1.getY() - (int) P2.getY()) / ((int) P1.getX() - (int) P2.getX());
		this.b = (double) ((int) P1.getY() - this.a * (int) P1.getX());
	}

	/**
	 * Constructeur de base: droite horizontale passant par (0,0).
	 */
	public Droite() {
		this.a = 0;
		this.b = 0;
	}

	/**
	 * Constructeur a partir des coefficients a et b :
	 */
	public Droite(double a, double b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Calcul de y l'ordonné d'un point appartenant à la droite et d'abscisse x.
	 * Retourne le point obtenu
	 * 
	 * @param x
	 * @return le point
	 */
	public Point calculY(double x) {
		double y = this.a * x + this.b;
		return new Point(x, y);
	}

	/**
	 * Calcul de x l'abscisse d'un point appartenant à la droite et d'ordonnée
	 * y. Retourne le point obtenu
	 * 
	 * @param y
	 * @return le point
	 */

	public Point calculX(double y) {
		double x = 0;
		if (this.a != 0) {
			x = 1 / (this.a) * (y - this.b);
		}
		// Dans le cas ou a=0, on a une droite horizontale,
		// on peut alors avoir n'importe quelle valeur de x, on choisit
		// arbitrairement 0.
		return new Point(x, y);
	}

	/**
	 * Methode de calcul du point d'intersection de deux droites ( cas ou 1
	 * droite est verticale manquant, de plus: on suppose que les droites ne
	 * sont pas parall�les)
	 */
	public Point intersection(Droite D2) {
		double x = (D2.b - this.b) / (this.a - D2.a);
		double y = this.a * x + this.b;
		return new Point(x, y);
	}

	/*
	 * public void pointSurDroite(Point p){ if
	 * (p.getX()*this.a+this.b==p.getY()){
	 * System.out.println("le point appartient bien � la droite"); } else {
	 * System.out.println("le point n'appartient pas � cette droite"); } }
	 */
}
