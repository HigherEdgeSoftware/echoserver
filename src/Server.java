/*
	MIT-LICENSE
	Copyright (c) 2013 Higher Edge Software, LLC

	Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
	and associated documentation files (the "Software"), to deal in the Software without restriction, 
	including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
	and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
	subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all copies or substantial 
	portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
	IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
	WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Server
{
	protected ServerSocket netServer;
	protected boolean isRunning;
	protected ServerInputThread input;
	protected String crossDomainFile;

	protected HashMap<Integer, ServerThread> aryClientThreads;
	protected HashMap<Integer, Socket> aryClients;

	public Server (String newCrossDomainFile)
	{
		netServer = null;
		isRunning = true;
		aryClientThreads = new HashMap<Integer, ServerThread> ();
		aryClients = new HashMap<Integer, Socket> ();
		crossDomainFile = newCrossDomainFile;
	}

	public void start (int port)
	{
		try
		{
			netServer = new ServerSocket (port);
			System.out.println ("Server started on port: " + port);
			input = new ServerInputThread (this);
			input.start ();

			while (isRunning == true)
			{
				Socket netClient = netServer.accept ();
				System.out.println ("Client connected with IP: " + netClient.getLocalAddress ());
				Integer iHash = getUniqueHash ();

				ServerThread tThread = new ServerThread (this, netClient, iHash, crossDomainFile);
				tThread.start ();

				aryClients.put (iHash, netClient);
				aryClientThreads.put (iHash, tThread);
			}

			netServer.close ();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public Integer getUniqueHash ()
	{
		Random rRandom = new Random ();
		Integer iHash = new Integer (rRandom.nextInt ());

		while (aryClients.containsKey (iHash) == true)
		{
			rRandom = new Random ();
			iHash = new Integer (rRandom.nextInt ());
		}

		return (iHash);
	}

	public void sendMessage (String line)
	{
		sendMessage (line, -1);
	}

	public void sendMessage (String line, int except)
	{
		try
		{

			Object []aryKeys = (Object [])aryClients.keySet ().toArray ();

			for (int iIdx = 0; iIdx < aryKeys.length; iIdx++)
			{
				int iKey = (Integer)aryKeys[iIdx];
				Socket netClient = aryClients.get (iKey);

				if (except != -1)
				{
					if (except == iKey)
						continue;
				}

				PrintWriter pwWriter = new PrintWriter (netClient.getOutputStream (), true);
				pwWriter.println (line);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void disconnectClient (int clientIndex)
	{
		try
		{
			System.out.println ("Client disconnected!");

			Socket netClient = (Socket)aryClients.get (clientIndex);
			netClient.close ();

			ServerThread tThread = (ServerThread)aryClientThreads.get (clientIndex);
			tThread.interrupt ();

			aryClientThreads.remove (clientIndex);
			aryClients.remove (clientIndex);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void quit () 
	{
		try
		{
			System.out.println ("Quitting...");

			for (int iIdx = 0; iIdx < aryClients.size (); iIdx++)
			{
				Socket netClient = (Socket)aryClients.get (iIdx);
				netClient.close ();

				ServerThread tThread = (ServerThread)aryClientThreads.get (iIdx);
				tThread.interrupt ();
			}

			isRunning = false;
			System.exit (0);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}

