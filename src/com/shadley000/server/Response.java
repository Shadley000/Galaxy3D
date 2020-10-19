package com.shadley000.server;

import java.io.Serializable;

public class Response implements Serializable
{
	private static final long serialVersionUID = 1L;

	protected Object obj;
	protected boolean isError = false;

	public Response(Object obj, boolean isError)
	{
		this.obj = obj;
		this.isError = isError;
	}

	public Response(Object obj)
	{
		this.obj = obj;
		this.isError = false;
	}

	public Object getObj()
	{
		return obj;
	}

	public boolean isError()
	{
		return isError;
	}

}
