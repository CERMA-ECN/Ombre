package fr.ecn.ombre.core.model;

/**
 * 
 * @author Claire Cervera
 */
public class Segment extends Droite {

	public double xmin;
	public double xmax;
	public double ymax;
	public double ymin;

	public Segment(Point P1, Point P2) {
		super(P1, P2);
		xmin = Math.min(P1.getX(), P2.getX());
		xmax = Math.max(P1.getX(), P2.getX());
		ymin = Math.min(P1.getY(), P2.getY());
		ymax = Math.max(P1.getY(), P2.getY());
	}
}
