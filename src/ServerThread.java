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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread
{
	protected Server server;
	protected Socket sock;
	protected int clientIndex;
	protected String crossDomainFile;

	public ServerThread (Server newServer, Socket newSocket, int newClientIndex, String newCrossDomainFile)
	{
		super ("ServerThread");

		server = newServer;
		sock = newSocket;
		clientIndex = newClientIndex;
		crossDomainFile = newCrossDomainFile;
	}

	public void sendSocketPolicyFile () throws Exception 
	{
		String strFile = "";

		try
		{
			FileReader frFile = new FileReader (crossDomainFile);
			BufferedReader brReader = new BufferedReader (frFile);
			String strLine = brReader.readLine ();

			while (strLine != null)
			{
				strFile += strLine;
				strLine = brReader.readLine ();
			}

			brReader.close ();
			frFile.close ();

			strFile += "\0";
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		try
		{
			System.out.print ("Client is requesting a socket policy file. ");
			PrintWriter pwWriter = new PrintWriter (sock.getOutputStream (), true);
			pwWriter.println (strFile);
			pwWriter.flush ();
			System.out.println ("Policy has been sent.");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void run ()
	{
		try
		{
			BufferedReader brReader = new BufferedReader (new InputStreamReader (sock.getInputStream ()));
			String strLine = brReader.readLine ();

			while (strLine != null)
			{
				if (sock.isClosed () == true)
					break;

				if (strLine.indexOf ("<policy-file-request/>") > -1)
				{
					sendSocketPolicyFile ();
					sock.close ();

					break;
				}
				else
				{
					server.sendMessage (strLine, -1);
					System.out.println (strLine);
				}

				strLine = brReader.readLine ();
			}

			brReader.close ();
			sock.close ();

			if (server != null)
				server.disconnectClient (clientIndex);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
