package fr.ecn.ombre;

import java.util.Map;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.ImageView;

import jjil.android.RgbImageAndroid;
import jjil.core.Image;
import fr.ecn.ombre.model.ImageInfos;
import fr.ecn.ombre.segmentdetection.ImageSegment;
import fr.ecn.ombre.segmentdetection.Segment;
import fr.ecn.ombre.utils.ImageUtils;
import fr.irstv.kmeans.DataGroup;
import fr.irstv.kmeans.RanSacFunction;

public class VanishingPointsController {
	
	/**
	 * Bitmap version of the image (will be used for display)
	 */
	protected Bitmap bitmap;
	
	protected Map<Integer, Vector<Segment>> segments;
	protected DataGroup[] groups;

	protected ImageView imageView;

	public VanishingPointsController(ImageInfos imageInfos) {
		Bitmap bitmap = ImageUtils.autoResize(BitmapFactory.decodeFile(imageInfos.getPath()), 750, 750);
		
		Image image = ImageUtils.toGray8(RgbImageAndroid.toRgbImage(bitmap));
		
		this.computeSegments(image);
		
		this.computeGroups();
		
		this.bitmap = ImageUtils.toBitmap(image);
	}

	public void setUp(ImageView imageView) {
		this.imageView = imageView;
		
		Drawable[] drawables = {new BitmapDrawable(this.bitmap), new VanishingPointsDrawable(this)};
		imageView.setImageDrawable(new LayerDrawable(drawables));
	}
	
	protected void computeSegments(Image image) {
		ImageSegment is = new ImageSegment(image);
		is.getLargeConnectedEdges(false, 8);
		this.segments = is.getFinalSegmentMap();
	}
	
	protected void computeGroups() {
		this.groups = new RanSacFunction(this.segments, 10, 20d, 0.01d).theDataGroup;
	}

	public void reComputeGroups() {
		this.computeGroups();
		this.imageView.invalidate();
	}
	
	/**
	 * @return the groups
	 */
	public DataGroup[] getGroups() {
		return groups;
	}

}
