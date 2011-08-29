package fr.ecn.ombre;

import java.util.HashMap;
import java.util.Vector;

import android.graphics.Bitmap;
import android.widget.ImageView;
import fr.ecn.ombre.model.ImageInfos;
import fr.ecn.ombre.segmentdetection.Segment;
import fr.ecn.ombre.segmentdetection.SegmentDetectionFunction;
import fr.ecn.ombre.segmentdetection.UsefulMethods;
import fr.irstv.kmeans.DataGroup;
import fr.irstv.kmeans.RanSacFunction;

public class VanishingPointsController {
	
	protected Bitmap b;

	public VanishingPointsController(ImageInfos imageInfos) {
		UsefulMethods um = new UsefulMethods();
		
		SegmentDetectionFunction sdf = new SegmentDetectionFunction(imageInfos.getPath(), false);
		RanSacFunction rsf = new RanSacFunction(sdf.segmentsList, 5, 20d, 0.05d);
		
		DataGroup[] theDataGroup = rsf.theDataGroup; // cleaned groups of DataPoints
		
		HashMap<Integer, Vector<Segment>> segmentMap = um.groupBeforeDisplay(theDataGroup);
		b = sdf.segmentDisplayFunction(imageInfos.getPath(), segmentMap, theDataGroup);
	}

	public void setUp(ImageView image) {
		image.setImageBitmap(b);
	}

}
