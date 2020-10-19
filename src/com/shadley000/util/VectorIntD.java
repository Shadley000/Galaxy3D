package com.shadley000.util;

import java.io.Serializable;

public class VectorIntD implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int x;
	private int y;
	private int z;
	private double length;

	public VectorIntD() {
		x = 0;
		y = 0;
		z = 0;
		length = 0;
	}

	public VectorIntD(VectorIntD p1, VectorIntD p2) {
		x = p1.x - p2.x;
		y = p1.y - p2.y;
		z = p1.z - p2.z;
		length = Math.sqrt(x * x + y * y + z * z);
	}

	public VectorIntD(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		length = Math.sqrt(x * x + y * y + z * z);
	}
	
	public VectorIntD(VectorD v1) {
		this.x = (int) v1.getX();
		this.y = (int) v1.getY();
		this.z = (int) v1.getZ();
		length = Math.sqrt(x * x + y * y + z * z);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public double length() {
		return length;
	}


	public VectorIntD add(VectorIntD b) {
		return new VectorIntD(x + b.getX(), y + b.getY(), z + b.getZ());
	}

	public VectorIntD subtract(VectorIntD b) {
		return new VectorIntD(x - b.getX(), y - b.getY(), z - b.getZ());
	}

	public int getVolume() {
		return x*y*z;
	}
	public int getSurfaceArea() {
		return 2*(x*y+x*z+y*z);
	}
	
	public String toString()
	{
		return String.format("[%d, %d, %d]",x,y,z);
	}
	public static VectorIntD parseVectorIntD(String string)
	{
		String strArr[] = string.trim().split(",");
		int x = Integer.parseInt(strArr[0].substring(1));
		int y = Integer.parseInt(strArr[1]);
		int z = Integer.parseInt(strArr[2].substring(0, strArr[2].length()-1));
		return new VectorIntD(x,y,z);
	}
}
