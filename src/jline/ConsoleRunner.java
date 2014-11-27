/**
 *	jline - Java console input library
 *	Copyright (c) 2002,2003 Marc Prud'hommeaux mwp1@cornell.edu
 *	
 *	This library is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU Lesser General Public
 *	License as published by the Free Software Foundation; either
 *	version 2.1 of the License, or (at your option) any later version.
 *	
 *	This library is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *	Lesser General Public License for more details.
 *	
 *	You should have received a copy of the GNU Lesser General Public
 *	License along with this library; if not, write to the Free Software
 *	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jline;

import java.io.*;
import java.util.*;
import java.text.MessageFormat;


/**
 *	<p>
 *	A pass-through application that sets the system input stream to a
 *	{@link ConsoleReader} and invokes the specified main method.
 *	</p>
 *
 *  @author  <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class ConsoleRunner
{
	public static void main (final String[] args)
		throws Exception
	{
		List argList = new ArrayList (Arrays.asList (args));
		if (argList.size () == 0)
		{
			usage ();
			return;
		}

		// invoke the main() method
		String mainClass = (String)argList.remove (0);

		// setup the inpout stream
		ConsoleReader reader = new ConsoleReader ();
		reader.setHistory (new History (new File (
			System.getProperty ("user.home"), ".jline-" + mainClass
				+ ".history")));

		String completors = System.getProperty (ConsoleRunner.class.getName ()
			+ ".completors", "");
		List completorList = new ArrayList ();
		for (StringTokenizer tok = new StringTokenizer (completors, ",");
			tok.hasMoreTokens (); )
		{
			completorList.add ((Completor)Class.forName (tok.nextToken ())
				.newInstance ());
		}

		if (completorList.size () > 0)
			reader.addCompletor (new ArgumentCompletor (completorList));

		ConsoleReaderInputStream.setIn (reader, null);
		try
		{
			Class.forName (mainClass)
				.getMethod ("main", new Class[] { String[].class})
				.invoke (null, new Object[] { argList.toArray (new String[0])});
		}
		finally
		{
			// just in case this main method is called from another program
			ConsoleReaderInputStream.restoreIn ();
		}
	}


	private static void usage ()
	{
		throw new IllegalArgumentException ("Usage: java "
			+ ConsoleRunner.class.getName ()
			+ " <target class name> [args]");
	}
}

