/**
 * 
 */
package fr.ecn.ombre.model;

import java.io.Serializable;

/**
 * @author jerome
 *
 */
public class Coordinate implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String coordinate;
	protected String ref;
	
	/**
	 * @param coordinate
	 * @param ref
	 */
	public Coordinate(String coordinate, String ref) {
		super();
		this.coordinate = coordinate;
		this.ref = ref;
	}

	/**
	 * @return the coordinate
	 */
	public String getCoordinate() {
		return coordinate;
	}

	/**
	 * @param coordinate the coordinate to set
	 */
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	/**
	 * @return the ref
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * @param ref the ref to set
	 */
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	/**
	 * Return the coordinate as a double
	 * @return
	 */
	public double toDouble() {
		String[] strArray = this.coordinate.split("[\"']");
		double d = Double.parseDouble(strArray[0])+Double.parseDouble(strArray[1])/60+Double.parseDouble(strArray[2])/3600;
		if (this.ref.equals("W") || this.ref.equals("S")) {
			d = -d;
		}
		return d;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return coordinate + " " + ref;
	}
}
