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
 *  A command history buffer.
 *
 *  @author  <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class History
{
	private List			history			= new ArrayList ();
	private PrintWriter		output			= null;
	private int				maxSize			= 500;
	private int				currentIndex	= 0;


	/**
	 *  Construstor: initialize a blank history.
	 */
	public History ()
	{
	}


	/**
	 *  Construstor: initialize History object the the specified
	 *  {@link File} for storage.
	 */
	public History (final File historyFile)
		throws IOException
	{
		setHistoryFile (historyFile);
	}


	public void setHistoryFile (final File historyFile)
		throws IOException
	{
		if (historyFile.isFile ())
			load (new FileInputStream (historyFile));
		setOutput (new PrintWriter (new FileWriter (historyFile), true));
		flushBuffer ();
	}


	/**
	 *  Load the history buffer from the specified InputStream.
	 */
	public void load (final InputStream in)
		throws IOException
	{
		load (new InputStreamReader (in));
	}


	/**
	 *  Load the history buffer from the specified Reader.
	 */
	public void load (final Reader reader)
		throws IOException
	{
		BufferedReader breader = new BufferedReader (reader);
		List lines = new ArrayList ();
		String line;
		while ((line = breader.readLine ()) != null)
		{
			lines.add (line);
		}

		for (Iterator i = lines.iterator (); i.hasNext (); )
			addToHistory ((String)i.next ());
	}


	public int size ()
	{
		return history.size ();
	}


	/**
	 *  Clear the history buffer
	 */
	public void clear ()
	{
		history.clear ();
		currentIndex = 0;
	}


	/**
	 *  Add the specified buffer to the end of the history. The pointer is
	 *  set to the end of the history buffer.
	 */
	public void addToHistory (final String buffer)
	{
		// don't append duplicates to the end of the buffer
		if (history.size () != 0 && buffer.equals (
			history.get (history.size () - 1)))
			return;

		history.add (buffer);
		while (history.size () > getMaxSize ())
			history.remove (0);

		currentIndex = history.size ();

		if (getOutput () != null)
		{
			getOutput ().println (buffer);
			getOutput ().flush ();
		}
	}


	/**
	 *  Flush the entire history buffer to the output PrintWriter.
	 */
	public void flushBuffer ()
		throws IOException
	{
		if (getOutput () != null)
		{
			for (Iterator i = history.iterator (); i.hasNext ();
				getOutput ().println ((String)i.next ()));

			getOutput ().flush ();
		}
	}


	/**
	 *  Move to the end of the history buffer.
	 */
	public void moveToEnd ()
	{
		currentIndex = history.size ();
	}


	/**
	 *  Set the maximum size that the history buffer will store.
	 */
	public void setMaxSize (final int maxSize)
	{
		this.maxSize = maxSize;
	}


	/**
	 *  Get the maximum size that the history buffer will store.
	 */
	public int getMaxSize ()
	{
		return this.maxSize;
	}


	/**
	 *  The output to which all history elements will be written (or null
	 *  of history is not saved to a buffer).
	 */
	public void setOutput (final PrintWriter output)
	{
		this.output = output;
	}


	/**
	 *  Returns the PrintWriter that is used to store history elements.
	 */
	public PrintWriter getOutput ()
	{
		return this.output;
	}


	/**
	 *  Returns the current history index.
	 */
	public int getCurrentIndex ()
	{
		return this.currentIndex;
	}


	/**
	 *  Return the content of the current buffer.
	 */
	public String current ()
	{
		if (currentIndex >= history.size ())
			return "";

		return (String)history.get (currentIndex);
	}


	/**
	 *  Move the pointer to the previous element in the buffer.
	 *
	 *  @return  true if we successfully went to the previous element
	 */
	public boolean previous ()
	{
		if (currentIndex <= 0)
			return false;

		currentIndex--;
		return true;
	}


	/**
	 *  Move the pointer to the next element in the buffer.
	 *
	 *  @return  true if we successfully went to the next element
	 */
	public boolean next ()
	{
		if (currentIndex >= history.size ())
			return false;

		currentIndex++;
		return true;
	}


	/**
	 *  Returns an immutable list of the history buffer.
	 */
	public List getHistoryList ()
	{
		return Collections.unmodifiableList (history);
	}


	/**
	 *  Returns the standard {@link AbstractCollection#toString} representation
	 *  of the history list.
	 */
	public String toString ()
	{
		return history.toString ();
	}
}

