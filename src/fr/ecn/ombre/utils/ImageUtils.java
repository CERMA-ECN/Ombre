package fr.ecn.ombre.utils;

import jjil.algorithm.Gray8Hist;
import jjil.core.Gray8Image;

public class ImageUtils {
    public static int getAutoThreshold(Gray8Image image) {
    	int[] histogram = Gray8Hist.computeHistogram(image);
        int level;
        int maxValue = histogram.length - 1;
        double result, sum1, sum2, sum3, sum4;
        
        int count0 = histogram[0];
        histogram[0] = 0; //set to zero so erased areas aren't included
        int countMax = histogram[maxValue];
        histogram[maxValue] = 0;
        int min = 0;
        while ((histogram[min]==0) && (min<maxValue))
            min++;
        int max = maxValue;
        while ((histogram[max]==0) && (max>0))
            max--;
        if (min>=max) {
            histogram[0]= count0; histogram[maxValue]=countMax;
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
        
        histogram[0]= count0; histogram[maxValue]=countMax;
        level = (int)Math.round(result);
        return level;
    }
}
