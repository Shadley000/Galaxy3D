package com.shadley000.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shadley000.util.CSVLine;
import com.shadley000.util.VectorIntD;

public class Attachment implements Serializable, Cloneable
{

	private static final long serialVersionUID = 1L;

	static public final int STATE_OFF = 0;
	static public final int STATE_LOADING = 1;
	static public final int STATE_RUNNING = 2;
	static public final int STATE_FINISHING = 3;

	static public final int MODE_DESTROYED = -1;
	static public final int MODE_OFF = 0;
	static public final int MODE_ON = 1;
	static public final int MODE_AUTO = 2;

	protected String name;
	protected long id;
	protected VectorIntD position;
	protected VectorIntD dimension;
	protected Entity parentEntity;

	protected List<Recipe> recipeQueue = new ArrayList<>();
	protected Recipe currentRecipe = null;
	protected long maxIterationCounts = Long.MAX_VALUE;
	double lowInputLevelThreshold = 0.1;
	double lowOutputLevelThreshold = 0.1;
	double highInputLevelThreshold = 0.9;
	double highOutputLevelThreshold = 0.9;
	protected Recipe overhaulRecipe = null;
	protected double failedInputDamage = 0.0;
	protected double failedOutputDamage = 0.0;
	protected boolean isCycleing = true;
	protected double lowHealthCutoff = 0.25;

	protected Integer currentState = STATE_OFF;
	protected int mode = MODE_OFF;

	protected long stateStartTime = 0;
	protected long iterationCount = 0;
	protected double health = 1.0;
	protected boolean lowInput = false;
	protected boolean highInput = false;
	protected boolean lowOutput = false;
	protected boolean highOutput = false;
	protected boolean resourceStarved = false;
	protected boolean outputBlocked = false;

	protected boolean isAuto_CurrentlyRunning = false;

	protected Map<Resource, StorageUnit> storageMap = new HashMap<Resource, StorageUnit>();
	protected Map<Resource, StorageUnit> inputResourceBufferMap = new HashMap<>();
	protected Map<Resource, StorageUnit> outputResourceBufferMap = new HashMap<>();

	public Attachment()
	{
	}

	public void update()
	{
		if (health < 0.01)
			mode = MODE_DESTROYED;
		switch (mode)
		{
		case MODE_DESTROYED:
			updateModeDestroyed();
			break;
		case MODE_OFF:
			updateModeOff();
			break;
		case MODE_ON:
			updateModeOn();
			break;
		case MODE_AUTO:
			updateModeAuto();
			break;
		}

	}

	public void updateModeDestroyed()
	{
		if (health > lowHealthCutoff)
		{
			mode = MODE_OFF;
		}
	}

	public void updateModeOff()
	{ // does nothing
	}

	public void updateModeOn()
	{ // runs until dead or blocked
		switch (currentState)
		{
		case STATE_OFF:
			if (!recipeQueue.isEmpty())
				currentRecipe = recipeQueue.remove(0);
			buildInputResourceBuffer();
			stateStartTime = System.currentTimeMillis();
			currentState = STATE_LOADING;
			break;
		case STATE_LOADING:
			loadInputResourceBuffer();
			if (isInputBufferFull())
			{
				resourceStarved = false;
				currentState = STATE_RUNNING;
				stateStartTime = System.currentTimeMillis();
			} else
			{
				resourceStarved = true;
				causeDamage(failedInputDamage);
			}

		case STATE_RUNNING:
			if (currentRecipe != null)
			{
				if (stateStartTime + currentRecipe.getCycleTime() < System.currentTimeMillis())
				{
					buildOutputResourceBuffer();
					currentState = STATE_FINISHING;
					stateStartTime = System.currentTimeMillis();
				}
			} else
			{
				currentState = STATE_OFF;
				stateStartTime = System.currentTimeMillis();
			}
			break;
		case STATE_FINISHING:
			updateStateFinishing();
			break;
		}
	}

	public void updateModeAuto()
	{ // safety features to auto start and stop
		switch (currentState)
		{
		case STATE_OFF:
			if (isMaintenanceRequired())
			{
				recipeQueue.add(0, overhaulRecipe);
			} else if (health < lowHealthCutoff)
			{
				currentRecipe = null;
				mode = MODE_OFF;
			} else if (!recipeQueue.isEmpty())
			{
				currentRecipe = recipeQueue.remove(0);
			}
			if (currentRecipe != null)
			{
				levelsCheck();
				if (lowInput || highOutput)
				{
					isAuto_CurrentlyRunning = false;
				} else if (highInput || lowOutput)
				{
					isAuto_CurrentlyRunning = true;
				}

				if (isAuto_CurrentlyRunning)
				{
					buildInputResourceBuffer();
					stateStartTime = System.currentTimeMillis();
					currentState = STATE_LOADING;
				}
			}
			break;
		case STATE_LOADING:
			if (health < lowHealthCutoff)
			{
				currentState = STATE_OFF;
				mode = MODE_OFF;
				returnInputResourceBuffer();
			} else
			{
				resourceStarved = false;
				loadInputResourceBuffer();
				if (isInputBufferFull())
				{
					resourceStarved = false;
					currentState = STATE_RUNNING;
					stateStartTime = System.currentTimeMillis();
				} else
				{
					resourceStarved = true;
					causeDamage(this.failedInputDamage);

				}
			}
			break;
		case STATE_RUNNING:
			if (health < lowHealthCutoff)
			{
				mode = MODE_OFF;
			} else if (stateStartTime + currentRecipe.getCycleTime() < System.currentTimeMillis())
			{
				buildOutputResourceBuffer();
				currentState = STATE_FINISHING;
				stateStartTime = System.currentTimeMillis();
			}
			break;
		case STATE_FINISHING:
			updateStateFinishing();
			break;
		}
	}

	public void updateStateFinishing()
	{
		outputBlocked = false;
		emptyOutputResourceBuffer();
		if (isOutputBufferEmpty())
		{
			outputBlocked = false;
			currentState = STATE_OFF;
			stateStartTime = System.currentTimeMillis();
			iterationCount++;

			if (isCycleing && recipeQueue.isEmpty())
			{
				recipeQueue.add(currentRecipe);
			} else
				currentRecipe = null;
		} else
			outputBlocked = true;
	}

	public void levelsCheck()
	{
		for (Resource resource : currentRecipe.getInputList().keySet())
		{
			StorageUnit storageUnit = parentEntity.getStorage(resource);
			if (storageUnit.getFillRatio() < lowInputLevelThreshold)
			{
				lowInput = true;
			}
			if (storageUnit.getFillRatio() > highInputLevelThreshold)
			{
				highInput = true;
			}
		}
		for (Resource resource : currentRecipe.getOutputList().keySet())
		{
			StorageUnit storageUnit = parentEntity.getStorage(resource);
			if (storageUnit.getFillRatio() < lowOutputLevelThreshold)
			{
				lowOutput = true;
			}
			if (storageUnit.getFillRatio() > highOutputLevelThreshold)
			{
				highOutput = true;
			}
		}

	}

	static public Attachment parse(CSVLine csvLine) throws Exception
	{
		Attachment attachment = new Attachment();

		try
		{
			attachment.setName(csvLine.getColumn(0).trim());
			attachment.setRecipe(GlobalVar.recipeMap.get(csvLine.getColumn(1).trim()));
			attachment.setOverHaulRecipe(GlobalVar.recipeMap.get(csvLine.getColumn(2).trim()));
			attachment.setMaxIterationCounts(Long.parseLong(csvLine.getColumn(3).trim()));
			attachment.setDimension(VectorIntD.parseVectorIntD(csvLine.getColumn(4)));
			attachment.setMode(Attachment.parseMode(csvLine.getColumn(5)));
			attachment.failedInputDamage = Double.parseDouble(csvLine.getColumn(6));
			attachment.failedOutputDamage = Double.parseDouble(csvLine.getColumn(7));
			attachment.lowInputLevelThreshold = Double.parseDouble(csvLine.getColumn(8));
			attachment.lowOutputLevelThreshold = Double.parseDouble(csvLine.getColumn(9));
			attachment.highInputLevelThreshold = Double.parseDouble(csvLine.getColumn(10));
			attachment.highOutputLevelThreshold = Double.parseDouble(csvLine.getColumn(11));

		} catch (NumberFormatException ex)
		{
			throw new Exception(" numberFormatException \n" + ex.getMessage());
		}
		return attachment;
	}

	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	public void setMaxIterationCounts(long max)
	{
		maxIterationCounts = max;
	}

	public long getMaxIterationCounts()
	{
		return maxIterationCounts;
	}

	public long getRemainingIterationCounts()
	{
		return maxIterationCounts - iterationCount;
	}

	public long getTimeUntilMaintenace()
	{
		return getRemainingIterationCounts() * currentRecipe.getCycleTime();
	}

	public void setMode(int mode)
	{
		this.mode = mode;
	}

	public int getMode()
	{
		return mode;
	}

	public void addStorageUnit(StorageUnit storageUnit)
	{
		storageMap.put(storageUnit.getResource(), storageUnit);
	}

	public void setParentEntity(Entity entity)
	{
		this.parentEntity = entity;
	}

	public String getName()
	{
		return name;
	}

	public long getId()
	{
		return id;
	}

	public void setRecipe(Recipe recipe)
	{
		currentRecipe = recipe;
		iterationCount = 0;
	}

	public long getIterationCount()
	{
		return iterationCount;
	}

	public void setName(String string)
	{
		this.name = string;

	}

	public int getState()
	{
		return currentState;
	}

	public Map<Resource, StorageUnit> getStorageUnits()
	{
		return storageMap;
	}

	public double completionRatio()
	{
		if (currentRecipe == null)
			return 0.0;

		long endTime = stateStartTime + currentRecipe.getCycleTime();
		long deficit = endTime - System.currentTimeMillis();
		if (deficit < 0.0)
			return 1.0;
		return 1.0 - (((double) deficit) / ((double) currentRecipe.getCycleTime()));
	}

	public boolean isInputBufferFull()
	{
		for (StorageUnit resourceBuffer : inputResourceBufferMap.values())
		{
			if (!resourceBuffer.isFull())
				return false;
		}
		return true;
	}

	public boolean isOutputBufferEmpty()
	{
		for (StorageUnit resourceBuffer : outputResourceBufferMap.values())
		{
			if (!resourceBuffer.isEmpty())
				return false;
		}
		return true;

	}

	public boolean isMaintenanceRequired()
	{
		return maxIterationCounts <= iterationCount;
	}

	public void loadInputResourceBuffer()
	{
		for (StorageUnit resourceBuffer : inputResourceBufferMap.values())
		{
			parentEntity.takeResource(resourceBuffer);
		}
	}

	public void returnInputResourceBuffer()
	{
		for (StorageUnit resourceBuffer : inputResourceBufferMap.values())
		{
			parentEntity.addResource(resourceBuffer);
		}
	}

	public void buildInputResourceBuffer()
	{
		if (currentRecipe == null)
			return;
		for (Resource resource : currentRecipe.getInputList().keySet())
		{
			inputResourceBufferMap.put(resource, new StorageUnit(resource, "Buffer", 0, 0.0, currentRecipe.getInputResourceQuantity(resource)));
		}
	}

	public void buildOutputResourceBuffer()
	{
		if (currentRecipe == null)
			return;
		for (Resource resource : currentRecipe.getOutputList().keySet())
		{
			double desiredQuantity = currentRecipe.getOutputResourceQuantity(resource);
			outputResourceBufferMap.put(resource, new StorageUnit(resource, "Buffer", 0, desiredQuantity, desiredQuantity));
		}
	}

	public void emptyOutputResourceBuffer()
	{
		for (StorageUnit resourceBuffer : outputResourceBufferMap.values())
		{
			parentEntity.addResource(resourceBuffer);

			if (!resourceBuffer.isEmpty())
			{ // if there is any remaining and no storage space left, clear the buffer
				if (resourceBuffer.getResource().isDisposable())
				{
					resourceBuffer.setQuantity(0.0);
				}

				if (resourceBuffer.getResource().isDamaging())
				{
					causeDamage(failedOutputDamage);
				}
			}
		}

	}

	private void causeDamage(double d)
	{
		health -= d;
		if (health < 0.01)
		{
			health = 0.0;
			mode = MODE_DESTROYED;
		}
	}

	public void addResource(StorageUnit resourceBuffer)
	{
		StorageUnit storageUnit = storageMap.get(resourceBuffer.getResource());
		if (storageUnit != null)
			storageUnit.fillFrom(resourceBuffer);

	}

	public void takeResource(StorageUnit resourceBuffer)
	{
		StorageUnit storageUnit = storageMap.get(resourceBuffer.getResource());
		if (storageUnit != null)
			resourceBuffer.fillFrom(storageUnit);

	}

	public VectorIntD getPosition()
	{
		return position;
	}

	public void setPosition(VectorIntD position)
	{
		this.position = position;
	}

	public VectorIntD getDimension()
	{
		return dimension;
	}

	public void setDimension(VectorIntD dimension)
	{
		this.dimension = dimension;
	}

	public void setOverHaulRecipe(Recipe overhaulRecipe2)
	{
		// TODO Auto-generated method stub
		overhaulRecipe = overhaulRecipe2;

	}

	public double getHealth()
	{
		return health;
	}

	public String getStateString()
	{
		if (currentState == STATE_OFF)
			return "OFF_";
		if (currentState == STATE_LOADING)
			return "LOAD";
		if (currentState == STATE_RUNNING)
			return "RUN_";
		if (currentState == STATE_FINISHING)
			return "FIN_";
		return "ERR";
	}

	public String getModeString()
	{
		if (mode == MODE_OFF)
			return "OFF_";
		if (mode == MODE_ON)
			return "ON__";
		if (mode == MODE_AUTO)
			return "AUTO";
		return "ERR";
	}

	static public int parseMode(String str)
	{
		if ("OFF_".equalsIgnoreCase(str))
			return MODE_OFF;
		if ("ON__".equalsIgnoreCase(str))
			return MODE_ON;
		if ("AUTO".equalsIgnoreCase(str))
			return MODE_AUTO;
		return -1;
	}


}
