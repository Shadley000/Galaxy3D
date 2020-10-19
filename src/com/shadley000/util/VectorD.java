package com.shadley000.util;

import java.io.Serializable;

public class VectorD  implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double x;
	private double y;
	private double z;
	private double length;

	public VectorD()
	{
		x = 0;
		y = 0;
		z = 0;
		length = 0;
	}

	/*public void setX(double x)
	{
		this.x = x;
		length = Math.sqrt(x * x + y * y + z * z);
	}

	public void setY(double y)
	{
		this.y = y;
		length = Math.sqrt(x * x + y * y + z * z);
	}

	public void setZ(double z)
	{
		this.z = z;
		length = Math.sqrt(x * x + y * y + z * z);
	}
*/
	public VectorD(VectorD p1, VectorD p2)
	{
		x = p1.x - p2.x;
		y = p1.y - p2.y;
		z = p1.z - p2.z;
		length = Math.sqrt(x * x + y * y + z * z);
	}

	public VectorD(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		length = Math.sqrt(x * x + y * y + z * z);
	}
	public VectorD(VectorIntD v1) {
		this.x =  v1.getX();
		this.y =  v1.getY();
		this.z =  v1.getZ();
		length = Math.sqrt(x * x + y * y + z * z);
	}
	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double getZ()
	{
		return z;
	}

	public double length()
	{
		return length;
	}

	public VectorD scale(double scale)
	{
		return new VectorD(scale * x, scale * y, scale * z);
	}

	public VectorD divide(double factor)
	{
		return new VectorD(x / factor, y / factor, z / factor);
	}

	public VectorD add(VectorD b)
	{
		return new VectorD(x + b.getX(), y + b.getY(), z + b.getZ());
	}

	public VectorD subtract(VectorD b)
	{
		return new VectorD(x - b.getX(), y - b.getY(), z - b.getZ());
	}

	public VectorD normalize()
	{
		return scale(1 / length());
	}

	public static VectorD getCrossProduct(VectorD a, VectorD b)
	{
		return new VectorD(a.getY() * b.getZ() - a.getZ() * b.getY(), -a.getX() * b.getZ() + a.getZ() * b.getX(), a.getX() * b.getY() - a.getY() * b.getX());
	}

	public static double dotProduct(VectorD a, VectorD b)
	{
		return a.getX() * b.getX() + a.getY() * b.getY() + a.getZ() * b.getZ();
	}

	static public VectorD getRandomDirectionVectorD(double radius)
	{
		VectorD v = new VectorD(MathTool.getRandom(-1.0, 1.0), MathTool.getRandom(-1.0, 1.0), MathTool.getRandom(-1.0, 1.0));
		return v.normalize().scale(radius);
	}

	static public VectorD getRandomDirectionGausianLengthVectorD(double averageRadius, double standardDeviation)
	{
		return getRandomDirectionVectorD(MathTool.getRandomGausian(averageRadius, standardDeviation));

	}

	public double getVolume()
	{
		return x * y * z;
	}

	public double getSurfaceArea()
	{
		return 2 * (x * y + x * z + y * z);
	}

	public String toString()
	{
		return String.format("[%f, %f, %f]", x, y, z);
	}
}
