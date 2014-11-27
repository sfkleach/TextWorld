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



/**
 *  A no-op unsupported terminal.
 *
 *  @author  <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class UnsupportedTerminal
	extends Terminal
{
	public void initializeTerminal ()
	{
		// nothing we need to do (or can do) for windows.
	}


	public boolean getEcho ()
	{
		return true;
	}


	/**
 	 *	Always returng 80, since we can't access this info on Windows.
 	 */
	public int getTerminalWidth ()
	{
		return 80;
	}


	/**
 	 *	Always returng 24, since we can't access this info on Windows.
 	 */
	public int getTerminalHeight ()
	{
		return 80;
	}


	public boolean isSupported ()
	{
		return false;
	}
}

