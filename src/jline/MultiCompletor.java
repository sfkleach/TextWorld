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

import java.util.*;

/**
 * <p/>
 * A completor that contains multiple embedded completors. This differs
 * from the {@link ArgumentCompletor}, in that the nested completors
 * are dispatched individually, rather than delimited by arguments.
 * </p>
 *
 * @author <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class MultiCompletor
	implements Completor {

	Completor[] completors = new Completor[ 0 ];


	/**
	 * Construct a MultiCompletor with no embedded completors.
	 */
	public MultiCompletor() {
		this( new Completor[ 0 ] );
	}


	/**
	 * Construct a MultiCompletor with the specified list of
	 * {@link Completor} instances.
	 */
	public MultiCompletor( final List completors ) {
		this( (Completor[])completors.toArray( new Completor[ completors.size() ] ) );
	}


	/**
	 * Construct a MultiCompletor with the specified
	 * {@link Completor} instances.
	 */
	public MultiCompletor( final Completor[] completors ) {
		this.completors = completors;
	}


	public int complete( final String buffer, final int pos, final List cand ) {
		int[] positions = new int[ completors.length ];
		List[] copies = new List[ completors.length ];
		for ( int i = 0; i < completors.length; i++ ) {
			// clone and save the candidate list
			copies[ i ] = new LinkedList( cand );
			positions[ i ] = completors[ i ].complete( buffer, pos, copies[ i ] );
		}

		int maxposition = -1;
		for ( int i = 0; i < positions.length; i++ ) {
			maxposition = Math.max( maxposition, positions[ i ] );
		}

		// now we have the max cursor value: build up all the
		// candidate lists that have the same cursor value
		for ( int i = 0; i < copies.length; i++ ) {
			if ( positions[ i ] == maxposition ) {
				cand.addAll( copies[ i ] );
			}
		}

		return maxposition;
	}


	public void setCompletors( final Completor[] completors ) {
		this.completors = completors;
	}


	public Completor[] getCompletors() {
		return this.completors;
	}
}
