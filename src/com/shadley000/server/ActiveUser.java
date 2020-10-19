package com.shadley000.server;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class ActiveUser
{
	String userName;
	String password;
	InetAddress inetAddress = null;
	long authenticationKey = -1;
	long loginTime = System.currentTimeMillis();
	long lastTouchTime = System.currentTimeMillis();
	
	Set<Long> keySet = new HashSet<Long>();
	
	public ActiveUser(InetAddress inetAddress, String userName, String password, Long key) {
		this.userName = userName;
		this.password = password;
		this.inetAddress = inetAddress;
		this.authenticationKey = key;
		keySet.add((long)0);
	}
	public long getAuthenticationKey()
	{
		return authenticationKey;
	}
	public void setAuthenticationKey(long authenticationKey)
	{
		this.authenticationKey = authenticationKey;
	}
	public Set<Long> getKeySet()
	{
		return keySet;
	}

	public void addKey(Long key)
	{
		keySet.add(key);
	}
	
	public void touch() {lastTouchTime = System.currentTimeMillis();}

	public void loginTouch()
	{
		lastTouchTime = System.currentTimeMillis();
		loginTime = System.currentTimeMillis();
	}

	public void invalidate()
	{
		authenticationKey = -1;
		loginTime = 0;
		lastTouchTime = 0;
		inetAddress = null;
	}
}
