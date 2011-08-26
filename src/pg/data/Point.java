package pg.data;

import Jama.Matrix;

/**
 * A class to handle Points in projective geometry
 * 
 * @author Cedric Telegone, ECN 2010
 * 
 */
public class Point {
	protected Vector coord;
	protected boolean drawable = false;
	protected double mag;

	/**
	 * create a new Point from a vector
	 * 
	 * @param v
	 *            vector of coordinates
	 */
	public Point(Vector v) {
		this.coord = v;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public Point(double x, double y) {
		// construction de la repr�sentation homog�ne d'un point � partir de sa
		// repr�sentation inhomog�ne
		coord = new Vector(x, y);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Point(double x, double y, double z) {
		coord = new Vector(x, y, z);
	}

	/**
	 * 
	 * @return
	 */

	public Vector getVec() {
		return coord;
	}

	/**
	 * allow a Point to be drawn
	 */

	public void drawable() {
		drawable = true;
	}

	/**
	 * avoid a Point to being drawn
	 */
	public void notDrawable() {
		drawable = false;
	}

	/**
	 * tell if a Point is going to be drawn
	 * 
	 * @return
	 */

	public boolean isDrawable() {
		return drawable;
	}

	/**
	 * 
	 * @return
	 */
	public double getX() {
		return coord.getX();
	}

	/**
	 * 
	 * @return
	 */
	public double getY() {
		return coord.getY();
	}

	/**
	 * 
	 * @return
	 */
	public double getZ() {
		return coord.getZ();
	}

	/**
	 * normalize the homogeneous coordinates (z coordinates = 1)
	 */
	public void normalize() {
		coord = coord.normalize();
	}

	/**
	 * tell if the Point belongs to a specified line
	 * 
	 * @param l
	 *            the line that may contain the Point
	 * 
	 */
	public boolean liesOn(Line l) {
		if (coord.scalar(l.getVec()) == 0)
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param s
	 * @param t
	 * @return
	 */
	public boolean aligned(SegmentPG s, double t) {
		if (Math.abs(angle(s)) > 1 - t) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 
	 * @param s
	 * @return
	 */

	public double angle(SegmentPG s) {
		double xa = coord.getX() - s.getP1().getX();
		double ya = coord.getY() - s.getP1().getY();
		double xb = s.getP2().getX() - s.getP1().getX();
		double yb = s.getP2().getY() - s.getP1().getY();
		double a_scalaire_b = xa * xb + ya * yb;
		double norme_a = Math.sqrt(xa * xa + ya * ya);
		double norme_b = Math.sqrt(xb * xb + yb * yb);
		double angle = (a_scalaire_b / norme_a) / norme_b;

		return angle;

	}

	/**
	 * get a Line from 2 points (current point and parameter point)
	 * 
	 * @param p
	 *            second point
	 * @return l computed line
	 */
	public Line cross(Point p) {
		return new Line(coord.normalize().cross(p.getVec().normalize()));
	}

	/**
	 * get the distance from a point
	 * 
	 */
	public double distance(Point p) {
		return coord.distance(p.getVec());
	}

	/***
	 * print on screen coordinates
	 */
	public void print() {
		coord.print();
	}

	/**
	 * Get the image of the point through an homography
	 * 
	 * @param h
	 *            the homography
	 * @return the image point
	 */
	public Point homography(Homography h) {
		double[][] arrayH = h.getH().getArray();
		Vector l1 = new Vector(arrayH[0][0], arrayH[0][1], arrayH[0][2]);
		Vector l2 = new Vector(arrayH[1][0], arrayH[1][1], arrayH[1][2]);
		Vector l3 = new Vector(arrayH[2][0], arrayH[2][1], arrayH[2][2]);
		double x1 = l1.scalar(coord);
		double x2 = l2.scalar(coord);
		double x3 = l3.scalar(coord);

		return new Point(x1, x2, x3);
	}

	/**
	 * 
	 * A Matrix between two point, specified by Zisserman in
	 * "Multiple view geometry in computer vision"
	 * 
	 * @param p
	 * @return
	 */

	public Matrix A(Point p) {

		// Le point courant est x, p est le point x'
		double[][] m = new double[2][9];

		m[0][0] = 0;
		m[0][1] = 0;
		m[0][2] = 0;
		m[0][3] = -p.getZ() * this.getX();
		m[0][4] = -p.getZ() * this.getY();
		m[0][5] = -p.getZ() * this.getZ();
		m[0][6] = p.getY() * this.getX();
		m[0][7] = p.getY() * this.getY();
		m[0][8] = p.getY() * this.getZ();

		m[1][0] = p.getZ() * this.getX();
		m[1][1] = p.getZ() * this.getY();
		m[1][2] = p.getZ() * this.getZ();
		m[1][3] = 0;
		m[1][4] = 0;
		m[1][5] = 0;
		m[1][6] = -p.getX() * this.getX();
		m[1][7] = -p.getX() * this.getY();
		m[1][8] = -p.getX() * this.getZ();

		return new Matrix(m);

	}

	/**
	 * transform into a pixel
	 * 
	 * @return the pixel
	 */
	public Pixel toPixel() {
		normalize();
		// return new
		// Pixel((int)(Math.rint(coord.getX())),(int)(Math.rint(coord.getY())));
		Pixel p = new Pixel((int) coord.getX(), (int) coord.getY());
		return p;
	}

}
