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
 * <p/>
 * A simple {@link Completor} implementation that handles a pre-defined
 * list of completion words.
 * </p>
 * <p/>
 * <p/>
 * Example usage:
 * </p>
 * <pre>
 *  myConsoleReader.addCompletor (new SimpleCompletor (new String [] { "now", "yesterday", "tomorrow" }));
 *  </pre>
 *
 * @author <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class SimpleCompletor implements Completor, Cloneable {

	/**
	 * The list of candidates that will be completed.
	 */
	SortedSet candidates;


	/**
	 * A delimiter to use to qualify completions.
	 */
	String delimiter;

	final SimpleCompletorFilter filter;


	/**
	 * Create a new SimpleCompletor with a single possible completion
	 * values.
	 */
	public SimpleCompletor( final String candidateString ) {
		this( new String[] { candidateString } );
	}


	/**
	 * Create a new SimpleCompletor with a list of possible completion
	 * values.
	 */
	public SimpleCompletor( final String[] candidateStrings ) {
		this( candidateStrings, null );
	}


	public SimpleCompletor( final String[] strings, final SimpleCompletorFilter filter ) {
		this.filter = filter;
		setCandidateStrings( strings );
	}


	/**
	 * Complete candidates using the contents of the specified Reader.
	 */
	public SimpleCompletor( final Reader reader ) throws IOException {
		this( getStrings( reader ) );
	}


	/**
	 * Complete candidates using the whitespearated values in
	 * read from the specified Reader.
	 */
	public SimpleCompletor( final InputStream in ) throws IOException {
		this( getStrings( new InputStreamReader( in ) ) );
	}


	private static String[] getStrings( final Reader in ) throws IOException {
		final Reader reader = in instanceof BufferedReader ? in : new BufferedReader( in );

		List words = new LinkedList();
		String line;
		while ( ( line = ( (BufferedReader)reader ).readLine() ) != null ) {
			for (
				StringTokenizer tok = new StringTokenizer( line );
				tok.hasMoreTokens();
				words.add( tok.nextToken() )
			) {
				//	Skip
			}
		}

		return (String[])words.toArray( new String[ words.size() ] );
	}


	//	Eliminated examples for buffer == null (Steve Leach)
	public int complete( final String buffer, final int cursor, final List clist ) {
		SortedSet matches = candidates.tailSet( buffer );
		for ( Iterator i = matches.iterator(); i.hasNext(); ) {
			String can = (String)i.next();
			if ( !( can.startsWith( buffer ) ) ) {
				break;
			}

			if ( delimiter != null ) {
				int index = can.indexOf( delimiter, cursor );
				if ( index != -1 ) {
					can = can.substring( 0, index + 1 );
				}
			}
			clist.add( can );
		}

		if ( clist.size() == 1 ) {
			clist.set( 0, ( (String)clist.get( 0 ) ) + " " );
		}

		// the index of the completion is always from the beginning of
		// the buffer.
		return clist.size() == 0 ? -1 : 0;
	}


	public void setDelimiter( final String delimiter ) {
		this.delimiter = delimiter;
	}


	public String getDelimiter() {
		return this.delimiter;
	}


	public void setCandidates( final SortedSet candidates ) {
		if ( filter != null ) {
			TreeSet filtered = new TreeSet();
			for ( Iterator i = candidates.iterator(); i.hasNext(); ) {
				String element = (String)i.next();
				element = filter.filter( element );
				if ( element != null ) {
					filtered.add( element );
				}
			}

			this.candidates = filtered;
		} else {
			this.candidates = candidates;
		}
	}


	public SortedSet getCandidates() {
		return Collections.unmodifiableSortedSet( this.candidates );
	}


	public void setCandidateStrings( final String[] strings ) {
		setCandidates( new TreeSet( Arrays.asList( strings ) ) );
	}


	public void addCandidateString( final String candidateString ) {
		final String string = filter == null ? candidateString : filter.filter( candidateString );
		if ( string != null ) {
			candidates.add( string );
		}
	}


	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


	/**
	 * Filter for elements in the completor.
	 *
	 * @author <a href="mailto:marc@solarmetric.com">Marc Prud'hommeaux</a>
	 */
	public static interface SimpleCompletorFilter {

		/**
		 * Filter the specified String. To not filter it, return the
		 * same String as the parameter. To exclude it, return null.
		 */
		public String filter( String element );
	}


	public static class NoOpFilter implements SimpleCompletorFilter {

		public String filter( final String element ) {
			return element;
		}
	}
}
