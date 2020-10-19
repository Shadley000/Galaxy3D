package com.shadley000.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.shadley000.framework.GlobalVar;

public class Server {

	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			System.out.println("Usage: java.exe com.shadley000.server.Server port ");
			return;
		}

		int port = Integer.parseInt(args[0]);
		
		System.out.println("Starting Server on port " + port);
		GlobalVar.buildGlobalVar();
		
		try (ServerSocket serverSocket = new ServerSocket(port))
		{

			System.out.println("Server is listening on port " + port);

			while (!GlobalVar.done)
			{
				Socket socket = serverSocket.accept();

				System.out.println("connected:" + socket.getInetAddress());

				new ServerThread(socket).start();
			}

		} catch (IOException ex)
		{
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	
}
