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

public class EchoServer
{
	public static void displayHelp ()
	{
		System.out.println ("Echo server");
		System.out.println ("Licensed under the MIT license");
		System.out.println ("Copyright 2013, Higher Edge Software, LLC");
		System.out.println ("Programmer: Nathanael Coonrod");
		System.out.println ("List of command line arguments:");
		System.out.println ("--start-flash-policy-server");
		System.out.println ("\t- Start the flash policy server. Requires that --cross-domain-file is used as well. Will listen on port 843. You may need to run as administrator in order to listen on a port lower than 1024.");
		System.out.println ("--cross-domain-file");
		System.out.println ("\t- Specify the location of the crossdomain.xml file for the flash policy server.");
		System.out.println ("--port");
		System.out.println ("\t- Specify which port to listen on.");
		System.out.println ("--help");
		System.out.println ("\t- Display help.");
	}

	public static void main (String []args)
	{
		boolean bStartPolicyServer = false;
		String strCrossDomainFile = "./crossdomain.xml";
		int iPort = 2040;

		for (int iIdx = 0; iIdx < args.length; iIdx++)
		{
			String strArg = args[iIdx];
			boolean bDisplayHelp = true;

			if (strArg.indexOf ("--start-flash-policy-server") > -1)
			{
				bStartPolicyServer = true;
				bDisplayHelp = false;
			}

			if (strArg.indexOf ("--cross-domain-file") > -1)
			{
				int iPos = strArg.indexOf ("=");
				strCrossDomainFile = strArg.substring (iPos + 1);
				bDisplayHelp = false;
			}

			if (strArg.indexOf ("--port") > -1)
			{
				int iPos = strArg.indexOf ("=");
				iPort = Integer.parseInt (strArg.substring (iPos + 1));
				bDisplayHelp = false;
			}

			if (strArg.indexOf ("--help") > -1)
				bDisplayHelp = true;

			if (bDisplayHelp == true)
				displayHelp ();
		}

		if (bStartPolicyServer == true)
		{
			PolicyServer sPolicy = new PolicyServer (843, strCrossDomainFile);
			sPolicy.start ();
		}

		Server sMain = new Server (strCrossDomainFile);
		sMain.start (iPort);
	}
}

