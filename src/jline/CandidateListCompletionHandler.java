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
 * <p/>
 * A {@link CompletionHandler} that deals with multiple distinct completions
 * by outputting the complete list of possibilities to the console. This
 * mimics the behavior of the
 * <a href="http://www.gnu.org/directory/readline.html">readline</a>
 * library.
 * </p>
 *
 * @author <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class CandidateListCompletionHandler implements CompletionHandler {

	private static ResourceBundle loc = ResourceBundle.getBundle( CandidateListCompletionHandler.class.getName() );


	public boolean complete( final ConsoleReader reader, final List candidates, final int pos ) throws IOException {
		CursorBuffer buf = reader.getCursorBuffer();

		// if there is only one completion, then fill in the buffer
		if ( candidates.size() == 1 ) {
			String value = candidates.get( 0 ).toString();

			// fail if the only candidate is the same as the current buffer
			if ( value.equals( buf.toString() ) ) {
				return false;
			}
			setBuffer( reader, value, pos );
			return true;
		} else if ( candidates.size() > 1 ) {
			String value = getUnambiguousCompletions( candidates );
			String bufString = buf.toString();
			setBuffer( reader, value, pos );


			// if we have changed the buffer, then just return withough
			// printing out all the subsequent candidates

			//	Steve Leach: I think the original code is off-by-one.  There is
			//	a compensating error in ArgumentCompletor.
//			if ( bufString.length() - pos + 1 != value.length() ) {
			if ( bufString.length() - pos != value.length() ) {
				return true;
			}
		}

		reader.printNewline();
		printCandidates( reader, candidates );

		// redraw the current console buffer
		reader.drawLine();

		return true;
	}


	private static void setBuffer( ConsoleReader reader, String value, int offset ) throws IOException {
		//	I think this may be another example of the off-by-1 error.  Steve Leach
//		while ( reader.getCursorBuffer().cursor >= offset && reader.backspace() ) {
		while ( reader.getCursorBuffer().cursor > offset && reader.backspace() ) {
			//	Skip;
		}
		reader.putString( value );
		reader.setCursorPosition( offset + value.length() );
	}


	/**
	 * Print out the candidates. If the size of the candidates
	 * is greated than the getAutoprintThreshhold,
	 * they prompt with aq warning.
	 *
	 * @param candidates the list of candidates to print
	 */
	private final void printCandidates( ConsoleReader reader, Collection candidates ) throws IOException {
		final Set distinct = new HashSet( candidates );		//	added final (Steve Leach)

		if ( distinct.size() > reader.getAutoprintThreshhold() ) {
			reader.printString( MessageFormat.format( loc.getString( "display-candidates" ),
				new Object[]{new Integer( candidates.size() )} ) + " " );

			reader.flushConsole();

			int c;

			String noOpt = loc.getString( "display-candidates-no" );
			String yesOpt = loc.getString( "display-candidates-yes" );

			while ( ( c = reader.readCharacter( new char[]{ yesOpt.charAt( 0 ), noOpt.charAt( 0 ) } ) ) != -1 ) {
				if ( noOpt.startsWith( new String( new char[]{ (char)c } ) ) ) {
					reader.printNewline();
					return;
				} else if ( yesOpt.startsWith( new String( new char[]{ (char)c } ) ) ) {
					//	The following printNewline is required now I have eliminated the
					//	double newline when presenting a list of candidates.  (Steve Leach)
					reader.printNewline();
					break;
				} else {
					reader.beep();
				}
			}
		}

		// copy the values and make them distinct, without otherwise
		// affecting the ordering. Only do it if the sizes differ.
		if ( distinct.size() != candidates.size() ) {
			Collection copy = new ArrayList();
			for ( Iterator i = candidates.iterator(); i.hasNext(); ) {
				Object next = i.next();
				if ( !( copy.contains( next ) ) ) {
					copy.add( next );
				}
			}

			candidates = copy;
		}

		//	This extra newline is superfluous (Steve Leach)
//		reader.printNewline();
		reader.printColumns( candidates );
	}


	/**
	 * Returns a root that matches all the {@link String} elements
	 * of the specified {@link List}, or null if there are
	 * no commalities. For example, if the list contains
	 * <i>foobar</i>, <i>foobaz</i>, <i>foobuz</i>, the
	 * method will return <i>foob</i>.
	 */
	private final String getUnambiguousCompletions( final List candidates ) {
		if ( candidates == null || candidates.size() == 0 ) {
			return null;
		}

		// convert to an array for speed
		String[] strings = (String[])candidates.toArray( new String[ candidates.size() ] );

		String first = strings[ 0 ];
		StringBuffer candidate = new StringBuffer();
		for ( int i = 0; i < first.length(); i++ ) {
			if ( startsWith( first.substring( 0, i + 1 ), strings ) ) {
				candidate.append( first.charAt( i ) );
			} else {
				break;
			}
		}

		return candidate.toString();
	}


	/**
	 * @return true is all the elements of <i>candidates</i>
	 *         start with <i>starts</i>
	 */
	private final boolean startsWith( final String starts, final String[] candidates ) {
		for ( int i = 0; i < candidates.length; i++ ) {
			if ( !candidates[ i ].startsWith( starts ) ) {
				return false;
			}
		}

		return true;
	}
}

