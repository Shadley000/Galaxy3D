package com.shadley000.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.shadley000.util.MathTool;
import com.shadley000.util.Sphere;
import com.shadley000.util.VectorD;

public class Entity implements Serializable, Cloneable
{

	private static final long serialVersionUID = 1L;

	protected long id;
	protected String name;
	protected boolean immovable;
	protected Sphere position;
	protected VectorD velocity;
	protected VectorD acceleration;
	protected VectorD force;

	public VectorD getForce()
	{
		return force;
	}

	public void setForce(VectorD force)
	{
		this.force = force;
	}

	protected double mass;

	protected long lastUpdateTime;
	protected List<Attachment> attachments = new ArrayList<>();
	protected Long lock;

	public Entity()
	{
		position = new Sphere(new VectorD(), 0);
		velocity = new VectorD();
		id = 0;
		name = "";
		lastUpdateTime = System.currentTimeMillis();
		immovable = true;
		lock = MathTool.getRandomLong(Long.MAX_VALUE);

	}
	
	public void strip()
	{
		attachments = new ArrayList<>();
		lock = (long) -1;
	}
	
	public boolean unlock(Long key) {
		return(lock.equals(key));
	}
	
	public boolean unlock(Set<Long> keySet) {
		return(keySet.contains(lock));
	}
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	public boolean isImmovable()
	{
		return immovable;
	}

	public void setImmovable(boolean immovable)
	{
		this.immovable = immovable;
	}

	public Sphere getPosition()
	{
		return position;
	}

	public VectorD getAcceleration()
	{
		return acceleration;
	}

	public long getLastUpdateTime()
	{
		return lastUpdateTime;
	}

	public List<Attachment> getAttachments()
	{
		return attachments;
	}

	public VectorD getVelocity()
	{
		return velocity;
	}

	public double getMass()
	{
		return mass;
	}

	public void setMass(double mass)
	{
		this.mass = mass;
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

	public void update()
	{

		long currentTime = System.currentTimeMillis();
		long deltaTime = currentTime - lastUpdateTime;
		lastUpdateTime = currentTime;

		if (!immovable)
		{
			if (force!=null && force.length()>0.0001) {
				acceleration = force.scale(1/mass);
				if (acceleration != null && acceleration.length() > 0.0001)
				{
					velocity = velocity.add(acceleration.scale(((double) deltaTime) / 1000.0));
				}	
			}
			

			if (velocity.length() > 0.0001)
			{
				position.move(velocity.scale(((double) deltaTime) / 1000.0));
			}
		}

		for (Attachment attachment : attachments)
			attachment.update();
	}

	public void addResource(StorageUnit resourceBuffer)
	{
		for (Attachment attachment : attachments)
		{
			attachment.addResource(resourceBuffer);
			if (resourceBuffer.isEmpty())
				return;
		}

	}

	public void takeResource(StorageUnit resourceBuffer)
	{
		for (Attachment attachment : attachments)
		{
			attachment.takeResource(resourceBuffer);
			if (resourceBuffer.isFull())
				return;
		}

	}

	public void addAttachment(Attachment attachment)
	{
		attachments.add(attachment);
		attachment.setParentEntity(this);
	}

	public StorageUnit getStorage(Resource resource)
	{
		StorageUnit storageUnit = new StorageUnit(resource, resource.getName(), 0, 0.0, 0.0);
		for (Attachment attachment : attachments)
		{
			StorageUnit aStorageUnit = attachment.getStorageUnits().get(resource);
			if (aStorageUnit != null)
			{
				storageUnit.setQuantity(storageUnit.getQuantity() + aStorageUnit.getQuantity());
				storageUnit.setMaxQuantity(storageUnit.getMaxQuantity() + aStorageUnit.getMaxQuantity());
			}
		}
		return storageUnit;
	}

	public Map<Resource, StorageUnit> buildShipStoreReport()
	{
		Map<Resource, StorageUnit> totalStorageMap = new HashMap<>();
		for (Attachment attachment : attachments)
		{
			Map<Resource, StorageUnit> storageMap = attachment.getStorageUnits();
			for (Resource resource : storageMap.keySet())
			{
				StorageUnit storageUnit = storageMap.get(resource);
				StorageUnit totalStorageUnit = totalStorageMap.get(resource);
				if (totalStorageUnit == null)
				{
					totalStorageUnit = new StorageUnit(resource, resource.getName() + " total", 0, storageUnit.getQuantity(), storageUnit.getMaxQuantity());
					totalStorageMap.put(resource, totalStorageUnit);
				} else
				{
					totalStorageUnit.setQuantity(totalStorageUnit.getQuantity() + storageUnit.getQuantity());
					totalStorageUnit.setMaxQuantity(totalStorageUnit.getMaxQuantity() + storageUnit.getMaxQuantity());
				}

			}
		}
		return totalStorageMap;
	}

	public void setPosition(Sphere position)
	{
		this.position = position;
	}

	public void setVelocity(VectorD velocity)
	{
		this.velocity = velocity;
	}

	public void setAcceleration(VectorD acceleration)
	{
		this.acceleration = acceleration;
	}

	public void setId(long newId)
	{
		id = newId;
		
	}

	public boolean detect(Entity entity)
	{
		VectorD seperation = getPosition().getCenter().subtract(entity.getPosition().getCenter());
		double detectionRange =100;
		return seperation.length()<detectionRange;
		
		
	}

	public VectorD getAcceleration(VectorD destination)
	{
		double accellerationMax = 1.0;
		VectorD pos = position.getCenter();
		
		// r1 = r0 + v0t - at^2/2
		// v1 = v0 + at
		// maximum acceleration = a
		// stopping distance,  v0 = -at    
		// stopping time = -v0/a
		// 
		// stopping at a specified location
		// r1 - r0 = v0t - at^2/2
		// r1 - r0 = -v0^2/a - v0^2/a
		// r1 - r0 = -3 v0^2 / 2a
		
		// accelerate at max (t1) 
		//         v0 shows intersection with destination
		//         stopping distance approaches
		// coast until stopping distance is reached (t2)
		// brake until stopped (t3)
		
		
		
		return pos;
		
		
	}
}
