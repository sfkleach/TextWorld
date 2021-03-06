/**
 * Copyright Stephen Leach, 2014
 * This file is part of the MinXML for Java library.
 * 
 * MinXML for Java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MinXML for Java.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package com.steelypip.powerups.minxml;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.steelypip.powerups.alert.Alert;

/**
 * This interface linearises the construction of a MinXML
 * tree. Start tags are constructed with at least two calls:
 * startTagOpen and startTagClose. In between these two calls
 * there can be any number of puts, which set up the 
 * attributes.
 * 
 * Note that startTagOpen, startTagClose and endTag all
 * take the element name as a parameter. When using this 
 * interface with an unknown implementation the same (equals) 
 * element name should be supplied for all three calls that 
 * construct a particular element.
 * 
 * However an implementation may elect to allow the
 * element name to be null on one or more of these calls
 * (or even all!). All non-null values should be
 * the same and that must be the final value of the 
 * element name.
 *
 */
public interface MinXMLBuilder {
	
	/**
	 * This method should be called to begin the construction of
	 * a start-tag with a particular element name. An implementation
	 * may insist on the name being non-null or may permit it to
	 * be supplied later.
	 * 
	 * After this method, the next builder method should be put
	 * or startTagClose. 
	 * 
	 * @param name the name of the element to be constructed (or null). 
	 */
	void startTagOpen( String name );
	
	/**
	 * This method returns the difference between the number of 
	 * startTagOpen and endTags calls. 
	 * @return the nesting level.
	 */
	int nestingLevel();
	
	/**
	 * Returns true if the nesting level is the value n.
	 * @param n the nesting level we are interested in.
	 * @return whether the nesting level is n.
	 */
	default boolean isAtNestingLevel( final int n ) {
		return n == this.nestingLevel();
	}
	
	/**
	 * This method adds the attribute key=value to the start tag
	 * that is under construction. The builder method starTagOpen must
	 * have been the immediately previous method.
	 * @param key the attribute key 
	 * @param value the attribute value
	 */
	void put( String key, String value );
	
	/**
	 * This method finishes the construction of the current start tag.
	 * It may be followed by a call to endTag or startTagOpen. If the
	 * tag name is not-null it must agree with the previous value. If
	 * the previous value was null then it automatically is in agreement.
	 * 
	 * An implementation may choose to make startTagClose optional,
	 * implicitly closing it when the next startTagOpen is invoked.
	 * 
	 * @param name the name of the element to be constructed (or null)
	 */
	void startTagClose( String name );
	
	/**
	 * This method finishes the construction of the current element.
	 * If the tag-name is non-null then it must be in agreement with the
	 * previous value. If the previous value is null then it is automatically
	 * in agreement. 
	 * 
	 * @param name the name of the element to be constructed (or null) 
	 */
	void endTag( String name );
	
	/**
	 * This method creates a new builder that will build elements of
	 * the same implementation type as this builder.
	 * @return a new builder
	 */
	@NonNull MinXMLBuilder newBuilder();
	
	/**
	 * This method copies an arbitrary MinXML tree into the 
	 * current tree build.
	 */
	default void mergeElement( final @NonNull MinXML tree ) {
		final String name = tree.getName();
		this.startTagOpen( name );
		this.startTagClose( name );
		for ( MinXML child : tree ) {
			this.mergeElement( child );
		}
		this.endTag( name );
	}
	
	default void addBuilderElement( @NonNull MinXMLBuilder builder ) {
		@NonNull MinXML new_tree = builder.build(); 
		try {
			this.addElement( new_tree );
		} catch ( UnsupportedOperationException e ) {
			this.mergeElement( new_tree );
		}
	}
	
	/** 
	 * This optional method adds an existing element E into the current
	 * tree build so that the final tree shares store with E.
	 */
	default void addElement( final @NonNull MinXML tree ) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * This method returns the first completed tree from the queue of
	 * completed trees, removing it from the queue. If there are no
	 * trees in the queue the default value is returned.
	 * An implementation must make the builder reusable again.
	 *  
	 * @return the constructed tree
	 */
	@Nullable MinXML build( final MinXML default_value );
	
	/**
	 * This method returns the first completed tree from the 
	 * queue of completed trees, removing it from the queue.
	 * If there are no trees in the queue an exception will be thrown.
	 *  
	 * @return the constructed tree
	 */
	default @NonNull MinXML build() {
		final MinXML mnx = this.build( null );
		if ( mnx == null ) {
			throw new Alert( "Trying to build element from no tags" );
		} else {
			return mnx;
		}
	}
	
	/**
	 * This method forces the completion of however much it is 
	 * possible to complete, builds the tree, and returns it.
	 * The implementation of the partially-built tree may be 
	 * different from that of a full build, giving the implementor
	 * the scope to avoid an expensive compaction.
	 * 
	 * If there is nothing to be completed, then the default 
	 * value is returned. 
	 * 
	 * Note that this may be called repeatedly, popping off
	 * completions of the same level.
	 */
	@Nullable MinXML partBuild( final MinXML default_value );
	
	default @NonNull MinXML partBuild() {
		final @Nullable MinXML result = this.partBuild( null );
		if ( result == null ) {
			throw new Alert( "Cannot part build from no tags" );
		}
		return result;
	}
}
