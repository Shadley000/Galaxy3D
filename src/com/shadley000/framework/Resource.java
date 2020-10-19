package com.shadley000.framework;

import java.io.Serializable;

import com.shadley000.util.CSVLine;

public class Resource implements Serializable
{

	private static final long serialVersionUID = 1L;

	public static final int GAS = 0;
	public static final int LIQUID = 1;
	public static final int SOLID = 2;
	public static final int OTHER = 3;

	public static final String KILOGRAM = "Kg";
	public static final String METER = "M";
	public static final String KILOWATT = "Kw";

	public static final String LITER = "L";
	public static final String SECONDS = "s";

	private String name;
	private String units;
	private double density;
	private int physicalState;
	private boolean isDisposable;
	private boolean isDamaging;

	public Resource()
	{

	}

	public Resource(String name, double density, int physicalState, String units)
	{
		this.name = name;
		this.units = units;
		this.density = density;
		this.physicalState = physicalState;

	}

	public static Resource parse(CSVLine csvLine) throws Exception
	{
		String name = csvLine.getColumn(0);

		double density = Double.parseDouble(csvLine.getColumn(1));

		int physicalState = Resource.OTHER;
		if ("Gas".equalsIgnoreCase(csvLine.getColumn(2).trim()))
			physicalState = Resource.GAS;
		else if ("Solid".equalsIgnoreCase(csvLine.getColumn(2).trim()))
			physicalState = Resource.SOLID;
		else if ("Liquid".equalsIgnoreCase(csvLine.getColumn(2).trim()))
			physicalState = Resource.LIQUID;
		else if ("Other".equalsIgnoreCase(csvLine.getColumn(2).trim()))
			physicalState = Resource.OTHER;
		else
			// throw new Exception(fileName + " line:" + lineNum + " unknown resource type:"
			// + csvLine.getColumn(2) + "\n" + nextLine);
			throw new Exception("unknown physical state:" + csvLine.getColumn(2));

		String units = Resource.KILOGRAM;
		if ("Kg".equalsIgnoreCase(csvLine.getColumn(3).trim()))
			units = Resource.KILOGRAM;
		else if ("m".equalsIgnoreCase(csvLine.getColumn(3).trim()))
			units = Resource.METER;
		else if ("kW".equalsIgnoreCase(csvLine.getColumn(3).trim()))
			units = Resource.KILOWATT;
		else if ("l".equalsIgnoreCase(csvLine.getColumn(3).trim()))
			units = Resource.LITER;
		else if ("s".equalsIgnoreCase(csvLine.getColumn(3).trim()))
			units = Resource.SECONDS;
		else
			// throw new Exception(fileName + " line:" + lineNum + " unknown unit type:" +
			// csvLine.getColumn(3) + "\n" + nextLine);
			throw new Exception("unknown measurement type:" + csvLine.getColumn(3));

		boolean isDisposable = false;
		if ("TRUE".equalsIgnoreCase(csvLine.getColumn(4)))
		{
			isDisposable=true;
		}
		boolean isDamaging = false;
		if ("TRUE".equalsIgnoreCase(csvLine.getColumn(5)))
		{
			isDamaging=true;
		}
		Resource resource = new Resource(name, density, physicalState, units);
		
		resource.isDisposable = isDisposable;
		resource.isDamaging = isDamaging;
		return resource;
	}

	public boolean isDisposable()
	{
		return isDisposable;
	}

	public boolean isDamaging()
	{
		return isDamaging;
	}

	public String getName()
	{
		return name;
	}

	public double getDensity()
	{
		return density;
	}

	public String getUnits()
	{
		return units;
	}

	public int getPhysicalState()
	{
		return physicalState;
	}

}
