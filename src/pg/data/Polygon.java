package pg.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to handle polygon as a list of points
 * 
 * @author Cedric Telegone, ECN 2010
 * 
 */
public class Polygon {

	protected List<Point> points;

	/**
	 *
	 */
	public Polygon() {
		points = new ArrayList<Point>();
	}

	/**
	 * add a point to the current polygon
	 * 
	 * @param p
	 *            a point
	 */
	public void add(Point p) {
		points.add(p);
	}

	/**
	 * tell if the polygon is allowed to be drawn
	 * 
	 * @return
	 */
	public boolean drawable() {
		boolean result = false;
		if (points.size() > 0) {
			result = true;
			for (int i = 0; i < points.size(); i++) {
				if (!points.get(i).drawable)
					result = false;

			}

		}

		return result;
	}

}
