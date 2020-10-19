package com.shadley000.util;

public class Cone {

	VectorD origin;
	VectorD normal;
	double angle;

	public VectorD getOrigin() {
		return origin;
	}

	public VectorD getNormal() {
		return normal;
	}

	public double getAngle() {
		return angle;
	}

	
	public Cone(VectorD origin,VectorD normal, double angle) {
		this.origin = origin;this.normal = normal;
		this.angle = angle;
	}
	
	public boolean isPointInside(VectorD p1)
	{
		
		VectorD relativePosition = p1.subtract(origin);
		
		double dotProduct = VectorD.dotProduct(relativePosition.normalize(), normal);
		double viewAngle = Math.acos(dotProduct);
		return (viewAngle<angle);
	}
}
