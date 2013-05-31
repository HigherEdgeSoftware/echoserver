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

public class PolicyServer extends Thread
{
	protected ServerSocket netServer;
	protected boolean isRunning;
	protected int port;
	protected String crossDomainFile;

	protected ArrayList aryClientThreads;
	protected ArrayList aryClients;

	public PolicyServer (int newPort, String newCrossDomainFile)
	{
		super ("PolicyServer");

		port = newPort;
		netServer = null;
		isRunning = true;
		aryClientThreads = new ArrayList ();
		aryClients = new ArrayList ();
		crossDomainFile = newCrossDomainFile;
	}

	public void run ()
	{
		try
		{
			System.out.println ("Policy Server started on port: " + port);
			netServer = new ServerSocket (port);

			while (isRunning == true)
			{
				Socket netClient = netServer.accept ();

				ServerThread tThread = new ServerThread (null, netClient, aryClients.size (), crossDomainFile);
				tThread.start ();

				aryClients.add (netClient);
				aryClientThreads.add (tThread);
			}

			netServer.close ();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}

