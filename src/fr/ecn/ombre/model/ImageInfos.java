package fr.ecn.ombre.model;

import java.io.Serializable;

public class ImageInfos implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String path = null;

	protected Coordinate latitude  = null;
	protected Coordinate longitude = null;
	
	protected Double orientation = null;
	
	public ImageInfos(String path) {
		super();
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	/**
	 * @return the latitude
	 */
	public Coordinate getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(Coordinate latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public Coordinate getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(Coordinate longitude) {
		this.longitude = longitude;
	}

	public Double getOrientation() {
		return orientation;
	}
	
	public void setOrientation(Double orientation) {
		this.orientation = orientation;
	}

	@Override
	public String toString() {
		return "ImageInfos [path=" + path + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", orientation=" + orientation + "]";
	}
}
