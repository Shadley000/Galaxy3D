package com.shadley000.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;

import com.shadley000.framework.Attachment;
import com.shadley000.framework.Entity;
import com.shadley000.framework.Recipe;
import com.shadley000.framework.Resource;
import com.shadley000.framework.StorageUnit;
import com.shadley000.server.Command;
import com.shadley000.server.Response;
import com.shadley000.util.VectorD;
import com.shadley000.util.VectorIntD;

public class Client
{
	static private Long key = new Long(-1);

	public static void main(String[] args)
	{
		if (args.length < 4)
		{
			System.out.println("Usage: java.exe com.shadley000.server.Client hostname port username password");
			return;
		}

		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		String username = args[2];
		String password = args[3];

		try
		{
			key = getKey(hostname, port, username, password);

			if (key >= 0)
			{
				boolean done = false;

				while (!done)
				{
					Collection<Entity> entities;
					entities = getEntities(hostname, port);

					if (entities != null)
					{
						System.out.println("Entity Count:" + entities.size());
						// printEntityReport(entities);
						printXYMap(createXYMap(entities, new VectorD(0, 0, 0), 1000, new VectorIntD(32, 32, 0)));
					} else
					{
						System.out.println("null result");
					}

					try
					{
						Thread.sleep(2000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			} else
			{
				System.out.println("Invalid user");
			}
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}

		// Console console = System.console();
		// String text = console.readLine("Enter Command [Map,Report,Quit]: ");

	}

	public static long getKey(String hostname, int port, String user, String password) throws Exception
	{
		Response response = getResponse(hostname, port, new Command(key, Command.COMMAND_LOGIN, user, password));
		return (Long) response.getObj();

	}

	@SuppressWarnings("unchecked")
	public static Collection<Entity> getEntities(String hostname, int port) throws Exception
	{
		Response response = getResponse(hostname, port, new Command(key, Command.COMMAND_ENTITY_LIST));
		return (Collection<Entity>) response.getObj();

	}

	public static Response getResponse(String hostname, int port, Command command) throws Exception
	{
		try (Socket socket = new Socket(hostname, port))
		{
			new ObjectOutputStream(socket.getOutputStream()).writeObject(command);
			Response response = (Response) new ObjectInputStream(socket.getInputStream()).readObject();
			socket.close();
			if (response.isError())
			{
				throw new Exception("Error:" + (String) response.getObj());
			}
			return response;

		} catch (UnknownHostException ex)
		{
			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex)
		{
			System.out.println("I/O error: " + ex.getMessage());
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;

	}

	public static void printEntityReport(Collection<Entity> entities)
	{
		for (Entity entity : entities)
		{

			System.out.println(entity.getName() + "\tPosition " + entity.getPosition().toString() + "\tVelocity " + entity.getVelocity().toString() + "\tMass " + entity.getMass());

			printAttachmentReport(entity.getAttachments());
			printStorageReport(entity.buildShipStoreReport());

			System.out.println("______________________________________________________________________________________________");
			System.out.println();
		}

	}

	public static void printAttachmentReport(Collection<Attachment> attachments)
	{
		String header = "";
		header += fixedLengthString("Attachment", 12) + "\t";
		header += fixedLengthString("Health", 12) + "\t";
		header += fixedLengthString("Mode", 4) + "\t";
		header += fixedLengthString("State", 5) + "\t";
		header += fixedLengthString("Completion", 10) + "\t";
		header += fixedLengthString("Maint", 8) + "\t";
		header += fixedLengthString("InFull", 5) + "\t";
		header += fixedLengthString("Storage", 5) + "\t";
		System.out.println(header);
		for (Attachment attachment : attachments)
		{
			String stateName = attachment.getStateString();
			String modeName = attachment.getModeString();
			String outputString = "";
			outputString += fixedLengthString(attachment.getName(), 12) + "\t";
			outputString += barChart(attachment.getHealth(), 12) + "\t";
			outputString += fixedLengthString(modeName, 4) + "\t";
			outputString += fixedLengthString(stateName, 5) + "\t";
			outputString += barChart(attachment.completionRatio(), 10) + "\t";
			outputString += barChart((float) attachment.getIterationCount() / (float) attachment.getMaxIterationCounts(), 8) + "\t";
			outputString += fixedLengthString("" + attachment.isInputBufferFull(), 5) + "\t";
			outputString += fixedLengthString("" + attachment.isOutputBufferEmpty(), 5);
			System.out.println(outputString);
		}
	}

	public static void printRecipeXXX(Recipe recipe)
	{
		System.out.println(recipe.getName());
		System.out.println("\t time:" + recipe.getCycleTime());
		for (Resource resource : recipe.getInputList().keySet())
		{
			System.out.println("\t input:" + resource.getName() + " " + recipe.getInputList().get(resource));

		}
		for (Resource resource : recipe.getOutputList().keySet())
		{
			System.out.println("\t output:" + resource.getName() + " " + recipe.getOutputList().get(resource));

		}
	}

	public static int[][] createXYMap(Collection<Entity> entities, VectorD center, double pixelWidth, VectorIntD pixelCount)
	{
		int image[][] = new int[pixelCount.getX()][pixelCount.getY()];

		for (int x = 0; x < pixelCount.getX(); x++)
		{
			for (int y = 0; y < pixelCount.getY(); y++)
			{
				image[x][y] = 0;
			}
		}

		VectorD upperLeft = center.subtract(new VectorD(pixelCount).scale(pixelWidth / 2.0));

		for (Entity entity : entities)
		{
			VectorIntD mapPos = new VectorIntD(entity.getPosition().getCenter().subtract(upperLeft).scale(1 / pixelWidth));
			// check for bounds
			if (mapPos.getX() >= 0 && mapPos.getX() < image.length && mapPos.getY() >= 0 && mapPos.getY() < image[0].length)
			{
				// if (entity.isImmovable())
				// image[mapPos.getX()][mapPos.getY()] += 5;
				// else
				image[mapPos.getX()][mapPos.getY()] += 1;
			}
		}
		return image;
	}

	public static void printXYMap(int image[][])
	{
		int max = Integer.MIN_VALUE;
		for (int y = 0; y < image[0].length; y++)
		{
			for (int x = 0; x < image.length; x++)
			{
				if (max < image[x][y])
					max = image[x][y];
			}
		}

		StringBuffer output = new StringBuffer();
		output.append("  ");
		for (int x = 0; x < image.length; x++)
		{
			output.append(" " + x % 10);
		}
		output.append("\n");
		for (int x = 0; x < image.length + 2; x++)
		{
			output.append("__");
		}
		output.append("\n");
		for (int y = 0; y < image[0].length; y++)
		{
			output.append(y % 10 + "|");
			for (int x = 0; x < image.length; x++)
			{
				if (image[x][y] == 0)
				{
					output.append("  ");

				} else if (image[x][y] > 9)
				{
					output.append(" *");
				} else
				{
					output.append(" " + image[x][y]);
				}

			}
			output.append("|\n");
		}
		for (int x = 0; x < image.length + 2; x++)
		{
			output.append("__");
		}
		output.append("\n");
		System.out.println(output);
	}

	public static String barChart(double ratio, int width)
	{
		String string = "|";
		;
		int stars = (int) (ratio * (width - 2));
		for (int i = 0; i < width - 2; i++)
		{
			if (i <= stars)
				string = string.concat("*");
			else
				string = string.concat(" ");
		}
		return string + "|";
	}

	public static String barChart(double ratio, double max, int width)
	{
		String string = "";
		;
		int stars = (int) (ratio * width);
		for (int i = 0; i < width; i++)
		{
			if (i <= stars)
				string = string.concat("*");
			else
				string = string.concat(" ");
		}
		return "0|" + string + "|" + max;
	}

	static public void printStorageReport(Map<Resource, StorageUnit> storageUnitMap)
	{
		System.out.println(String.format("\t%s\t%s", fixedLengthString("Resource", 24), "% Full   "));
		for (Resource resource : storageUnitMap.keySet())
		{
			StorageUnit storageUnit = storageUnitMap.get(resource);
			String name = fixedLengthString(resource.getName(), 16);
			String completionBar = barChart(storageUnit.getFillRatio(), storageUnit.getMaxQuantity(), 40);
			System.out.println(String.format("%s\t%f\t%s", name, storageUnit.getQuantity(), completionBar));
		}
	}

	public static String fixedLengthString(String string, int length)
	{
		return String.format("%1$" + length + "s", string);
	}

	public static void findMapBounds(Map<Long, Entity> entities, VectorD center, double pixelWidth)
	{

		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		double maxZ = Double.MIN_VALUE;

		for (Entity entity : entities.values())
		{
			VectorD pos = entity.getPosition().getCenter();
			if (pos.getX() < minX)
			{
				minX = pos.getX();
			} else if (pos.getX() > maxX)
			{
				maxX = pos.getX();
			}

			if (pos.getY() < minY)
			{
				minY = pos.getY();
			} else if (pos.getY() > maxY)
			{
				maxY = pos.getY();
			}

			if (pos.getZ() < minZ)
			{
				minZ = pos.getZ();
			} else if (pos.getZ() > maxZ)
			{
				maxZ = pos.getZ();
			}
		}
		VectorD minimum = new VectorD(minX, minY, minZ);
		VectorD maximum = new VectorD(maxX, maxY, maxZ);

		VectorD mapScale = maximum.subtract(minimum);
		double maxDimension = 0;
		if (Math.abs(mapScale.getX()) > maxDimension)
			maxDimension = Math.abs(mapScale.getX());
		if (Math.abs(mapScale.getY()) > maxDimension)
			maxDimension = Math.abs(mapScale.getY());
		if (Math.abs(mapScale.getZ()) > maxDimension)
			maxDimension = Math.abs(mapScale.getZ());

	}
}