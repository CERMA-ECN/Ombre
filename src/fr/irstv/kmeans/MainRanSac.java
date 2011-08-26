package fr.irstv.kmeans;

import java.io.IOException;

import fr.irstv.dataModel.CircleKDistance;

/**
 * small testing class for the RanSac algorithm
 * @author Guillaume Moreau
 *
 */
public class MainRanSac {
	public static void main(String[] args) throws IOException {
//		if (args.length != 1) {
//			System.out.println("Usage: java MainRanSac Xmlfilename");
//			return;
//		}
		CircleKDistance fd = new CircleKDistance();
		fd.setScilabCheck(true);
		String file = "XML/facade2.xml";
		DataMk dataSet = new DataMk(file);
		System.out.println("--debug-- "+dataSet.getCorpus().size()+" elements to classify");
		long a = -System.currentTimeMillis();
		// here we launch the real job
		RanSac r = new RanSac(6,dataSet,fd);
		// param init
		r.setMaxSample(5);
		r.setSigma(40d);
		r.setStopThreshold(0.1d);
		r.go();
		a += System.currentTimeMillis();
		System.err.println("--debug-- Computation terminated in "+((float)a)/1000f+"s");		
	}
}
