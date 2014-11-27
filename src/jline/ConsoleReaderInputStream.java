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


/**
 *	An {@link InputStream} implementation that wraps a {@link ConsoleReader}.
 *	It is useful for setting up the {@link System#in} for a generic
 *	console.
 *
 *  @author  <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class ConsoleReaderInputStream
	extends SequenceInputStream
{
	private static InputStream systemIn = System.in;


	public static void setIn ()
		throws IOException
	{
		setIn (new ConsoleReader (), null);
	}


	public static void setIn (final ConsoleReader reader, final String prompt )
	{
		System.setIn (new ConsoleReaderInputStream (reader, prompt));
	}


	/**
	 *  Restore the original {@link System#in} input stream.
	 */
	public static void restoreIn ()
	{
		System.setIn (systemIn);
	}


	public ConsoleReaderInputStream (final ConsoleReader reader, final String prompt)
	{
		super (new ConsoleEnumeration (reader, prompt));
	}


	private static class ConsoleEnumeration
		implements Enumeration
	{
		private final ConsoleReader reader;
		private ConsoleLineInputStream next = null;
		private ConsoleLineInputStream prev = null;
		private String prompt;


		public ConsoleEnumeration (final ConsoleReader reader, final String prompt)
		{
			this.reader = reader;
			this.prompt = prompt;
		}


		public Object nextElement ()
		{
			if (next != null)
			{
				InputStream n = next;
				prev = next;
				next = null;
				return n;
			}

			return new ConsoleLineInputStream (reader, prompt);
		}


		public boolean hasMoreElements ()
		{
			// the last line was null
			if (prev != null && prev.wasNull == true)
				return false;

			if (next == null)
				next = (ConsoleLineInputStream)nextElement ();

			return next != null;
		}
	}	


	private static class ConsoleLineInputStream
		extends InputStream
	{
		private final ConsoleReader reader;
		private String line = null;
		private int index = 0;
		private boolean eol = false;
		protected boolean wasNull = false;
		private String prompt;

		public ConsoleLineInputStream (final ConsoleReader reader, final String prompt )
		{
			this.reader = reader;
			this.prompt = prompt;
		}


		public int read ()
			throws IOException
		{
			if (eol)
				return -1;

			if (line == null)
				line = reader.readLine( this.prompt );

			if (line == null)
			{
				wasNull = true;
				return -1;
			}

			if (index >= line.length ())
			{
				eol = true;
				return '\n'; // lines are ended with a newline
			}

			return line.charAt (index++);
		}
	}
}

