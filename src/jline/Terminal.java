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
 * Representation of the input terminal for a platform. Handles
 * any initialization that the platform may need to perform
 * in order to allow the {@link ConsoleReader} to correctly handle
 * input.
 *
 * @author <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public abstract class Terminal {

	private static Terminal term;


	/**
	 * @see #setupTerminal
	 */
	public static Terminal getTerminal() {
		return setupTerminal();
	}


	/**
	 * <p>Configure and return the {@link Terminal} instance for the
	 * current platform. This will initialize any system settings
	 * that are required for the console to be able to handle
	 * input correctly, such as setting tabtop, buffered input, and
	 * character echo.</p>
	 * <p/>
	 * <p>This class will use the Terminal implementation specified in the
	 * <em>jline.terminal</em> system property, or, if it is unset, by
	 * detecting the operating system from the <em>os.name</em>
	 * system property and instantiateing either the
	 * {@link WindowsTerminal} or {@link UnixTerminal}.
	 *
	 * @see #initializeTerminal
	 */
	public static synchronized Terminal setupTerminal() {
		if ( term != null ) {
			return term;
		}

		final Terminal t;

		String os = System.getProperty( "os.name" ).toLowerCase();
		String termProp = System.getProperty( "jline.terminal" );
		if ( termProp != null && termProp.length() > 0 ) {
			try {
				t = (Terminal)Class.forName( termProp ).newInstance();
			} catch ( Exception e ) {
				throw (IllegalArgumentException)new IllegalArgumentException( e.toString() ).fillInStackTrace();
			}
		} else if ( os.indexOf( "windows" ) != -1 ) {
			t = new WindowsTerminal();
		} else {
			t = new UnixTerminal();
		}

		try {
			t.initializeTerminal();
		} catch ( Exception e ) {
			e.printStackTrace();
			return term = new UnsupportedTerminal();
		}

		return term = t;
	}


	/**
	 * Read a single character from the input stream. This might
	 * enable a terminal implementation to better handle nuances of
	 * the console.
	 */
	public int readCharacter( final InputStream in ) throws IOException {
		return in.read();
	}


	/**
	 * Initialize any system settings
	 * that are required for the console to be able to handle
	 * input correctly, such as setting tabtop, buffered input, and
	 * character echo.
	 */
	public abstract void initializeTerminal() throws Exception;


	/**
	 * Returns the current width of the terminal (in characters)
	 */
	public abstract int getTerminalWidth();


	/**
	 * Returns the current height of the terminal (in lines)
	 */
	public abstract int getTerminalHeight();


	/**
	 * Returns true if this terminal is capable of initializing the
	 * terminal to use jline.
	 */
	public abstract boolean isSupported();


	/**
	 * Returns true if the terminal will echo all characters type.
	 */
	public abstract boolean getEcho();
}
