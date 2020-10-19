package com.shadley000.util;

public class Plane {

	// Hessian normal N DOT X = -P

	VectorD normal;
	double distanceToOrigin; // distance to origin

	// Hessian format
	public Plane(double distanceToOrigin, VectorD normal) {

		this.normal = normal.normalize();
		this.distanceToOrigin = distanceToOrigin;

	}

	// defined by three points
	public Plane(VectorD a, VectorD b, VectorD c) {

		VectorD ab = b.subtract(a);
		VectorD ac = c.subtract(a);
		this.normal = VectorD.getCrossProduct(ab, ac).normalize();

		 distanceToOrigin = -VectorD.dotProduct(a, normal);

	}

	// defined by equation of a plane
	public Plane(double a, double b, double c, double d) {
		VectorD v = new VectorD(a, b, c);
		normal = v.normalize();
		distanceToOrigin = d / v.length();
	}

	public double distancetoPoint(VectorD v) {
		return VectorD.dotProduct(normal, v) + distanceToOrigin;
	}

	public double getA() {
		return normal.getX();
	}

	public double getB() {
		return normal.getY();
	}

	public double getC() {
		return normal.getZ();
	}

	public double getD() {
		return distanceToOrigin*normal.length();
	}
}
