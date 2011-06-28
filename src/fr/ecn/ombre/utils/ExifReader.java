package fr.ecn.ombre.utils;

import java.io.File;

import android.util.Log;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.GpsDescriptor;
import com.drew.metadata.exif.GpsDirectory;

import fr.ecn.ombre.model.ImageInfos;

/**
 * Helper class for reading Exif data from a JPEG file
 *
 */
public class ExifReader {
	
	protected ImageInfos image;
	
	protected GpsDescriptor gpsDescriptor;
	
	/**
	 * Creates a new ExifReader for an image
	 * 
	 * @param image The image for witch we want to read the exif
	 * @throws JpegProcessingException
	 */
	public ExifReader(ImageInfos image) throws JpegProcessingException {
		this.image = image;
		
		//Create a GpsDescriptor
		Metadata metadata = JpegMetadataReader.readMetadata(new File(this.image.getPath()));
		
		Directory gpsDirectory = metadata.getDirectory(GpsDirectory.class);
		
		this.gpsDescriptor = new GpsDescriptor(gpsDirectory);
	}
	
	/**
	 * Convert GPS coordinates from hour format (xx"xx'xx.xx) to the decimal one xx.xx
	 * 
	 * @param degree
	 * @return
	 */
	public double convertHourToDecimal(String degree) {
		// Select the degrees, the minutes and the seconds and put it in an array
		String[] strArray=degree.split("[\"']");
		// Sum the terms, converting it into decimal
		return Double.parseDouble(strArray[0])+Double.parseDouble(strArray[1])/60+Double.parseDouble(strArray[2])/3600;
	}
	
	/**
	 * @return the absolute longitude (can be negative) from the Exif of a JPEG picture
	 * @throws MetadataException  
	 */
	public double getLongitude() throws MetadataException {
		// Get the longitude data, in degrees, minutes and seconds and convert it into decimal
		Double longitude = this.convertHourToDecimal(this.gpsDescriptor.getGpsLongitudeDescription());
		
		// All the numbers will be positive, so get the longitude reference, and if it's oriented west "W", return the opposite
		if (getLongitudeRef().equals("W")) {
			longitude =- longitude;
		}
		return longitude;
	}
	
	/**
	 * @return the longitude orientation (W or E)
	 * @throws MetadataException 
	 */
	public String getLongitudeRef() throws MetadataException {
		// Get the GPS longitude reference
		String longitudeRef = this.gpsDescriptor.getDescription(GpsDirectory.TAG_GPS_LONGITUDE_REF);
		return longitudeRef;
	}
	
	/**
	 * @return the absolute latitude (can be negative) from the Exif of a JPEG picture
	 * @throws MetadataException  
	 */
	public double getLatitude() throws MetadataException {
		// Get the latitude data, in degrees, minutes and seconds and convert it into decimal
		Double latitude = this.convertHourToDecimal(this.gpsDescriptor.getGpsLatitudeDescription());
		// All the numbers will be positive, so get the latitude reference, and if it's oriented south "S", return the opposite
		if (getLatitudeRef().equals("S")) {
			latitude =- latitude;
		}
		return latitude;
	}
	
	/**
	 * @return the latitude orientation (N or S)
	 * @throws MetadataException 
	 */
	public String getLatitudeRef() throws MetadataException {
		// Get the GPS latitude reference
		String latitudeRef = this.gpsDescriptor.getDescription(GpsDirectory.TAG_GPS_LATITUDE_REF);
		return latitudeRef;
	}
	
	/**
	 * Get the latitude and longitude from the Exif and save them in the ImageInfos object.
	 * 
	 * @param image
	 */
	public static void readExif(ImageInfos image) {
		try {
			ExifReader reader = new ExifReader(image);
			
			try {
				image.setLatitude(reader.getLatitude());
			} catch (MetadataException e) {
				// Latitude can't be read
				Log.w("Ombre", e);
			}
			
			try {
				image.setLongitude(reader.getLongitude());
			} catch (MetadataException e) {
				// Logitude can't be read
				Log.w("Ombre", e);
			}
		} catch (JpegProcessingException e) {
			//Exif can't be read so just log the exception
			Log.w("Ombre", e);
		}
	}
}