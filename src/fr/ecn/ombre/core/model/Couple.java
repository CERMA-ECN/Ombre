package fr.ecn.ombre.core.model;

import java.io.Serializable;

/**
 * 
 * @author Claire Cervera
 * @author Julien Bardonnet
 */
public class Couple implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Attributs: deux points, le premier "en l'air", le deuxième au sol
	protected Point pointAir;
	protected Point pointSol;
	protected int indice; // Il permettra d'établir une correspondance entre
							// points sur mur et points des faces. Utilisation
							// pour les points sur le mur!

	// Getters et Setters:
	public Point getPointAir() {
		return pointAir;
	}

	public void setPointAir(Point pointAir) {
		this.pointAir = pointAir;
	}

	public void setPointAir(int x, int y) {
		this.pointAir.setX(x);
		this.pointSol.setY(y);
	}

	public Point getPointSol() {
		return pointSol;
	}

	public void setPointSol(Point pointSol) {
		this.pointSol = pointSol;
	}

	public void setPointSol(int x, int y) {
		this.pointSol.setX(x);
		this.pointSol.setY(y);
	}

	public int getIndice() {
		return (this.indice);
	}

	public void setIndice(int i) {
		this.indice = i;
	}

	// Constructeurs:
	public Couple() {
		this.pointAir = new Point(0, 0);
		this.pointSol = new Point(0, 0);
		this.indice = 0;
	}

	public Couple(Point pAir, Point pSol) {
		this.pointAir = pAir;
		this.pointSol = pSol;
		this.indice = 0;
	}

	public Couple(int xAir, int yAir, int xSol, int ySol) {
		this.pointAir = new Point(xAir, yAir);
		this.pointSol = new Point(xSol, ySol);
		this.indice = 0;
	}

}
