package com.shadley000.util;

public class PolarCoordinate {

	private double radius;
	private double theta;
	private double rho;

	public PolarCoordinate(double radius, double theta, double rho) {
		this.radius = radius;
		this.theta = theta;
		this.rho = rho;
	}
	
	public PolarCoordinate(VectorD v) {
		this.radius = v.length();
		this.theta = Math.atan2(v.getY(), v.getX());
		this.rho = Math.acos(v.getZ()/radius);
	}
	
	public double getRadius() {
		return radius;
	}

	public double getTheta() {
		return theta;
	}

	public double getRho() {
		return rho;
	}

	public VectorD getVectorD()
	{
		return new VectorD(radius*Math.cos(theta)*Math.sin(rho),
				radius*Math.sin(theta)*Math.sin(rho),
				radius*Math.acos(theta));
	}

	public PolarCoordinate getNormal() {
		return new PolarCoordinate(1.0,theta,rho);
	}
}
