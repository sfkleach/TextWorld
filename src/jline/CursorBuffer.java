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
 * A CursorBuffer is a holder for a {@link StringBuffer} that
 * also contains the current cursor position.
 *
 * @author <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class CursorBuffer {

	public int cursor = 0;
	public final StringBuffer buffer = new StringBuffer();

	public int length() {
		return buffer.length();
	}


	public char current() {
		if ( cursor <= 0 ) {
			return 0;
		}
		return buffer.charAt( cursor - 1 );
	}


	/**
	 * Insert the specific character into the buffer, setting the
	 * cursor position ahead one.
	 *
	 * @param c the character to insert
	 */
	public void insert( final char c ) {
		buffer.insert( cursor++, c );
	}


	/**
	 * Insert the specified {@link String} into the buffer, setting
	 * the cursor to the end of the insertion point.
	 *
	 * @param str the String to insert. Must not be null.
	 */
	public void insert( final String str ) {
		if ( buffer.length() == 0 ) {
			buffer.append( str );
		} else {
			buffer.insert( cursor, str );
		}

		cursor += str.length();
	}

	public String toString() {
		return buffer.toString();
	}
	
}
