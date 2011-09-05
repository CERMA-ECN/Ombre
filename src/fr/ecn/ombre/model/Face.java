package fr.ecn.ombre.model;

public class Face {
	
	protected Point[] points;

	public Face(Point p1, Point p2, Point p3, Point p4) {
		Point[] points = {p1, p2, p3, p4};
		this.points = points;
	}

	/**
	 * @return the points
	 */
	public Point[] getPoints() {
		return points;
	}

	/**
	 * @param points the points to set
	 */
	public void setPoints(Point[] points) {
		this.points = points;
	}
}
