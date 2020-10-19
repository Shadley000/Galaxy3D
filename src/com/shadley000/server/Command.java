package com.shadley000.server;

import java.io.Serializable;

public class Command implements Serializable
{

	private static final long serialVersionUID = 1L;

	public static final int COMMAND_SHUTDOWN = -1;
	public static final int COMMAND_ECHO = 0;
	public static final int COMMAND_LOGIN = 1;
	public static final int COMMAND_ENTITY_LIST = 2;

	int command = 0;
	long key = -1;
	String text1 = null;
	String text2 = null;

	public Command()
	{
	}

	public Command(long key, int command)
	{
		this.key = key;
		this.command = command;
		this.text1 = null;
		this.text2 = null;
	}

	public Command(long key, int command, String text1)
	{
		this.key = key;
		this.command = command;
		this.text1 = text1;
		this.text2 = null;
	}

	public Command(long key, int command, String text1, String text2)
	{
		this.key = key;
		this.command = command;
		this.text1 = text1;
		this.text2 = text2;
	}
}
