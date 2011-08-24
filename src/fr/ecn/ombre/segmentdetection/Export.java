package fr.ecn.ombre.segmentdetection;

/**
 * Class implementing the segment export.
 *
 * @author Leo COLLET, Cedric TELEGONE, Ecole Centrale Nantes
 * @version 1.0
 * @deprecated
 */

public class Export {
	
	/**
	 * Attributes
	 */
	protected String XMLpath;

	/**
	 * Default no-args constructor
	 */
	public Export(){
		
	}
	
	/**
	 * Export an ImageSegment to a specified XML file
	 * 
	 * @param image	the ImageSegment to be exported.
	 */
	public static void exportToXML(ImageSegment image, String path){
		/**
		 * TRY to export image, if caught NullPointerException (meaning image.finalSegmentMap not computed yet), compute image.finalSegmentMap and export image.
		 */
		try{
			System.out.println(path);
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}
	}
	
}
