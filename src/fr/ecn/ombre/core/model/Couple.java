package fr.ecn.ombre.core.model;

import java.io.Serializable;

import fr.ecn.common.core.geometry.Point;

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

	// Getters et Setters:
	public Point getPointAir() {
		return pointAir;
	}

	public void setPointAir(Point pointAir) {
		this.pointAir = pointAir;
	}

	public void setPointAir(double x, double y) {
		this.pointAir.setX(x);
		this.pointSol.setY(y);
	}

	public Point getPointSol() {
		return pointSol;
	}

	public void setPointSol(Point pointSol) {
		this.pointSol = pointSol;
	}

	public void setPointSol(double x, double y) {
		this.pointSol.setX(x);
		this.pointSol.setY(y);
	}

	// Constructeurs:
	public Couple() {
		this.pointAir = new Point(0, 0);
		this.pointSol = new Point(0, 0);
	}

	public Couple(Point pAir, Point pSol) {
		this.pointAir = pAir;
		this.pointSol = pSol;
	}

	public Couple(double xAir, double yAir, double xSol, double ySol) {
		this.pointAir = new Point(xAir, yAir);
		this.pointSol = new Point(xSol, ySol);
	}

}
