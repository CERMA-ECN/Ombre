package fr.ecn.ombre.utils;

import fr.ecn.ombre.image.filters.Gray8toRgb;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import jjil.algorithm.Gray8Hist;
import jjil.algorithm.RgbAvgGray;
import jjil.core.Error;
import jjil.core.Gray8Image;
import jjil.core.Image;
import jjil.core.RgbImage;

public class ImageUtils {
    public static int getAutoThreshold(Gray8Image image) {
    	int[] histogram = Gray8Hist.computeHistogram(image);
        int level;
        int maxValue = histogram.length - 1;
        double result, sum1, sum2, sum3, sum4;
        
		//int count0 = histogram[0];
        histogram[0] = 0; //set to zero so erased areas aren't included
		//int countMax = histogram[maxValue];
        histogram[maxValue] = 0;
        int min = 0;
        while ((histogram[min]==0) && (min<maxValue))
            min++;
        int max = maxValue;
        while ((histogram[max]==0) && (max>0))
            max--;
        if (min>=max) {
			//histogram[0]= count0; histogram[maxValue]=countMax;
            level = histogram.length/2;
            return level;
        }
        
        int movingIndex = min;
        do {
            sum1=sum2=sum3=sum4=0.0;
            for (int i=min; i<=movingIndex; i++) {
                sum1 += (double)i*histogram[i];
                sum2 += histogram[i];
            }
            for (int i=(movingIndex+1); i<=max; i++) {
                sum3 += (double)i*histogram[i];
                sum4 += histogram[i];
            }           
            result = (sum1/sum2 + sum3/sum4)/2.0;
            movingIndex++;
        } while ((movingIndex+1)<=result && movingIndex<max-1);
        
		//histogram[0]= count0; histogram[maxValue]=countMax;
        level = (int)Math.round(result);
        return level;
    }

	public static Gray8Image toGray8(Image image) {
		if (image instanceof Gray8Image) {
			return (Gray8Image) image;
		} else if (image instanceof RgbImage) {
			RgbAvgGray convertor = new RgbAvgGray();
			try {
				convertor.push(image);
				return (Gray8Image) convertor.getFront();
			} catch (Error e) {
				//Shouldn't append
				return null;
			}
		} else {
			//TODO
			return null;
		}
	}

	public static RgbImage toRgb(Image image) {
		if (image instanceof RgbImage) {
			return (RgbImage) image;
		} else if (image instanceof Gray8Image) {
			Gray8toRgb convertor = new Gray8toRgb();
			try {
				convertor.push(image);
				return (RgbImage) convertor.getFront();
			} catch (Error e) {
				//Shouldn't append
				return null;
			}
		} else {
			//TODO
			return null;
		}
	}
	
	public static Bitmap toBitmap(Image image) {
		RgbImage rgbImage = toRgb(image);
		
		int[] data = rgbImage.getData();
		
		System.out.println("Data : " + Integer.toHexString(data[45]));
		
		Bitmap b =  Bitmap.createBitmap(
				rgbImage.getData(),
				rgbImage.getWidth(),
				rgbImage.getHeight(), 
                Bitmap.Config.ARGB_8888);
		
		System.out.println("Pixel : " + Integer.toHexString(b.getPixel(45, 0)));
		
		return b;
	}
	
	public static Bitmap autoResize(Bitmap bitmap, int maxHeight, int maxWidth) {
		int height = bitmap.getHeight();
		int width  = bitmap.getWidth();
		
		if (height >= maxHeight || width >= maxWidth) {
			float scale = Math.max((float)maxHeight / height, (float)maxWidth / width);
			
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}
		
		return bitmap;
	}
}
