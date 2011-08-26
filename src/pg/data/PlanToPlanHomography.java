package pg.data;
import Jama.Matrix;
import Jama.SingularValueDecomposition;

/**
 *
 * A class to compute an homography between two plans
 * @author Cedric Telegone, ECN 2010
 */
public class PlanToPlanHomography {
	protected Point x1;
	protected Point x2;
	protected Point x3;
	protected Point x4;
	protected Point x1_;
	protected Point x2_;
	protected Point x3_;
	protected Point x4_;



	/**
	 * create a new plan to plan homography
	 * @param x1
	 * @param x1_
	 * @param x2
	 * @param x2_
	 * @param x3
	 * @param x3_
	 * @param x4
	 * @param x4_
	 */
	public PlanToPlanHomography(Point x1,Point x1_,Point x2,Point x2_,Point x3,Point x3_,Point x4,Point x4_){

		this.x1=x1;
		this.x1_=x1_;
		this.x2=x2;
		this.x2_=x2_;
		this.x3=x3;
		this.x3_=x3_;
		this.x4=x4;
		this.x4_=x4_;



	}

	/**
	 * computation of the homography Matrix
	 * @return the computed homography
	 */
	public Homography compute(){

		//p 91

		//1: Compute Ai 2x9 matrix
		Matrix A1=x1.A(x1_);
		Matrix A2=x2.A(x2_);
		Matrix A3=x3.A(x3_);
		Matrix A4=x4.A(x4_);

		//2: Assemble the 4 2x9 matrix into A 8x9 matrix

		double[][] mA=new double[9][9];
		mA[0]=A1.getArray()[0];
		mA[1]=A1.getArray()[1];
		mA[2]=A2.getArray()[0];
		mA[3]=A2.getArray()[1];
		mA[4]=A3.getArray()[0];
		mA[5]=A3.getArray()[1];
		mA[6]=A4.getArray()[0];
		mA[7]=A4.getArray()[1];
		for(int i=0;i<9;i++){
			mA[8][i]=0;
		}




		Matrix A=new Matrix(mA);

		SingularValueDecomposition svd=new SingularValueDecomposition(A);
		Matrix v=svd.getV();
		double[] h=new double[9];
		for(int i=0;i<9;i++){
		h[i]=v.getArray()[i][8];

		}


		Matrix hMatrix=new Matrix(h,3);



		return new Homography(hMatrix.transpose());
	}

}
