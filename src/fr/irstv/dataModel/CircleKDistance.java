/**
 *
 */
package fr.irstv.dataModel;

import java.util.LinkedList;
import java.util.List;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

//import no.uib.cipr.matrix.DenseMatrix;
//import no.uib.cipr.matrix.NotConvergedException;
//import no.uib.cipr.matrix.SVD;


import fr.irstv.kmeans.EuclidianDistance;

/**
 * @author moreau
 *
 */
public class CircleKDistance extends EuclidianDistance {

	/**
	 * tells whether scilab checking code should be generated
	 */
	private boolean scilabCheck;

	/**
	 * for debugging  purposes: group number
	 */
	private int debugGroupNumber;

	/**
	 * for debugging purposes: iteration number
	 */
	private int debugIteration;

	private static String colorIndexTab[] = { "b" , "r" , "g" , "y" , "k" };

	public int getDebugGroupNumber() {
		return debugGroupNumber;
	}
	public void setDebugGroupNumber(int debugGroupNumber) {
		this.debugGroupNumber = debugGroupNumber;
	}
	public int getDebugIteration() {
		return debugIteration;
	}
	public void setDebugIteration(int debugIteration) {
		this.debugIteration = debugIteration;
	}
	/**
	 * matrix display
	 */
//	void print(DenseMatrix a) {
//		for (int i=0 ; i<a.numRows() ; i++) {
//			for (int j=0 ; j<a.numColumns() ; j++) {
//				System.out.print(a.get(i, j)+"\t");
//			}
//			System.out.println();
//		}
//	}
	/**
	 * computation of the centroid is done here; in fact it is
	 * the computation of the new circle
	 *
	 * @see fr.irstv.kmeans.DataDistance#centroid(java.util.List)
	 */
	public DataPoint centroid(List<? extends DataPoint> l) {
		// for debug
		LinkedList<DataPoint> listH = new LinkedList<DataPoint>();

		// B matrix creation
		Matrix B = new Matrix(l.size(),4);
		for (int i=0 ; i<l.size() ; i++) {
			DataPoint h = l.get(i);
			listH.add(h);
			double xh = h.get(0);
			double yh = h.get(1);
			B.set(i, 0, xh*xh+yh*yh);
			B.set(i, 1, xh);
			B.set(i, 2, yh);
			B.set(i, 3, 1d);
		}

		if (isScilabCheck()) {
			System.out.println("h_"+getDebugGroupNumber()+"=[");
			for (DataPoint _h : listH) {
				System.out.println(_h);
			}
			System.out.println("];");
		}

		// compute SVD of B matrix
		if (l.size() < 3) {
			System.out.println("probably not enough points for B");
		}
		/*System.out.println(B.numRows());
		System.out.println(B.numColumns());
		*/
		SingularValueDecomposition svd = B.svd();
		Matrix V = svd.getV();

		double a = V.get(0,3);
		double b1 = V.get(1,3);
		double b2 = V.get(2,3);
		double c = V.get(3,3);
		double z1 = -b1/2d/a;
		double z2 = -b2/2d/a;
		double r = Math.sqrt(z1*z1+z2*z2-c/a);
		/*
		if (isScilabCheck()) {
			System.out.println("plot(h_"+getDebugGroupNumber()+"(:,1),h_"+getDebugGroupNumber()+"(:,2),'"+colorIndexTab[getDebugGroupNumber()]+"+')");
			System.out.println("circle("+z1+","+z2+","+r+",'"+colorIndexTab[getDebugGroupNumber()]+"')");
		}
		*/

		DataKCircle cx = new DataKCircle(2);
		cx.set(0,z1);
		cx.set(1,z2);
		cx.setRadius(r);
		System.out.println("distances between the points and the circle");
		for (int i=0; i<listH.size(); i++) {
			System.out.println(distance(listH.get(i),cx));
		}

		return cx;
	}

//	private void petiteFonctionBidon(int n,double xa,double xb,double r) {
//		DenseMatrix B = new DenseMatrix(n,4);
//		for (int i=0 ; i<n ; i++) {
//			double x = xa+r*Math.cos(2d*Math.PI/(double)n*(double)i);
//			double y = xb+r*Math.sin(2d*Math.PI/(double)n*(double)i);
//
//			B.set(i, 0, x*x+y*y);
//			B.set(i,1,x);
//			B.set(i,2,y);
//			B.set(i,3,1d);
//
//		}
//		System.out.println(B);
//		// on cacule la SVD de B
//
//		SVD svd = new SVD(B.numRows(),B.numColumns());
//		try {
//			svd.factor(B);
//		} catch (NotConvergedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		DenseMatrix V = svd.getVt();
//		//V.transpose();
//		double a = V.get(0,3);
//		double b1 = V.get(1,3);
//		double b2 = V.get(2,3);
//		double c = V.get(3,3);
//		double z1 = -b1/2d/a;
//		double z2 = -b2/2d/a;
//		double r2 = Math.sqrt(z1*z1+z2*z2-c/a);
//		DataPoint dp = new DataPoint(2);
//		dp.set(0,z1);
//		dp.set(0,z2);
//		System.out.println("z = ("+z1+","+z2+")");
//		System.out.println("r = "+r2);
//
//	}

	/**
	 * the distance is always seen as the distance to the circle. Do not forget
	 * that here the second point is always the center of the circle. This does
	 * not procide us with the distance. The circle K is derived class from
	 * the DataPoint notion. It is a virtual DataPoint that contains more
	 * information, i.e. the radius of the circle

	 * @see fr.irstv.kmeans.DataDistance#distance(fr.irstv.dataModel.DataPoint, fr.irstv.dataModel.DataPoint)
	 */
	public double distance(DataPoint p1, DataPoint p2) {
		// is p2 a CircleK
		double d;
		try {
			DataKCircle c = (DataKCircle) p2;
			// distance to the circle = distance to the center - radius
			d = Math.abs(super.distance(p1, p2)-c.getRadius());
		}
		catch (ClassCastException e) {
			e.printStackTrace();
			return -1d;
		}
		return d;
	}

//	public static void main(String[] args) {
//
//		DataPoint x1 = new DataPoint(4);
//		x1.set(0,0.0); x1.set(1, 5.0);
//		x1.set(2, 1.0); x1.set(3, 3.0);
//		System.out.println("x1="+x1);
//
//		DataPoint x2 = new DataPoint(4);
//		x2.set(0,1.0); x2.set(1, 5.0);
//		x2.set(2, 4.0); x2.set(3, 2.0);
//		System.out.println("x2="+x2);
//
//		DataPoint x3 = new DataPoint(4);
//		x3.set(0,1.0); x3.set(1, 7.0);
//		x3.set(2, 5.0); x3.set(3, 5.0);
//		System.out.println("x2="+x2);
//
//		LinkedList<DataPoint> lp = new LinkedList<DataPoint>();
//		lp.add(x1);
//		lp.add(x2);
//		lp.add(x3);
//
//
//		CircleKDistance test = new CircleKDistance();
//		test.petiteFonctionBidon(5, 2, 3, 1);
//
//		test.centroid(lp);
//	}
	public boolean isScilabCheck() {
		return scilabCheck;
	}
	public void setScilabCheck(boolean scilabCheck) {
		this.scilabCheck = scilabCheck;
	}
}
