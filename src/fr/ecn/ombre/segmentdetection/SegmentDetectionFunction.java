package fr.ecn.ombre.segmentdetection;

//import fr.irstv.kmeans.DataGroup;

import java.io.*;
import java.util.*;

import fr.irstv.kmeans.DataGroup;

import jjil.core.Gray8Image;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * @author Leo COLLET, Cedric TELEGONE, Ecole Centrale Nantes, 2010
 *	taken from App.java - used in MainMain.java
 */
public class SegmentDetectionFunction {
	
	public HashMap<Integer, Vector<Segment>> segmentsList;
	public Bitmap bitmap;
	/**
	 * Detects the segment and computes the groups.
	 * @param path the path to the image
	 * @param differentColours for displaying different colour for each group
	 */
	public SegmentDetectionFunction(String path, boolean differentColours) {

		// creating the color map
		int[] colorMap = {
				Color.RED,
				Color.BLUE,
				Color.GREEN,
				Color.rgb(255, 200, 0),
				Color.YELLOW,
				Color.MAGENTA,
				Color.CYAN,
				Color.WHITE,
		};

		/** si besoin de plusieurs images a la fois */
		/*Vector<String> theImages = FilesFinder.findFiles(path);
		for (int i=0; i<theImages.size(); i++){
			System.out.println(path + theImages.get(i));
			ImageSegment is = new ImageSegment(path + theImages.get(i));
			Utils.getImageFromSegmentMap(is.baseImage, is.getLargeConnectedEdges(false), colorMap);
		}*/

		try {
			ImageSegment is = new ImageSegment(path);
			is.getLargeConnectedEdges(false, 8);
			segmentsList = is.getFinalSegmentMap();
			
			// displaying the image
			if (differentColours) {
				this.bitmap = Utils.getImageFromImageSegment(is, colorMap);
			} else {
				this.bitmap = Utils.getImageFromImageSegment(is);
			}
		} catch (NullPointerException fnfe){
			System.out.println("The file you tried to process cannot be reached.");
			fnfe.printStackTrace();
		}

	}

	public static void exportToXML(ImageSegment is, String path){
		PrintWriter file;

	    try{
	    	file =  new PrintWriter(new BufferedWriter(new FileWriter("XML/"+path + ".xml")));

		    file.println("<?xml version=\"1.0\" ?>");

		    file.println("<root>");
		    	file.println("\t" + "<Epsilon Epsilon=\"40\"/>");
		    	file.println("\t \t" + "<LES-SEGMENTS>");
		    	for (Map.Entry<Integer, Vector<Segment>> ent : is.finalSegmentMap.entrySet()){
					for (int i=0; i<ent.getValue().size(); i++){
						file.print("\t \t \t" + "<Coordonnees Segments-xp1=\"" + ent.getValue().get(i).getStartPoint().getX() + "\" ");
						file.print("Segments-yp1=\"" + ent.getValue().get(i).getStartPoint().getY() + "\" ");
						file.print("Segments-xp2=\"" + ent.getValue().get(i).getEndPoint().getX() + "\" ");
						file.println("Segments-yp2=\"" + ent.getValue().get(i).getEndPoint().getY() + "\" />");
					}
				}
		    	file.println("\t \t" + "</LES-SEGMENTS>");
		    file.println("</root>");

		    file.close();
	    } catch (IOException ioe){
	    	System.out.println("Cannot write on file " + path + ".xml");
	    	ioe.printStackTrace();
	    } catch (NullPointerException npe){
	    	System.out.println("Cannot access to image segment map. Must be computed.");
	    	npe.printStackTrace();
	    }
	}
	
	/**
	 * Display ALL segments groups and their vanishing points by color
	 * @param file
	 * @param segmentMap
	 * @param groupsChosen
	 */
	public Bitmap segmentDisplayFunction(String file, HashMap<Integer, Vector<Segment>> segmentMap, DataGroup[] dg) {
		String path = file;

		// creating the color map
		int[] colorMap = {
				Color.RED,
				Color.BLUE,
				Color.GREEN,
				Color.rgb(255, 200, 0),
				Color.YELLOW,
				Color.MAGENTA,
				Color.CYAN,
				Color.WHITE,
		};
		
		ImageSegment is = new ImageSegment(path);
		Gray8Image is2 = is.baseImage;
		// displaying the image
		return Utils.getImageFromSegmentMap(is2, segmentMap, colorMap, dg);
	}
	
	/**
	 * Display ONLY CHOSEN segments groups and their vanishing points by color
	 * @param file
	 * @param segmentMap
	 * @param groupsChosen
	 */
	public void segmentDisplayFunction(String file, HashMap<Integer, Vector<Segment>> segmentMap, ArrayList<Integer> groupsChosen) {
		String path = file;

		// creating the color map
		int[] colorMap = {
				Color.RED,
				Color.BLUE,
				Color.GREEN,
				Color.rgb(255, 200, 0),
				Color.YELLOW,
				Color.MAGENTA,
				Color.CYAN,
				Color.WHITE,
		};
		
		ImageSegment is = new ImageSegment(path);
		Gray8Image is2 = is.baseImage;
		// displaying the image
		Utils.getImageFromSegmentMap(is2, segmentMap, colorMap, groupsChosen);
	}

}
