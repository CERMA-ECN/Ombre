package fr.ecn.ombre;

import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;

import jjil.android.RgbImageAndroid;
import jjil.core.Image;
import fr.ecn.ombre.image.utils.ImageLoader;
import fr.ecn.ombre.model.ImageInfos;
import fr.ecn.ombre.segmentdetection.ImageSegment;
import fr.ecn.ombre.segmentdetection.Segment;
import fr.ecn.ombre.utils.ImageUtils;
import fr.irstv.dataModel.CircleKDistance;
import fr.irstv.kmeans.CleaningDataGroups;
import fr.irstv.kmeans.DataGroup;
import fr.irstv.kmeans.DataMk;
import fr.irstv.kmeans.RanSac;

public class VanishingPointsController {
	
	/**
	 * Bitmap version of the image (will be used for display)
	 */
	protected Bitmap bitmap;
	
	protected Map<Integer, List<Segment>> segments;
	protected DataGroup[] groups;

	public VanishingPointsController(ImageInfos imageInfos) {
		Bitmap bitmap = ImageLoader.loadResized(imageInfos.getPath(), 800);
		
		Image image = ImageUtils.toGray8(RgbImageAndroid.toRgbImage(bitmap));
		
		this.computeSegments(image);
		
		this.computeGroups();
		
		this.bitmap = ImageUtils.toBitmap(image);
	}
	
	public void computeSegments(Image image) {
		Log.i("Ombre", "Starting segments computation");
		long time = System.nanoTime();
		
		ImageSegment is = new ImageSegment(image);
		is.getLargeConnectedEdges(false, 8);
		this.segments = is.getFinalSegmentMap();
		
		Log.i("Ombre", "Segments computation done in " + (System.nanoTime() - time));
	}
	
	public void computeGroups() {
		Log.i("Ombre", "Starting groups computation");
		long time = System.nanoTime();
		
		CircleKDistance fd = new CircleKDistance();

		DataMk dataSet = new DataMk(this.segments);
		
		// here we launch the real job
		RanSac r = new RanSac(6,dataSet,fd);
		// param init
		r.setMaxSample(25);
		r.setSigma(20d);
		r.setStopThreshold(0.05d);
		r.go();
		
		//Cleaning the groups
		this.groups = new CleaningDataGroups().clean(r.getGroups());
		
		Log.i("Ombre", "Groups computation done in " + (System.nanoTime() - time));
	}

	/**
	 * @return the bitmap
	 */
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	/**
	 * @return the groups
	 */
	public DataGroup[] getGroups() {
		return groups;
	}

}
