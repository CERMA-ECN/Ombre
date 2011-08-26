package pg.data;

/**
 * A class to handle homogenous vectors in projective geometry
 * @author Cedric Telegone, ECN 2010
 *
 */
public class Vector{

	protected double x;
	protected double y;
	protected double z;

	/**
	 *
	 */
	public Vector(){
		x=0;
		y=0;
		z=0;
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vector(double x,double y,double z){
		this.x=x;
		this.y=y;
		this.z=z;
	}

	/**
	 *
	 * @param x
	 * @param y
	 */
	public Vector(double x, double y){

		this.x=x;
		this.y=y;
		z=1;
	}

	/**
	 *
	 * @return
	 */
	public double getX() {
		return x;
	}

	/**
	 * to normalize the vector (z=1)
	 * @return
	 */
	public Vector normalize(){
		if(z==0)
			return this;
		else
			return new Vector(x/z,y/z,1);
	}

	/**
	 *
	 * @param x
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 *
	 * @return
	 */
	public double getY() {
		return y;
	}

	/**
	 *
	 * @param y
	 */
	public void setY(double y) {
		this.y = y;
	}
	/**
	 *
	 * @param z
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 *
	 * @return
	 */
	public double getZ() {
		return z;
	}


	/**
	 * test the equality of homogeneous vectors
	 * @param p
	 * @return
	 */
	public boolean equals(Vector p){
		this.normalize();
		p.normalize();
		if(this.x==p.getX()&&this.y==p.getY()&&this.z==p.getZ())
			return true;
		else
			return false;
	}

	/**
	 * scalar product
	 * @param v
	 * @return
	 */
	public double scalar(Vector v){
		double result;


		result=x*v.getX()+y*v.getY()+z*v.getZ();
		return result;
	}

	/**
	 * distance fonction
	 * @param v
	 * @return
	 */
	public double distance(Vector v){
		if(z==0||v.getZ()==0)
			return 0;
		else{
			double dX=x/z-v.getX()/v.getZ();
			double dY=y/z-v.getY()/v.getZ();
			return Math.sqrt(dX*dX+dY*dY);

		}

	}

	/**
	 * cross product (product of vectors)
	 * @param v
	 * @return
	 */
	public Vector cross(Vector v){
		Vector result=new Vector();
		result.setX(y*v.getZ()-v.getY()*z);
		result.setY(-x*v.getZ()+v.getX()*z);
		result.setZ(x*v.getY()-v.getX()*y);
		return result;
	}

	/**
	 * display vector coordinates
	 */
	public void print(){
		System.out.println("["+x+" "+y+" "+z+"]");
	}


}
