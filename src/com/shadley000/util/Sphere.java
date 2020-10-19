package com.shadley000.util;

import java.io.Serializable;

public class Sphere  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	VectorD center;
	double radius;
	
	public Sphere(VectorD center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	public VectorD getCenter() {
		return center;
	}
	
	public void setCenter(VectorD center) {
		this.center = center;
	}
	
	public void move(VectorD move) {
		this.center = center.add(move);
	}

	public double getRadius() {
		return radius;
	}

	public boolean isPointInside(VectorD p) {
		return (p.subtract(center).length() < radius);
	}

	public double surfaceArea() {
		return Sphere.surfaceArea(radius);
	}

	public double volume() {
		return Sphere.volume(radius);
	}

	public static double surfaceArea(double radius) {
		return 4.0 * radius * radius * Math.PI;
	}

	public static double volume(double radius) {
		return (4.0 / 3.0) * radius * radius * radius * Math.PI;
	}
	
	public boolean isCollision(Sphere s)
	{
		return(center.subtract(s.center).length()<radius+s.radius);
	}
	public String toString() {return center.toString() +":"+radius;}

}
