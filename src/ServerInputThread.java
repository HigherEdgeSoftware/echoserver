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
import java.io.InputStreamReader;

public class ServerInputThread extends Thread
{
	protected Server server;

	public ServerInputThread (Server newServer)
	{
		super ("ServerInputThread");
		server = newServer;
	}

	public void run ()
	{
		try
		{
			BufferedReader brReader = new BufferedReader (new InputStreamReader (System.in));
			String strLine = "";
	
			while (true)
			{
				System.out.print ("> ");
				strLine = brReader.readLine ();

				if (strLine == null)
					continue;
	
				if (strLine.compareTo ("quit") == 0)
				{
					server.quit ();

					break;
				}

				System.out.println (strLine);
				server.sendMessage (strLine);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}

