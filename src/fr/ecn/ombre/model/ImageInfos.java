package fr.ecn.ombre.model;

import java.io.Serializable;

public class ImageInfos implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String path = null;

	protected Double latitude    = null;
	protected Double longitude   = null;
	protected Double orientation = null;
	
	public ImageInfos(String path) {
		super();
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}

	public Double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(Double longitude) {
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
