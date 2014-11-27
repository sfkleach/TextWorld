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
 *  <p>
 *  Terminal that is used for unix platforms. Terminal initialization
 *  is handled by issuing the <em>stty</em> command against the
 *  <em>/dev/tty</em> file to disable character echoing and enable
 *  character input. All known unix systems (including
 *  Linux and Macintosh OS X) support the <em>stty</em>), so this
 *  implementation should work for an reasonable POSIX system.
 *	</p>
 *
 *  @author  <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class UnixTerminal
	extends Terminal
{
	private Map terminfo;
	private int width = -1;
	private int height = -1;


	/**
	 *  Remove line-buffered input by invoking "stty -icanon min 1"
	 *  against the current terminal.
	 */
	public void initializeTerminal ()
		throws IOException, InterruptedException
	{
		// save the initial tty configuration
		final String ttyConfig = stty ("-g");

		// sanity check
		if (ttyConfig.length () == 0
			|| (ttyConfig.indexOf ("=") == -1
			&& ttyConfig.indexOf (":") == -1))
		{
			throw new IOException ("Unrecognized stty code: " + ttyConfig);
		}


		// set the console to be character-buffered instead of line-buffered
		stty ("-icanon min 1");

		// disable character echoing
		stty ("-echo");

		// at exit, restore the original tty configuration (for JDK 1.3+)
		try
		{
			Runtime.getRuntime ().addShutdownHook (new Thread ()
			{
				public void start ()
				{
					try
					{
						stty (ttyConfig);
					}
					catch (Exception e)
					{
						consumeException (e);
					}
				}
			});
		}
		catch (AbstractMethodError ame)
		{
			// JDK 1.3+ only method. Bummer.
			consumeException (ame);
		}
	}


	/** 
	 *  No-op for exceptions we want to silently consume.
	 */
	private void consumeException (Throwable e)
	{
	}


	public boolean isSupported ()
	{
		return true;
	}


	public boolean getEcho ()
	{
		return false;
	}


	/**
 	 *	Returns the value of "stty size" width param.
	 *
	 *	<strong>Note</strong>: this method caches the value from the
	 *	first time it is called in order to increase speed, which means
	 *	that changing to size of the terminal will not be reflected
	 *	in the console.
 	 */
	public int getTerminalWidth ()
	{
		if (width != -1)
			return width;

		int val = 80;
		try
		{
			String size = stty ("size");
			if (size.length () != 0 && size.indexOf (" ") != -1)
			{
				val = Integer.parseInt (
					size.substring (size.indexOf (" ") + 1));
			}
		}
		catch (Exception e)
		{
			consumeException (e);
		}

		return width = val;
	}


	/**
 	 *	Returns the value of "stty size" height param.
	 *
	 *	<strong>Note</strong>: this method caches the value from the
	 *	first time it is called in order to increase speed, which means
	 *	that changing to size of the terminal will not be reflected
	 *	in the console.
 	 */
	public int getTerminalHeight ()
	{
		if (height != -1)
			return height;

		int val = 24;

		try
		{
			String size = stty ("size");
			if (size.length () != 0 && size.indexOf (" ") != -1)
			{
				val = Integer.parseInt (
					size.substring (0, size.indexOf (" ")));
			}
		}
		catch (Exception e)
		{
		}

		return height = val;
	}


	/**
	 *  Execute the stty command with the specified arguments
	 *  against the current active terminal.
	 */
	private static String stty (final String args)
		throws IOException, InterruptedException
	{
		return exec ("stty " + args + " < /dev/tty").trim ();
	}


	/**
	 *  Execute the specified command and return the output
	 *  (both stdout and stderr).
	 */
	private static String exec (final String cmd)
		throws IOException, InterruptedException
	{
		return exec (new String [] { "sh", "-c", cmd });
	}


	/**
	 *  Execute the specified command and return the output
	 *  (both stdout and stderr).
	 */
	private static String exec (final String [] cmd)
		throws IOException, InterruptedException
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream ();

		Process p = Runtime.getRuntime ().exec (cmd);
		int c;
		InputStream in;
			
		in = p.getInputStream ();
		while ((c = in.read ()) != -1)
			bout.write (c);

		in = p.getErrorStream ();
		while ((c = in.read ()) != -1)
			bout.write (c);

		p.waitFor ();

		String result = new String (bout.toByteArray ());
		return result;
	}
}

