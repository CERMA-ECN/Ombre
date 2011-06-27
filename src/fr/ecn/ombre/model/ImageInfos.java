package fr.ecn.ombre.model;

public class ImageInfos {
	protected String path = null;

	protected Long latitude    = null;
	protected Long longitude   = null;
	protected Long orientation = null;
	
	public ImageInfos(String path) {
		super();
		this.path = path;
	}
	
	public Long getLatitude() {
		return latitude;
	}
	
	public void setLatitude(Long latitude) {
		this.latitude = latitude;
	}
	
	public Long getLongitude() {
		return longitude;
	}
	
	public void setLongitude(Long longitude) {
		this.longitude = longitude;
	}
	
	public Long getOrientation() {
		return orientation;
	}
	
	public void setOrientation(Long orientation) {
		this.orientation = orientation;
	}
}
