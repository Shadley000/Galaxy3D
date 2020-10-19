package com.shadley000.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shadley000.framework.exceptions.CollisionException;
import com.shadley000.util.Sphere;

public class SolarSystem implements Runnable

{

	protected List<Entity> entities;
	protected Map<Long, Entity> entitiesMap;

	protected long cycleTime = 1000;

	public SolarSystem()
	{
		entities = new ArrayList<Entity>();
		entitiesMap = new HashMap<Long, Entity>();
	}

	public List<Entity> getEntities()
	{
		return entities;
	}

	public void remove(Entity newEntity)
	{
		entities.remove(newEntity);
		entitiesMap.remove(newEntity.id);
	}

	public void add(Entity newEntity) throws CollisionException
	{

		List<Entity> closeEntities = findEntities(newEntity.getPosition());
		if (closeEntities.size() < 1)
		{
			entities.add(newEntity);
			entitiesMap.put(newEntity.id, newEntity);
		} else
			throw new CollisionException();
	}

	public void update()
	{
		for (Entity entity : entities)
			entity.update();
	}

	public List<Entity> findEntities(Sphere sphere)
	{

		List<Entity> closeEntities = new ArrayList<Entity>();
		for (Entity entity : entities)
		{
			if (sphere.isCollision(entity.getPosition()))
				closeEntities.add(entity);
		}
		return closeEntities;
	}

	@Override
	public void run()
	{
		long updateTime;

		long waitTimeSum=0;
		long counter = 0;
		long counterMax = 100;
		while (!GlobalVar.done)
		{
			updateTime = System.currentTimeMillis();

			update();
			long waitTime = cycleTime - (System.currentTimeMillis() - updateTime);
			waitTimeSum += waitTime;
			counter++;
			if (waitTime > 0)
			{
				try
				{
					Thread.sleep(waitTime);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			if(counter>counterMax)
			{
				long averageWaitTime = waitTimeSum/counterMax;
				System.out.println("Load ratio: "+(double)(cycleTime-averageWaitTime)/(double)cycleTime);
				waitTimeSum=0;
				counter = 0;
			}
		}
	}

}
