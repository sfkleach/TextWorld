package com.steelypip.powerups.chain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;

public abstract class Chain< T > implements Iterable< T > {
	
	public static <T> Chain< T > newChain( T[] data ) {
		Chain< T > sofar= new Null<>();
		for ( int i = data.length - 1; i >= 0; i-- ) {
			sofar = new Pair< T >( data[ i ], sofar );
		}
		return sofar;
	}
	
	public static <T> Chain< T > newChain( Iterator< T > data ) {
		if ( data.hasNext() ) {
			return new Pair< T >( data.next(), newChain( data ) );
		} else {
			return new Null<>();
		}
	}
	
	public static <T> Chain< T > newChain( Iterable< T > data ) {
		return newChain( data.iterator() );
	}
	
	public Iterator< T > iterator() {
		return new Iterator< T >() {
			
			private Chain< T > sofar = Chain.this;

			@Override
			public boolean hasNext() {
				return ! sofar.isEmpty();
			}

			@Override
			public T next() {
				final T t = this.sofar.getHead();
				this.sofar = this.sofar.getTail();
				return t;
			}
			
		};
	}
	
	public abstract int size();
	
	public abstract boolean hasSize( int n );

		
	public abstract boolean isEmpty();
	
	public boolean isntEmpty() { return ! this.isEmpty(); }
	
	public boolean hasSingleMember( final T t ) {
		if ( ! this.hasSize( 1 ) ) return false;
		final T head = this.getHead();
		return t == head || t != null && t.equals( head ); 
	}
	
	public abstract T getHead() throws UnsupportedOperationException;
	
	public abstract Chain< T > getTail() throws UnsupportedOperationException;
	
	public abstract T get( int n ) throws UnsupportedOperationException;

	public < U > Chain< U > map( Function< T, U > function ) {
		if ( this.isEmpty() ) {
			return new Null< U >();
		} else {
			return new Pair< U >( function.apply( this.getHead() ), this.getTail().map( function ) );
		}
	}
	
	
	
}
