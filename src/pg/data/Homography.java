package pg.data;
import Jama.Matrix;

/**
*
* a class to handle homographies in projective geometry
* @author Cedric Telegone, ECN 2010
*
*/


public class Homography {

	protected Matrix h;

	/**
	 * Create a new homography from a Matrix
	 * @param h a Matrix
	 */
	public Homography(Matrix h){
		this.h=h;
	}

	/**
	 *
	 * @return the matrix homography
	 */
	public Matrix getH(){
		return h;

	}


	/**
	 * print the matrix homography values
	 */
	public void print(){
	h.times(1000).print(10,3);
	}

	/**
	 * invert the homography
	 * @return a new inverted homography
	 */
	public Homography invert(){

		return new Homography(h.inverse());
	}



}
