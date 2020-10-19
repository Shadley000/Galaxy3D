package com.shadley000.util;

public class Map3D
{
	int map[][][] = null;

	public Map3D(VectorIntD dimensions)
	{
		map = new int[dimensions.getX()][dimensions.getY()][dimensions.getZ()];
		for (int x = 0; x < map.length; x++)
		{
			for (int y = 0; y < map[0].length; y++)
			{
				for (int z = 0; z < map[0][0].length; z++)
				{
					map[x][y][z] = 0;
				}
			}
		}
	}

	public int add(int x, int y, int z, int val)
	{

		return map[x][y][z] += val;

	}
	public int get(int x, int y, int z)
	{

		return map[x][y][z];

	}
	
	public void set(int x, int y, int z, int value)
	{

		 map[x][y][z] = value;

	}
}
