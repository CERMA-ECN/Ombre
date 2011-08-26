package pg.data;

/**
 * A class to handle pixel in projective geometry
 * 
 * @author Cedric Telegone, ECN 2010
 * 
 */
public class Pixel {
	protected int x;
	protected int y;

	/**
	 * create a pixel from his x and y coordinates
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 */
	public Pixel(int x, int y) {
		// construction de la repr�sentation homog�ne d'un point � partir de sa
		// repr�sentation inhomog�ne
		this.x = x;
		this.y = y;
	}

	/**
	 * get an homogeneous vector from the pixel coordinates
	 * 
	 */
	public Vector getVec() {
		return new Vector((double) x, (double) y, 1);
	}

	/**
	 * get x coordinate
	 * 
	 */
	public int getX() {
		return x;
	}

	/**
	 * get y coordinate
	 * 
	 */
	public int getY() {
		return y;
	}

	/**
	 * Transform a pixel into Point
	 * 
	 * @return the transformed Point
	 */
	public Point toPoint() {
		return new Point(this.getVec());

	}

}
