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
 * A file name completor takes the buffer and issues a list of
 * potential completions.
 * <p/>
 * <p/>
 * This completor tries to behave as similar as possible to
 * <i>bash</i>'s file name completion (using GNU readline)
 * with the following exceptions:
 * <p/>
 * <ul>
 * <li>Candidates that are directories will end with "/"</li>
 * <li>Wildcard regular expressions are not evaluated or replaced</li>
 * <li>The "~" character can be used to represent the user's home,
 * but it cannot complete to other users' homes, since java does
 * not provide any way of determining that easily</li>
 * </ul>
 * <p/>
 * <p>TODO</p>
 * <ul>
 * <li>Handle file with spaces in them</li>
 * <li>Have an option for file type color highlighting</li>
 * </ul>
 *
 * @author <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class FileNameCompletor implements Completor {

	//	Eliminated examples for null buffer (Steve Leach)
	public int complete( final String buffer, final int cursor, final List candidates ) {
		String translated = buffer;

		// special character: ~ maps to the user's home directory
		if ( translated.startsWith( "~" + File.separator ) ) {
			translated = System.getProperty( "user.home" ) + translated.substring( 1 );
		} else if ( translated.startsWith( "~" ) ) {
			translated = new File( System.getProperty( "user.home" ) ).getParentFile().getAbsolutePath();
		} else if ( !( translated.startsWith( File.separator ) ) ) {
			translated = new File( "" ).getAbsolutePath() + File.separator + translated;
		}

		File f = new File( translated );

		final File dir;

		if ( translated.endsWith( File.separator ) ) {
			dir = f;
		} else {
			dir = f.getParentFile();
		}

		final File[] entries = dir == null ? new File[ 0 ] : dir.listFiles();
		return matchFiles( buffer, translated, entries, candidates );
	}


	/**
	 * Match the specified <i>buffer</i> to the array of <i>entries</i>
	 * and enter the matches into the list of <i>candidates</i>. This method
	 * can be overridden in a subclass that wants to do more
	 * sophisticated file name completion.
	 *
	 * @return the offset of the match
	 * @param	buffer		the untranslated buffer
	 * @param	translated	the buffer with common characters replaced
	 * @param	entries		the list of file to match
	 * @param	candidates	the list of candidates to populate
	 */
	public int matchFiles( String buffer, String translated, File[] entries, List candidates ) {
		if ( entries == null ) {
			return -1;
		}

		int matches = 0;

		// first pass: just count the matches
		for ( int i = 0; i < entries.length; i++ ) {
			if ( entries[ i ].getAbsolutePath().startsWith( translated ) ) {
				matches++;
			}
		}

		// green - executable
		// blue - directory
		// red - compressed
		// cyan - symlink
		for ( int i = 0; i < entries.length; i++ ) {
			if ( entries[ i ].getAbsolutePath().startsWith( translated ) ) {
				String name = (
					entries[ i ].getName() +
					( matches == 1 && entries[ i ].isDirectory() ? File.separator : " " )
				);

				/*
				if (entries [i].isDirectory ())
				{
					name = new ANSIBuffer ().blue (name).toString ();
				}
				*/

				candidates.add( name );
			}
		}


		final int index = buffer.lastIndexOf( File.separator );
		return index + File.separator.length();
	}
}

