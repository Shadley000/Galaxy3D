package com.shadley000.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.shadley000.framework.exceptions.CollisionException;
import com.shadley000.server.Security;
import com.shadley000.util.CSVLine;
import com.shadley000.util.MathTool;
import com.shadley000.util.Sphere;
import com.shadley000.util.VectorD;

public class GlobalVar
{
	public static Map<String, Resource> resourceMap = new HashMap<>();
	public static Map<String, Recipe> recipeMap = new HashMap<>();
	public static Map<String, Attachment> attachmentTemplateMap = new HashMap<>();
	public static Map<String, Entity> entityMap = new HashMap<>();
	private static SolarSystem solarSystem = null;
	private static Security security = null;
	private static long idCounter = 0;
	public static boolean done = false;

	public static SolarSystem getSolarSystem()
	{
		return solarSystem;
	}

	public static long getNewId()
	{
		idCounter++;
		return idCounter;
	}

	public static void buildGlobalVar()
	{

		try
		{
			String dataroot = "C:\\eclipse-workspace\\Galaxy3D\\WebContent\\WEB-INF\\";
			GlobalVar.buildResourceMap(dataroot + "Resources.csv");
			GlobalVar.buildRecipeMap(dataroot + "Recipes.csv");
			GlobalVar.buildAttachmentTemplateMap(dataroot + "AttachmentTemplates.csv");
			GlobalVar.buildStandardEntityMap(dataroot + "Entity.csv");
			GlobalVar.buildSolarSystem(dataroot + "SolarSystem1.csv");
			GlobalVar.security = new Security(dataroot + "userdb.csv");
			loadUserKeys(dataroot + "userKeys.csv");

			new Thread(solarSystem).start();
			new Thread(security).start();

		} catch (Exception e1)
		{
			e1.printStackTrace();

		}
	}

	private static void loadUserKeys(String fileName) throws IOException
	{
		File csvFile = new File(fileName);

		BufferedReader reader = new BufferedReader(new FileReader(csvFile));

		String nextLine;

		try
		{
			while ((nextLine = reader.readLine()) != null)
			{
				CSVLine csvLine = new CSVLine(nextLine);
				String userName = csvLine.getColumn(0).trim();
				Long key = Long.parseLong(csvLine.getColumn(1).trim());
				security.getActiveUser(userName).addKey(key);
			}
		} finally
		{
			reader.close();
		}
	}

	private static void buildAttachmentTemplateMap(String fileName) throws Exception
	{
		File csvFile = new File(fileName);

		int lineNum = 0;

		BufferedReader reader = new BufferedReader(new FileReader(csvFile));

		String nextLine;

		try
		{
			Attachment attachment = null;
			while ((nextLine = reader.readLine()) != null)
			{
				CSVLine csvLine = new CSVLine(nextLine);

				if (csvLine.getColumn(0).trim().isEmpty() || csvLine.getColumn(0).startsWith("//"))
				{ // skip blanks
				} else
				{
					if (csvLine.getColumn(0).equalsIgnoreCase("STORAGE"))
					{
						String resourceName = csvLine.getColumn(1).trim();
						Resource resource = resourceMap.get(resourceName);
						Double quantity = Double.parseDouble(csvLine.getColumn(2));
						Double maxQuantity = Double.parseDouble(csvLine.getColumn(3));
						long id = 0;
						StorageUnit storageUnit = new StorageUnit(resource, resourceName, id, quantity, maxQuantity);
						attachment.addStorageUnit(storageUnit);
					} else
					{
						try
						{
							attachment = Attachment.parse(csvLine);

							attachmentTemplateMap.put(attachment.getName(), attachment);
						} catch (NumberFormatException ex)
						{
							throw new Exception(fileName + " line:" + lineNum + ex.getMessage() + "\n" + nextLine);
						}
					}
				}
				lineNum++;
			}
		} finally
		{
			reader.close();
		}
	}

	private static void buildResourceMap(String fileName) throws Exception
	{
		File csvFile = new File(fileName);

		int lineNum = 0;

		BufferedReader reader = new BufferedReader(new FileReader(csvFile));

		String nextLine;

		try
		{
			while ((nextLine = reader.readLine()) != null)
			{
				CSVLine csvLine = new CSVLine(nextLine);

				if (csvLine.getColumn(0).trim().isEmpty() || csvLine.getColumn(0).startsWith("//"))
				{ // skip blanks
				} else
				{
					Resource resource = null;
					try
					{
						resource = Resource.parse(csvLine);
					} catch (Exception e)
					{
						throw new Exception(fileName + " line:" + lineNum + " " + e.getMessage() + "\n" + nextLine);
					}
					if (GlobalVar.resourceMap.containsKey(resource.getName()))
						throw new Exception(fileName + " line:" + lineNum + " duplicate resource name:" + resource.getName() + "\n" + nextLine);

					GlobalVar.resourceMap.put(resource.getName(), resource);
				}
				lineNum++;
			}
		} finally
		{
			reader.close();
		}
	}

	private static void buildRecipeMap(String fileName) throws FileNotFoundException, IOException, Exception
	{
		File csvFile = new File(fileName);

		int lineNum = 0;

		BufferedReader reader = new BufferedReader(new FileReader(csvFile));
		String nextLine;
		Recipe recipe = null;

		try
		{
			while ((nextLine = reader.readLine()) != null)

			{
				CSVLine csvLine = new CSVLine(nextLine);

				if (csvLine.getColumn(0).trim().isEmpty() || csvLine.getColumn(0).startsWith("//"))
				{// skip blanks
				} else if (csvLine.getColumn(0).equalsIgnoreCase("INPUT"))
				{
					String resourceName = csvLine.getColumn(1);
					double quantity = Double.parseDouble(csvLine.getColumn(2));
					Resource resource = GlobalVar.resourceMap.get(resourceName);
					if (resource == null)
						throw new Exception(fileName + " line:" + lineNum + " resource not found:" + resourceName + "\n" + nextLine);
					recipe.addInput(GlobalVar.resourceMap.get(resourceName), quantity);

				} else if (csvLine.getColumn(0).equalsIgnoreCase("OUTPUT"))
				{
					String resourceName = csvLine.getColumn(1);
					double quantity = Double.parseDouble(csvLine.getColumn(2));
					Resource resource = GlobalVar.resourceMap.get(resourceName);
					if (resource == null)
						throw new Exception(fileName + " line:" + lineNum + " resource not found:" + resourceName + "\n" + nextLine);
					recipe.addOutput(resource, quantity);

				} else
				{
					String name = csvLine.getColumn(0);
					if (GlobalVar.recipeMap.containsKey(name))
					{
						reader.close();
						throw new Exception(fileName + " line:" + lineNum + " duplicate recipe name:" + name + "\n" + nextLine);
					}
					long cycleTime = 1000 * Long.parseLong(csvLine.getColumn(1));
					recipe = new Recipe(name, cycleTime);
					GlobalVar.recipeMap.put(recipe.getName(), recipe);
				}

				lineNum++;
			}
		} finally
		{
			reader.close();
		}

	}

	private static void buildStandardEntityMap(String fileName) throws FileNotFoundException, IOException, Exception
	{
		File csvFile = new File(fileName);

		int lineNum = 0;

		BufferedReader reader = new BufferedReader(new FileReader(csvFile));
		String nextLine;
		Entity entity = null;

		try
		{
			while ((nextLine = reader.readLine()) != null)
			{
				CSVLine csvLine = new CSVLine(nextLine);

				if (csvLine.getColumn(0).trim().isEmpty() || csvLine.getColumn(0).startsWith("//"))
				{// skip blanks
				} else if (csvLine.getColumn(0).equalsIgnoreCase("NAME"))
				{
					String entityName = csvLine.getColumn(1);
					if (GlobalVar.entityMap.get(entityName) != null)
						throw new Exception(fileName + " line:" + lineNum + " duplicate standard entity:" + entityName + "\n" + nextLine);

					entity = new Entity();
					entity.setName(entityName);
					entity.setPosition(new Sphere(new VectorD(0.0, 0.0, 0.0), 1.0));
					entity.setVelocity(new VectorD(1.0, 1.0, 1.0));
					entity.setImmovable(false);
					entityMap.put(entityName, entity);

				} else if (csvLine.getColumn(0).equalsIgnoreCase("ATTACHMENT"))
				{
					String attachmentName = csvLine.getColumn(1);
					Attachment attachment = GlobalVar.attachmentTemplateMap.get(attachmentName);
					if (attachment == null)
						throw new Exception(fileName + " line:" + lineNum + " ATTACHMENT not found:" + attachmentName + "\n" + nextLine);
					entity.addAttachment((Attachment) attachment.clone());
				} else if (csvLine.getColumn(0).equalsIgnoreCase("IMMOVABLE"))
				{
					entity.setImmovable(Boolean.parseBoolean(csvLine.getColumn(1)));
				} else
				{

				}

				lineNum++;
			}
		} finally
		{
			reader.close();
		}

	}

	private static void buildSolarSystem(String string) throws CloneNotSupportedException, CollisionException
	{
		solarSystem = new SolarSystem();
		{
			Entity aEntity = (Entity) entityMap.get("Planet").clone();
			aEntity.setPosition(new Sphere(new VectorD(0, 0, 0), 1.0));
			aEntity.setVelocity(new VectorD(0, 0, 0));
			aEntity.setName("A Planet");
			aEntity.setId(GlobalVar.getNewId());
			aEntity.lock = (long) 0;
			try {			solarSystem.add(aEntity);}
			catch(CollisionException e) {
				System.out.println("Collision Exception");
			}
		}

		for(int i=0;i<20;i++){
			Entity aEntity = (Entity) entityMap.get("Base").clone();

			VectorD position = VectorD.getRandomDirectionVectorD(10000);
			aEntity.setPosition(new Sphere(position, 1.0));
			aEntity.setVelocity(VectorD.getRandomDirectionGausianLengthVectorD(3.0, 3.0));
			aEntity.setName("A Entity 1");
			aEntity.setId(GlobalVar.getNewId());
			aEntity.lock = (long) 1;
			try {			solarSystem.add(aEntity);}
			catch(CollisionException e) {
				System.out.println("Collision Exception");
			}
		}
		for(int i=0;i<20;i++){
			Entity aEntity = (Entity) entityMap.get("Base").clone();
			VectorD position = VectorD.getRandomDirectionVectorD(10000);
			aEntity.setPosition(new Sphere(position, 1.0));
			aEntity.setVelocity(VectorD.getRandomDirectionGausianLengthVectorD(3.0, 3.0));
			aEntity.setName("A Entity 2");
			aEntity.setId(GlobalVar.getNewId());
			aEntity.lock = (long) 2;
			try {			solarSystem.add(aEntity);}
			catch(CollisionException e) {
				System.out.println("Collision Exception");
			}
		}
		for(int i=0;i<1000;i++){
			Entity aEntity = (Entity) entityMap.get("Base").clone();

			VectorD position = VectorD.getRandomDirectionVectorD(10000);
			aEntity.setPosition(new Sphere(position, 1.0));
			aEntity.setVelocity(VectorD.getRandomDirectionGausianLengthVectorD(3.0, 3.0));
			aEntity.setName("rnds");
			aEntity.setId(GlobalVar.getNewId());
			aEntity.lock = MathTool.getRandomLong(1000);
			try {			solarSystem.add(aEntity);}
			catch(CollisionException e) {
				System.out.println("Collision Exception");
			}
		}
		
		
	}

	public static Security getSecurity()
	{
		// TODO Auto-generated method stub
		return security;
	}
}
