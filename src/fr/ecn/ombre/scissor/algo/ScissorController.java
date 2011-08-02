package fr.ecn.ombre.scissor.algo;

//import ij.gui.PolygonRoi;

import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.Iterator;

import jjil.core.Gray8Image;


/**
 * Scissor Controller contains a ScissorElement array list which
 * can manage multiple scissor lines. Scissor lines can be grouped by
 * setting their type value. This controller can also set the 
 * group color or get the group area value.
 * @author LIU Xinchang
 *
 */
public class ScissorController extends Scissor {
	private int currentLayerID;
	private ScissorElement currentScissor;
	private ArrayList<ScissorElement> arrScissor;
	private int currentType;
	/**
	 * Constructor of ScissorController, sent an Image
	 * to its parent and initial current type to zero.
	 * @param ip
	 */
	public ScissorController(Gray8Image ip) {
		super(ip);
		arrScissor=new ArrayList<ScissorElement>();
		currentType=0;
	}	
	/**
	 * Tell this controller to generate a new scissor line.
	 */
	public void newScissorProcess()
	{
		newScissorProcess(currentType );
	}
	/**
	 * Tell this controller to generate a new scissor line
	 *  of specified type.
	 *  @param type int scissor line type 
	 */
	public void newScissorProcess(int type )
	{
		if(currentScissor==null)
		{	
			currentLayerID=0;
			currentScissor =new ScissorElement(this, currentLayerID,type);
			
		}
		else 
		{
			if (currentScissor.getState()!=SCISSOR_STATE.HOLD)
				currentScissor.endScissor();
			currentLayerID++;
			currentScissor =new ScissorElement(this, currentLayerID,type);
		}
		if(!arrScissor.contains(currentScissor))
			arrScissor.add(currentScissor);
		setActive();
	}
	/**
	 * Tell current scissor line to add a new key point.
	 * @param x int x coordinate
	 * @param y int y coordinate
	 */
	public void addNewKeyPoint(int x,int y)
	{
		if (currentScissor!=null)
			currentScissor.addNewKeyPoint(x, y);
	}
	/**
	 * Tell current scissor line that the cursor 
	 * is moving to (x,y).
	 * @param x int x coordinate
	 * @param y int y coordinate
	 */	
	public void setMovePoint(int x,int y)
	{
		if (currentScissor!=null)
			currentScissor.setMovePoint(x, y);
	}
	/**
	 * Tell current scissor to end the track process.
	 */
	public void endScissor()
	{
		if (currentScissor!=null)
			currentScissor.endScissor();
	}	
	/**
	 * Tell current scissor to remove recent key point.
	 */
	public void removeRecentKeyPoint()
	{
		if (currentScissor!=null)
			currentScissor.removeRecentKeyPoint();
	}
	/**
	 * Tell current scissor to reset.
	 */
	public void reset()
	{
		if (currentScissor!=null)
			currentScissor.reset();
	}

	/**
	 * Paint all scissor lines in the array list.
	 * @param canvas Canvas
	 */
	public void draw(Canvas canvas)  {
		for (ScissorElement se : this.arrScissor) {
			if (se.getVisible())
				se.draw(canvas);
		}
	}
	
	/**
	 * Clear all scissor lines
	 */
	public void resetScissorController()
	{
		arrScissor.clear();
	}
	/**
	 * End and remove current scissor from the array list
	 */
	public void removeCurrentScissor()
	{
		arrScissor.get(currentLayerID).reset();
	}
	/**
	 * Convert current scissor line to a ROI of ImageJ.
	 * @return A PolygonRoi copy of current scissor line 
	 */
//	public PolygonRoi getRoiOfCurrentScissor()
//	{
//		return getRoi(currentLayerID);
//	}
	/**
	 * Convert scissor line whom id is n to a ROI of ImageJ.
	 * @return A PolygonRoi copy of No.n scissor line 
	 */
//	public PolygonRoi getRoi(int n)
//	{
//		return arrScissor.get(n).toRoi();
//	}
        
    public void setState(SCISSOR_STATE state)
	{
    	currentScissor.setState(state);
	}
	public void setActive()
	{
		currentScissor.setActive();
	}
	public void setHold()
	{
		currentScissor.setHold();
	}
	//bug
	public boolean isDoing()
	{
		if (currentScissor!=null)
			return (currentScissor.getState()==SCISSOR_STATE.DOING);
		return (false);
	}
	/**
	 * Change current scissor line to one specified type
	 * @param t int number, indicate the type
	 */
	public void changeCurrentScissorType(int t)
	{
		currentScissor.setType(t);
	}
	/**
	 * Get the area enclosed by type 0 scissor line
	 * @return int pixels number of this area
	 */
//	public int getSurface()
//	{
//		return getSurface(0);
//	}
	/**
	 * Get the area enclosed by a scissor line of specified type
	 * @return int pixels number of this area
	 */
//	public int getSurface(int type)
//	{
//		int surface=0;
//		Iterator<ScissorElement> i =arrScissor.iterator();
//		while (i.hasNext())
//		{
//			ScissorElement se=i.next();
//			if (se.getType()==type)
//				surface+=se.getSurface();
//		}
//		return surface;
//	}
	/**
	 * Set current type, follow scissor lines will be of this type. 
	 * @param t int number, indicate the type
	 */
	public void setCurrentTpye(int t)
	{
		this.currentType=t;
		
	}
	/**
	 * @return current type of scissor line
	 */
	public int getCurrentType()
	{
		return this.currentType;
		
	}
	/**
	 * Set color of current scissor line.
	 * @param c Color to set
	 */
	public void setTypeColor(int c)
	{
		Iterator<ScissorElement> i =arrScissor.iterator();
		while (i.hasNext())
		{
			ScissorElement se=i.next();
			if (se.getType()==currentType)
				se.setLineColor(c);
		}
	}
	/**
	 * Set color of scissor lines of a specified type  
	 * @param type	int number, indicate the type of scissor lines
	 * @param c	Color to set
	 */
	public void setTypeColor(int type,int c)
	{
		Iterator<ScissorElement> i =arrScissor.iterator();
		while (i.hasNext())
		{
			ScissorElement se=i.next();
			if (se.getType()==type)
				se.setLineColor(c);
		}
	}
}