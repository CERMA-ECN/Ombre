package fr.ecn.ombre.model;

import java.io.Serializable;

public class Point implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected double x;
	protected double y;
	
	/**
	 * @param x
	 * @param y
	 */
	public Point(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
}
