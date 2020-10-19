package com.shadley000.util;

public class UniqueId {

	private static long currentId = 0;
	
	public static long getNewId() {currentId++; return currentId;}
	public void reset() {currentId=0;}
}
