package com.shadley000.framework;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Recipe implements Serializable
{

	private static final long serialVersionUID = 1L;

	private String name;
	private long cycleTime;
	private Map<Resource, Double> inputMap = new HashMap<>();
	private Map<Resource, Double> outputMap = new HashMap<>();

	public Recipe(String name, long cycleTime)
	{
		this.name = name;
		this.cycleTime = cycleTime;
	}

	public void addInput(Resource resource, Double quantity)
	{
		inputMap.put(resource, quantity);
	}

	public void addOutput(Resource resource, Double quantity)
	{
		outputMap.put(resource, quantity);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public long getCycleTime()
	{
		return cycleTime;
	}

	public void setCycleTime(long cycleTime)
	{
		this.cycleTime = cycleTime;
	}

	public Map<Resource, Double> getInputList()
	{
		return inputMap;
	}

	public void setInputList(Map<Resource, Double> inputList)
	{
		this.inputMap = inputList;
	}

	public Map<Resource, Double> getOutputList()
	{
		return outputMap;
	}

	public void setOutputList(Map<Resource, Double> outputList)
	{
		this.outputMap = outputList;
	}

	public double getInputResourceQuantity(Resource resource)
	{
		return inputMap.get(resource);
	}

	public double getOutputResourceQuantity(Resource resource)
	{
		return outputMap.get(resource);
	}

}
