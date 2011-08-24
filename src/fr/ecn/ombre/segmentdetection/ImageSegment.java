package fr.ecn.ombre.segmentdetection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import jjil.android.RgbImageAndroid;
import jjil.core.Gray8Image;
import jjil.core.Image;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import fr.ecn.ombre.image.ByteImage;
import fr.ecn.ombre.image.FloatImage;
import fr.ecn.ombre.utils.ImageUtils;

/**
 * Class implementing the extraction of the main edges of an image.
 *
 * @author Leo COLLET, Cedric TELEGONE, Ecole Centrale Nantes
 * @version 1.0
 */

public class ImageSegment {

	/**
	 * Attributes
	 */
	protected Gray8Image baseImage;
	protected int MAX_WIDTH;
	protected HashMap<Integer,Vector<Segment>> finalSegmentMap;

	/**
	 * Public constructor.
	 *
	 * @param	path	The path to where images are located
	 * @param	wMax	The maximal desired width of the image
	 */
	public ImageSegment(String pathToImage){
		Bitmap bitmap = BitmapFactory.decodeFile(pathToImage);
		
		//Auto resize
		if (bitmap.getHeight() > 1000 || bitmap.getWidth() > 1000) {
			Matrix matrix = new Matrix();
			//TODO: calculate scale values
			matrix.postScale(0.25f, 0.25f);
			
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}
		
		Image image = RgbImageAndroid.toRgbImage(bitmap);
		
		this.baseImage = ImageUtils.toGray8(image);
	}

	/**
	 * Method computing relevant edges in an grayscale image.
	 *
	 * @param	ALLOW_NOISE_REDUCTION	A boolean value set to TRUE if a noise reduction is necessary.
	 * @param	num_dir					Number of computed directions
	 */
	public void getLargeConnectedEdges(boolean ALLOW_NOISE_REDUCTION, int num_dir){

		int w = this.baseImage.getWidth();
		int h = this.baseImage.getHeight();
		Gray8Image imageProcessed = this.baseImage;
		double minLength = 0.025*Math.sqrt(w*w + h*h);

		/*if (ALLOW_NOISE_REDUCTION){
			/**
			 *  Get the processed image 2D matrix.
			 */
		/*
		int[][] imageArray = this.baseImage.getProcessor().getIntArray();
			/**
			 *  Calculate the gaussian smoothed image.
			 */
		/*
			int[][] imageSmoothed = GaussianSmooth.smooth(imageArray, w, h, 7, 1.5);
			for (int i=0; i<w; i++){
				for (int j=0; j<h; j++){
					imageProcessed.getProcessor().putPixel(i, j, imageSmoothed[i][j]);
				}
			}
		}
		*/

		/**
		 *  Calculate the image X and Y gradients.
		 */
		ByteImage[] gradients = Utils.getGradients(imageProcessed);

		/**
		 *  Calculate the Canny Deriche filtered image
		 */
		float alpha = (float) 0.5;
		FloatImage imageCanny = CannyDericheFilter.processDeriche(this.baseImage, alpha);
		int wCanny = imageCanny.getWidth();
		int hCanny = imageCanny.getHeight();
		/**
		 *  Eliminate generated horizontal and vertical border edges, thickness eHMax and eVMax pixels
		 *  TODO Automatic detection of relevant thickness elimination?
		 */
		int eHMax = 4;
		for (int i=0; i<wCanny; i++){
			for (int e=0; e<eHMax; e++){
				imageCanny.setPixel(i, e, 0);
				imageCanny.setPixel(i, hCanny-1-e, 0);
			}
		}
		int eVMax = 10;
		for (int j=0; j<hCanny; j++){
			for (int e=0; e<eVMax; e++){
				imageCanny.setPixel(e, j, 0);
				imageCanny.setPixel(wCanny-1-e, j, 0);
			}
		}

		/**
		 * Cross-compare X and Y gradients to Canny-Deriche filtered image.
		 * Select, in ipGradX and ipGradY, the pixels (x,y) where ipCanny(x,y)>0.
		 * Return two arrays selectX and selectY.
		 * Compute Arctan(selectY / selectX). In case selectX[i] equals to 0, set Arctan value to a previously defined MAX_ANGLE_VALUE equal to Pi/2 - epsilon.
		 *
		 */
		final double EPSILON = 0.0000000001;
		final double MAX_ANGLE_VALUE = Math.PI/2 - EPSILON;

		Vector<Integer> vX = new Vector<Integer>();
		Vector<Integer> vY = new Vector<Integer>();
		Vector<Point> indexes = new Vector<Point>();
		HashMap<Integer, Vector<Point>> direction = new HashMap<Integer, Vector<Point>>(num_dir);
		for (Iterator<Map.Entry<Integer, Vector<Point>>> e = direction.entrySet().iterator(); e.hasNext();){
			Map.Entry<Integer, Vector<Point>> ent = (Map.Entry<Integer, Vector<Point>>) e.next();
			ent.setValue(new Vector<Point>());
		}

		for (int i=0; i<wCanny; i++){
			for (int j=0; j<hCanny; j++){
				if (imageCanny.getPixel(i, j) > 0){
					vX.add((int)gradients[0].getPixel(i, j));
					vY.add((int)gradients[1].getPixel(i, j));
					// Store indexes
					indexes.addElement(new Point(i, j)); // i = horizontal coordinate ; j = vertical coordinate
				}
			}
		}


		for (int k=0; k<vX.size(); k++){
			int angle;
			double a;
			if (vX.get(k) != 0){
				a =Math.atan(vY.get(k) / vX.get(k));
			} else {
				a = MAX_ANGLE_VALUE;
			}
			/**
			 * Calculate angle value in [|0;num_dir - 1|]
			 */
			angle = (int) Math.ceil(((a / Math.PI) * num_dir)-0.5 % num_dir);
			/**
			 * Add point indexes(k), which is on an edge of direction k, into direction hashmap.
			 */
			if (!direction.containsKey(angle)){
				direction.put(angle, new Vector<Point>());
			}
			direction.get(angle).addElement(indexes.get(k));
		}

		/**
		 * Initialize final segment map
		 */
		this.finalSegmentMap = new HashMap<Integer, Vector<Segment>>(num_dir);
		for (int key=0; key<num_dir; key++){
			this.finalSegmentMap.put(key, new Vector<Segment>());
		}

		/**
		 * Compute relevant information for each direction in [|0, num_dir-1|]
		 */
		for (int k=0; k<num_dir; k++){

			/**
			 * Calculate number of points in direction k
			 */
			int num_ind = 0;
			if (direction.containsKey(k)){
				num_ind = num_ind + direction.get(k).size();
			}

			Vector<Point> ind = new Vector<Point>(num_ind);
			int[][] dir_im=new int[this.baseImage.getHeight()][this.baseImage.getWidth()];

			/**
			 * For direction k, find points on k-oriented edges
			 * Put 1 in matrix dir_im where a point is on a k-oriented edge
			 */
			int count = 0;
			if (direction.containsKey(k)){
				for (int i=0; i<direction.get(k).size(); i++){
					ind.add(i+count, direction.get(k).get(i));
					dir_im[direction.get(k).get(i).getY()][direction.get(k).get(i).getX()] = 1;
				}
				count = count + direction.get(k).size();
			}

			/**
			 * Find 8-connected components in matrix dir_im
			 */
			ConnectedObjects c = LabelImage.labelImage(dir_im,num_dir); // Get the connected points

			/**
			 * Initialize classified segments hashmap
			 */
			HashMap<Integer, Segment> classifiedSegments = new HashMap<Integer, Segment>(c.getNum_labels());
			for (int id=0; id<c.getNum_labels(); id++){
				classifiedSegments.put(id, new Segment());
			}
			/**
			 * Get the number of pixels in each edge
			 */
			for (int i=0; i<ind.size(); i++){
				int id = c.getMatrix(ind.get(i).getY(), ind.get(i).getX());
				classifiedSegments.get(id).addPoint(ind.get(i));
			}

			/**
			 * Remove too small edges
			 * Process remaining edges
			 */
			for (int id=0; id<c.getNum_labels(); id++){
				if (classifiedSegments.get(id).getPoints().size() < minLength){
					classifiedSegments.remove(id);
				} else {
					double conf = classifiedSegments.get(id).computeStartEndPoints();
					if (conf < 400){
						classifiedSegments.remove(id);
					}
				}
			}

			/**
			 * Update finalSegmentMap
			 */
			for (Iterator<Map.Entry<Integer, Segment>> iter = classifiedSegments.entrySet().iterator(); iter.hasNext();){
				Map.Entry<Integer, Segment> ent = (Map.Entry<Integer, Segment>) iter.next();
				this.finalSegmentMap.get(k).add(ent.getValue());
			}

		}

	}

	/**
	 * Get baseImage value
	 * @return	ImagePlus this.baseImage
	 */
	public Image getBaseImage() {
		return baseImage;
	}
	/**
	 * Get MAX_WITDH value
	 * @return	int this.MAX_WIDTH
	 */
	public int getMAX_WIDTH() {
		return MAX_WIDTH;
	}
	/**
	 * Get finalSegmentMap value
	 * @return	HashMap<Integer,Vector<Segment>> this.finalSegmentMap
	 */
	public HashMap<Integer, Vector<Segment>> getFinalSegmentMap() {
		return finalSegmentMap;
	}



}
