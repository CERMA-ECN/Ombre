package fr.ecn.ombre.cissor.algo;

//import ij.IJ;
//import ij.ImagePlus;
import ij.gui.PolygonRoi;
//import ij.process.ImageProcessor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import android.graphics.Point;
import android.graphics.Rect;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class for control one track of scissor tool from begin to end.
 * This class provides method to add, remove points, auto generation
 * of key points, auto remove recent point and so on. Paint method
 * can be call if user want to show this track. 
 * @author LIU Xinchang
 *
 */
public class ScissorLine{
	//the operation is determined by current state 
	private SCISSOR_STATE state;
	
	private ScissorPolygon currentScissorLine;
	private ArrayList<ScissorPolygon> scissorLine; //this array list stores polygons from one key point to next key point
	
	private Color currentLineColor;
	private Color lineColor;
	private double magnification;	//scale parameter
	private Rect srcRect;		//translation parameter
	private Scissor scissor;		//each polygon is obtained from the result of algorithm in class Scissor 
	
	/*
	 * interactive operation variable, used detect mouse motion 
	 */
	private int mouseCount;			 
	private int mouseCountMax;
	private int endCounter;
	private ArrayList<AutoKeyVertex> autoKeyVertexList;
	private int removeCounter;

	/**
	 * Constructor of ScissorLine, initial parameters
	 * @param s Scissor associated to current graph
	 */
	public ScissorLine(Scissor s){
		scissor=s;
		lineColor=Color.yellow;
		currentLineColor=Color.red;
		srcRect=new Rect(0,0,0,0);
		magnification=1;
		scissorLine=new ArrayList<ScissorPolygon>();
		state=SCISSOR_STATE.HOLD;
		mouseCount=0;
		mouseCountMax=4;
		autoKeyVertexList=new ArrayList<AutoKeyVertex>();
		
	}
	/**
	 *Add a new key point in scissor track.
	 * @param x	x coordinate
	 * @param y y coordinate
	 */
	public void addNewKeyPoint(int x,int y)
	{
        x = offScreenX(x);
        y = offScreenY(y);
        
		if (state==SCISSOR_STATE.BEGIN)	//just after the scissor tool begin 	
    	{
			reset();
			scissor.setBegin(x, y);
    		currentScissorLine=scissor.getPathsList(x, y);

    		state=SCISSOR_STATE.DOING;
    		this.autoKeyVertexList.clear();
    		removeCounter=0;
    		endCounter=0;
        	
    	}else if(state ==SCISSOR_STATE.DOING)
    	{
    		ScissorPolygon tempPath=scissor.getPathsList(x, y);
			scissorLine.add(tempPath);
			scissor.setBegin(x, y);
    		currentScissorLine=scissor.getPathsList(x, y);
    		this.autoKeyVertexList.clear();
    		removeCounter=0;
    		endCounter=0;
    	}
	}
	/**
	 * When mouse move, call this method for generating changeable track
	 * and handle the interactive function.
	 * @param x	x coordinate
	 * @param y y coordinate
	 */
	public void setMovePoint(int x,int y)
	{
        x = offScreenX(x);
        y = offScreenY(y);
        //Check current state
		if (state==SCISSOR_STATE.DOING)
		{	
			//Backup current changeable scissor line
           	ScissorPolygon tempSp=currentScissorLine;
           	//update current changeable scissor line
        	currentScissorLine=scissor.getPathsList(x, y);
        	//Auto generate key point
        	if(mouseCount++>mouseCountMax)
        	{
        		mouseCount=0;
        		Point p=currentScissorLine.separationPoint(tempSp, 10);
        		if (p!=null)
        		{
        			AutoKeyVertex v=new AutoKeyVertex(p);
        			this.autoKeyVertexList.add(v);
        		}
        		int i=0;
        		//Check whether ancient key points are still on the track
        		while(i<autoKeyVertexList.size())
        		{
	        		AutoKeyVertex v =autoKeyVertexList.get(i);
	        		if (currentScissorLine.containVertex(v.getX(), v.getY(),10))
	    			{
	    				if (v.count())
	    				{
	    					Rect r=currentScissorLine.getBounds();
	    					this.addNewKeyPoint(reOffScreenX(v.getX()), reOffScreenY(v.getY()));
	    					scissor.setActiveRegion(r.left, r.top);
	    					scissor.setActiveRegion(r.right, r.bottom);
	    					autoKeyVertexList.remove(v);
	    				}
	    				i++;
	    			}
	    			else
	    				autoKeyVertexList.remove(v);	
        		}
        	}
        	//Auto remove last key point if the cursor is close to it
    		if (removeCounter<10)
    		{
        		if (currentScissorLine.npoints<6)
        			removeCounter++;
    		}
    		else
    		{
    			this.removeRecentKeyPoint();
    			removeCounter=0;
    		}
    		//Auto end the scissor track if the cursor is close to begin point
    		if (scissorLine.size()>1)
    		{
	    		if(endCounter<10)
	    		{
	    			ScissorPolygon sp=scissorLine.get(0);
	        		if (x-sp.getBeginX()<5 && sp.getBeginX()-x<5 && sp.getBeginY()-y<5 && y-sp.getBeginY()<5)
	        			endCounter++;
	    		}
	    		else
	    		{
	    			this.endScissor();
	    			endCounter=0;
	    		}
    		}
		}
	}
	/**
	 * End scissor process and connect last point to begin point
	 */
	public void endScissor()
	{
		if (state==SCISSOR_STATE.DOING)
		{
			currentScissorLine=null;
			ScissorPolygon pg=scissorLine.get(0);
			scissorLine.add(scissor.getPathsList(pg.getBeginX(), pg.getBeginY()));
			state=SCISSOR_STATE.HOLD;
		}
	}	
	/**
	 * If current line is not from begin point, Remove recent key point.
	 */
	public void removeRecentKeyPoint()
	{
		if (scissorLine.size()>1)
		{
			scissorLine.remove(scissorLine.size()-1);
			ScissorPolygon pg=scissorLine.get(scissorLine.size()-1);
			scissor.setBegin(pg.xpoints[0],pg.ypoints[0]);
		}
		else if (scissorLine.size()==1)
		{
			ScissorPolygon pg=scissorLine.get(0);
			scissor.setBegin(pg.getBeginX(),pg.getBeginY());
			scissorLine.remove(scissorLine.size()-1);
		}
	}
	/**
	 * Reset scissorLine, clear all tracks.
	 */
	public void reset()
	{
		this.scissorLine.clear();
		currentScissorLine=null;
		this.state=SCISSOR_STATE.HOLD;
	}
	/**
	 * Paint all tracks,  default color is yellow for tracks and
	 * red for changeable part. A square is also painted at every
	 * key point.
	 * @param g Of type Graphics
	 */
	public void paint(Graphics g)
	{

	    Graphics2D g2 = (Graphics2D)g;
	    g2.setColor(lineColor);
	    g2.setStroke(new BasicStroke(1f));
	    
	    Iterator<ScissorPolygon> i=scissorLine.iterator();
	    while (i.hasNext())
	    {
	    	Polygon pg=(Polygon) i.next();
	    	pg=newReOffScreenPoly(pg);
	    	g2.drawRect(pg.xpoints[0]-2, pg.ypoints[0]-2,4,4);
	    	g2.drawPolyline(pg.xpoints, pg.ypoints,pg.npoints);
	    }
	    g2.setColor(currentLineColor);
	    if (currentScissorLine!=null)
	    {
	    	Polygon pg=newReOffScreenPoly(currentScissorLine);
	    	g2.drawPolyline(pg.xpoints, pg.ypoints,pg.npoints);
	    }
	    
	}
    public Polygon newReOffScreenPoly(Polygon poly)
    {
    	Polygon poly2=new Polygon();
    	for(int i=0;i<poly.npoints;i++)
    		poly2.addPoint(reOffScreenX(poly.xpoints[i]), reOffScreenY(poly.ypoints[i]));
    	return poly2;
    }
    /**
     * Translation from screen view coordinate to image coordinate 
     * @param x x coordinate in screen view
     * @return x coordinate in image 
     */
    public int offScreenX(int x)
    {
		return (int) (x/magnification +srcRect.left);
    }
    /**
     * Translation from screen view coordinate to image coordinate 
     * @param y y coordinate in screen view
     * @return y coordinate in image 
     */
    public int offScreenY(int y)
    {
		return (int) (y/magnification +srcRect.top);
    }
    /**
     * Translation from image coordinate to screen view coordinate
     * @param x x coordinate in image
     * @return  x coordinate in screen view
     */
    public int reOffScreenX(int x)
    {
		return (int) ( (x-srcRect.left)*magnification);
    }
    /**
     * Translation from image coordinate to screen view coordinate
     * @param y y coordinate in image
     * @return  y coordinate in screen view
     */
    public int reOffScreenY(int y)
    {
		return (int) ((y-srcRect.top)*magnification);
    }  
    /**
     * Set zoom parameter.
     * @param m double number of magnification
     */
    public void setMagnification(double m)
    {
    	this.magnification=m;
    }
    /**
     * Set screen rectangle which defines translation parameter.
     * @param r Rect
     */
    public void setSrcRect(Rect r)
    {
    	this.srcRect=r;
    } 
	/**
	 * Set current state to a user defined state.
	 * @param s state of type SCISSOR_STATE
	 */
	public void setState(SCISSOR_STATE s)
	{
		state=s;
	}
	/**
	 * Set current state to BEGIN.
	 */
	public void setActive()
	{
		state=SCISSOR_STATE.BEGIN;
	}
	/**
	 * Set current state to HOLD.
	 */
	public void setHold()
	{
		state=SCISSOR_STATE.HOLD;
	}
	/**
	 * Check current state
	 * @return current state
	 */
	public SCISSOR_STATE getState()
	{
		return state;
	}
	/**
	 * Check current state
	 * @return true if surrent state is isDoing
	 */
	public boolean isDoing()
	{
		return (state==SCISSOR_STATE.DOING);
	}
	/**
	 * Convert scissor track to a free ROI of ImageJ
	 * @return A PolygonRoi 
	 */
	public PolygonRoi toRoi()
	{
		ScissorPolygon pg=new ScissorPolygon();
		for (int i=0;i<this.scissorLine.size();i++)
			pg.appends(scissorLine.get(i));
		PolygonRoi pr=new PolygonRoi(pg, PolygonRoi.FREEROI);
		return pr;
	}
	/**
	 * Get the area in the scissor track
	 * @return int area
	 */
	public int getSurface()
	{
		int surface=0;
		byte[] b=(byte[]) toRoi().getMask().getPixels();
		for (int i=0;i<b.length;i++)
			if (b[i]!=0)
				surface++;
		return surface;
	}
	/**
	 * Set color of scissor track
	 * @param c A color
	 */
	public void setLineColor(Color c)
	{
		this.lineColor=c;
	}
	/**
	 * Set color of changeable part of track line
	 * @param c A color
	 */
	public void setCurrentLineColor(Color c)
	{
		this.currentLineColor=c;
	}
}
