package com.shadley000.util;

public class NameValuePair {

	private String name;
	private double value;
	
	public NameValuePair(String name, double value)
	{
		this.name = name;
		this.value = value;
	}
	
	public String getName() {return name;}
	public double getValue() {return value;}
}
