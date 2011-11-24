package fr.ecn.ombre.core.model;

import fr.ecn.common.core.geometry.Point;

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

	/**
	 * return the intersection of two segments or null if the intersection
	 * doesn't exist
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static Point intersection(Segment s1, Segment s2) {
		// We get the intersection of the two lines
		Point intersection = s1.intersection(s2);

		// is the intersection of the two lines on the segment
		double x = intersection.getX();
		if ((s1.xmin < x) && (x < s1.xmax) && (s2.xmin < x) && (x < s2.xmax)) {
			return intersection;
		} else {
			return null;
		}
	}
}
