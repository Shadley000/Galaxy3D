package com.shadley000.util;

public class Vector2D {

	double x;
	double y;

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	static public Vector2D add(Vector2D v1, Vector2D v2) {
		return new Vector2D(v1.x + v2.x, v1.y + v2.y);
	}

	static public Vector2D subtract(Vector2D v1, Vector2D v2) {
		return new Vector2D(v1.x - v2.x, v1.y - v2.y);
	}

	static public double dotProduct(Vector2D v1, Vector2D v2) {
		return v1.x * v2.x + v1.y * v2.y;
	}

	public double length() {
		return Math.sqrt(x * x + y * y);
	}
}
