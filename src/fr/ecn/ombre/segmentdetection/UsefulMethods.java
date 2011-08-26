package fr.ecn.ombre.segmentdetection;

import java.util.*;

import fr.irstv.kmeans.*;
import fr.irstv.dataModel.*;

public class UsefulMethods {

	// fait correspondre des DataPoints H aux segments d�tect�s de segmentsList 
	// et les classe par point de fuite dans segmentMap (m�thode num�rique, peu fiable)
	public HashMap<Integer, Vector<Segment>> groupBeforeDisplay(DataGroup[] groups, HashMap<Integer, Vector<Segment>> segmentsList) {
		HashMap<Integer, Vector<Segment>> segmentMap = new HashMap<Integer, Vector<Segment>>();

		int i=0;
		for (DataGroup g:groups) {
			Vector<Segment> vec = new Vector<Segment>();
			for (DataPoint dp:g.getComponents()) {
				for (int j=0;j<segmentsList.size();j++) {
					int index = belongsToWhichSegment(dp, segmentsList.get(j));
					if (index != -1) {
						vec.add(segmentsList.get(j).get(index));
						break;
					}
				}
			}
			segmentMap.put(i, vec);
			i++;
		}

		return segmentMap;
	}
	
	/**
	 * Retrieve segments groups from the RanSac, then find their associate vanishing points and turn data into a HashMap<Integer, Vector<Segment>>
	 * @param groups
	 * @return segmentMap
	 */
	public HashMap<Integer, Vector<Segment>> groupBeforeDisplay(DataGroup[] groups) {
		HashMap<Integer, Vector<Segment>> segmentMap = new HashMap<Integer, Vector<Segment>>();

		int i=0;
		for (DataGroup g:groups) {
			Vector<Segment> vec = new Vector<Segment>();
			for (MkDataPoint dp:g.getComponents()) {
				vec.add(new Segment(dp));
			}
			segmentMap.put(i, vec);
			i++;
		}

		return segmentMap;
	}

	public boolean isOnSegment(DataPoint dp, Segment s) {
		double ab[] = s.computeParams();
		double res;
		if (ab[2] == 1) {
			res = dp.get(0) - ab[1];
		} else {
			res = dp.get(0)*ab[0] + ab[1] - dp.get(1);
		}

		if (Math.abs(res)<=0.2) {
			return true;
		}
		return false;
	}

	public int belongsToWhichSegment(DataPoint dp, Vector<Segment> segments) {
		for (int i=0;i<segments.size();i++) {
			Segment s = segments.get(i);
			if (isOnSegment(dp, s)) {
				return i;
			}
		}
		return -1;
	}

}
