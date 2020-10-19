package com.shadley000.framework;

import java.io.Serializable;

public class StorageUnit implements Serializable, Cloneable
{
	private static final long serialVersionUID = 1L;
	private String name;
	private long id;
	private Resource resource;

	private double quantity = 0.0;
	private double maxQuantity = 0.00001;

	public StorageUnit()
	{

	}

	public StorageUnit(Resource resource, String name, long id, double initialQuantity, double maxQuantity)
	{
		this.resource = resource;
		this.name = name;
		this.id = id;
		this.maxQuantity = maxQuantity;
		this.quantity = initialQuantity;

	}

	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public Resource getResource()
	{
		return resource;
	}

	public void setResource(Resource resource)
	{
		this.resource = resource;
	}

	public double getQuantity()
	{
		return quantity;
	}

	public void setQuantity(double quantity)
	{
		this.quantity = quantity;
	}

	public double getMaxQuantity()
	{
		return maxQuantity;
	}

	public void setMaxQuantity(double maxQuantity)
	{
		this.maxQuantity = maxQuantity;
	}

	public double getDeficit()
	{
		return maxQuantity - quantity;
	}

	public void fillFrom(StorageUnit sourceStorageUnit)
	{
		if (sourceStorageUnit.getResource().equals(resource))
		{
			if (getDeficit() > sourceStorageUnit.getQuantity())
			{
				quantity += sourceStorageUnit.getQuantity();
				sourceStorageUnit.setQuantity(0.0);
			} else
			{
				sourceStorageUnit.setQuantity(sourceStorageUnit.getQuantity() - getDeficit());
				quantity = maxQuantity;
			}
		}
	}

	public double getFillRatio()
	{
		return quantity / maxQuantity;
	}

	public boolean isEmpty()
	{
		// TODO Auto-generated method stub
		return quantity <= 0.0;
	}

	public boolean isFull()
	{
		// TODO Auto-generated method stub
		return maxQuantity - quantity <= 0;
	}

}
