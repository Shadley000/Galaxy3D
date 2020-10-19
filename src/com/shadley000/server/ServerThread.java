package com.shadley000.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.shadley000.framework.Entity;
import com.shadley000.framework.GlobalVar;
import com.shadley000.util.VectorD;

public class ServerThread extends Thread
{
	private Socket socket;

	public ServerThread(Socket socket)
	{
		this.socket = socket;
	}

	public void run()
	{
		try
		{
			Command command = (Command) new ObjectInputStream(socket.getInputStream()).readObject();
			// System.out.println(command.key + "\t" + command.command + "\t" +
			// command.text1 + "\t" + command.text2);

			if (Command.COMMAND_LOGIN == command.command)
			{
				Long key = GlobalVar.getSecurity().loginUser(socket.getInetAddress(), command.text1, command.text2);
				System.out.println("login key:" + key);
				if (key > 0)
					returnResponse(new Response(key));
				else
					returnResponse(new Response("Error: user not found " + command.key, true));
			} else if (GlobalVar.getSecurity().validateUser(socket.getInetAddress(), command.key))
			{
				ActiveUser activeUser = GlobalVar.getSecurity().getActiveUser(command.key);
				if (Command.COMMAND_ECHO == command.command)
				{
					// System.out.println("login echo:" + command.key);
					returnResponse(new Response("Echo"));
				} else if (Command.COMMAND_SHUTDOWN == command.command)
				{
					// System.out.println("shutdown key:" + command.key);
					returnResponse(new Response("done"));
					GlobalVar.done = true;
				} else if (Command.COMMAND_ENTITY_LIST == command.command)
				{
					// System.out.println("entity list key:" + command.key);
					returnResponse(new Response(getVisibleEntities(activeUser)));
				} else
				{
					// System.out.println("unrecognized key:" + command.key);
					returnResponse(new Response("Error: unrecognized command " + command.command, true));
				}
			} else
			{
				returnResponse(new Response("Error: key not valid " + command.key, true));
			}

		} catch (IOException ex)
		{
			ex.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				socket.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void returnResponse(Response response) throws IOException
	{
		if (response.isError)
			System.out.println((String) response.getObj());
		new ObjectOutputStream(socket.getOutputStream()).writeObject(response);
	}

	private Set<Entity> getVisibleEntities(ActiveUser activeUser)
	{
		Set<Entity> ownedEntityList = new HashSet<Entity>();
		Set<Entity> lockedEntityList = new HashSet<Entity>();
		Set<Entity> detectedEntityList = new HashSet<Entity>();
		Set<Entity> observableEntityList = new HashSet<Entity>();

		for (Entity entity : GlobalVar.getSolarSystem().getEntities())
		{
			if (entity.unlock(activeUser.getKeySet()))
			{
				try
				{
					ownedEntityList.add((Entity) entity.clone());
				} catch (CloneNotSupportedException e)
				{
					e.printStackTrace();
				}
			} else
			{
				lockedEntityList.add(entity);

			}

		}

		for (Entity entity : lockedEntityList)
		{
			Entity clone = detectEntity(ownedEntityList, entity);
			if (clone != null)
				detectedEntityList.add(clone);
		}

		observableEntityList.addAll(ownedEntityList);
		observableEntityList.addAll(detectedEntityList);
		return observableEntityList;
	}

	private Entity detectEntity(Set<Entity> ownedEntityList, Entity testEntity)
	{
		for (Entity ownedEntity : ownedEntityList)
		{
			if (ownedEntity.detect(testEntity))
			{
				try
				{
					Entity clone = (Entity) testEntity.clone();
					clone.strip();
					return clone;
				} catch (CloneNotSupportedException e)
				{
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
