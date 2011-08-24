package fr.ecn.ombre.segmentdetection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


/**
 *
 */

/**
 * @author Leo COLLET, Cedric TELEGONE, Ecole Centrale Nantes, 2010
 *
 */
public class SegmentDetection {

	public SegmentDetection(){

	}

	public static void run(File file) {
		//String path = "TestImages/DSCN3616.jpg";
		String path = file.getPath();

		/*Vector<String> theImages = FilesFinder.findFiles(path);
		for (int i=0; i<theImages.size(); i++){
			System.out.println(path + theImages.get(i));
			ImageSegment is = new ImageSegment(path + theImages.get(i));
			Utils.getImageFromSegmentMap(is.baseImage, is.getLargeConnectedEdges(false), colorMap);
		}*/

		try {
			ImageSegment is = new ImageSegment(path);
			is.getLargeConnectedEdges(false, 8);
			Utils.getImageFromImageSegment(is);
			String[] splitPath = path.split("/");
			String fileBaseName;
			if (splitPath.length >= 1){
				fileBaseName = splitPath[splitPath.length - 1];
			} else {
				fileBaseName = path;
			}
			String fileBaseNameWithoutExtension = (fileBaseName != null) ? fileBaseName.substring(0,fileBaseName.indexOf('.')) : "";
			exportToXML(is, fileBaseNameWithoutExtension);
		} catch (NullPointerException fnfe){
			System.out.println("The file you tried to process can not be reached.");
			fnfe.printStackTrace();
		}

	}

	public static void exportToXML(ImageSegment is, String path){
		PrintWriter file;

	    try{
	    	file =  new PrintWriter(new BufferedWriter(new FileWriter(path + ".xml")));

		    file.println("<?xml version=\"1.0\" ?>");

		    file.println("<root>");
		    	file.println("\t" + "<Epsilon Epsilon=\"40\"/>");
		    	file.println("\t \t" + "<LES-SEGMENTS>");
		    	for (Iterator<Map.Entry<Integer, Vector<Segment>>> iter = is.finalSegmentMap.entrySet().iterator(); iter.hasNext();){
					Map.Entry<Integer, Vector<Segment>> ent = (Map.Entry<Integer, Vector<Segment>>) iter.next();
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
	    	System.out.println("Can not write on file " + path + ".xml");
	    	ioe.printStackTrace();
	    } catch (NullPointerException npe){
	    	System.out.println("Can not access to image segment map. Must be computed.");
	    	npe.printStackTrace();
	    }

	}

}
