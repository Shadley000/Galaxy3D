package com.shadley000.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import com.shadley000.framework.GlobalVar;
import com.shadley000.util.CSVLine;
import com.shadley000.util.MathTool;

public class Security implements Runnable
{
	private Map<Long, ActiveUser> keyToActiveUser = new HashMap<Long, ActiveUser>();
	private Map<String, ActiveUser> userNameToActiveUser = new HashMap<String, ActiveUser>();
	long expireTime = 30 * 60 * 1000;
	long updateTime = 60 * 1000;
	public static Long invalidKey = (long) -1;

	public Security(String filename) throws IOException
	{
		File csvFile = new File(filename);

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
					String username = csvLine.getColumn(0).trim();
					String password = csvLine.getColumn(1).trim();
					ActiveUser activeUser = new ActiveUser(null, username, password, invalidKey);
					userNameToActiveUser.put(username, activeUser);
				}
			}
		} finally
		{
			reader.close();
		}
	}

	public boolean validateUser(InetAddress inetAddress, long key)
	{
		if (key < 0)
			return false;

		ActiveUser activeUser = keyToActiveUser.get(key);

		if (activeUser != null)
		{
			if (activeUser.inetAddress != null && inetAddress.equals(activeUser.inetAddress))
			{
				activeUser.touch();
				return true;
			} else
			{
				System.out.println("Active user has ip address change " + key + " " + inetAddress + " " + activeUser.inetAddress);
				keyToActiveUser.remove(key);
				activeUser.invalidate();
				return false;
			}
		} else
		{
			System.out.println("Active user not found" + key);
			return false;
		}
	}

	public Long loginUser(InetAddress inetAddress, String username, String password)
	{
		ActiveUser activeUser = userNameToActiveUser.get(username);
		Long key = invalidKey;

		if (activeUser != null && activeUser.password.equals(password))
		{

			while (key == invalidKey || keyToActiveUser.containsKey(key))
			{
				key = new Long(MathTool.getRandomLong(Long.MAX_VALUE));
			}

			activeUser.inetAddress = inetAddress;
			activeUser.touch();
			activeUser.loginTouch();
			keyToActiveUser.put(key, activeUser);

			return key;
		} else
		{
			System.out.println("failed login");
			return key;
		}
	}

	@Override
	public void run()
	{
		while (!GlobalVar.done)
		{
			for (Long key : keyToActiveUser.keySet())
			{
				ActiveUser activeUser = keyToActiveUser.get(key);
				if (System.currentTimeMillis() - activeUser.lastTouchTime > expireTime)
				{
					keyToActiveUser.remove(key);
					activeUser.invalidate();
				}
			}
			try
			{
				Thread.sleep(updateTime);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public ActiveUser getActiveUser(long key)
	{
		return keyToActiveUser.get(key);
	}
	public ActiveUser getActiveUser(String userName)
	{
		return userNameToActiveUser.get(userName);
	}
}
