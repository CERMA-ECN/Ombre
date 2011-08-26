package pg.data;

/**
* A class to handle lines in projective geometry
* @author Cedric Telegone, ECN 2010
*
*/

public class Line{
	protected Vector coord;

	/**
	 * create new line from a vector
	 * @param v a vector
	 */

	public Line(Vector v){
		coord=v;
	}

	/**
	 * create new line from 3 doubles
	 *
	 */
	public Line(double a, double b, double c) {
		coord=new Vector(a,b,c);
	}

	/**
	 * tell if the line contains a Point
	 * @param p the Point to test
	 * @return
	 */
	public boolean contains(Point p){
		return p.liesOn(this);
	}


	/**
	 * tell if a line is equal to a second one
	 * @param l the second line
	 * @return
	 */
	 public boolean equals(Line l){
		return coord.equals(l.getVec());
	}

	 /**
	  * get the vector of coordinates
	  *
	  */

	public Vector getVec(){
		return coord;
	}
	/**
	 * get the intersection Point of the line with a second one
	 * @param l the second line
	 * @return Point intersection point
	 */
	public Point intersect(Line l){
		Point result=new Point(coord.cross(l.getVec()));
		result.normalize();
		return result;
	}
	/**
	 * get the intersection Point of the line with a second one
	 * @param l the second line
	 * @return Point intersection point
	 */

	public Point cross(Line l){
		return this.intersect(l);
	}

	/**
	 * print coordinates
	 *
	 */

	public void print(){
		coord.print();
	}



}
