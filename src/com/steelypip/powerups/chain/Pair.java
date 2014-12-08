package com.steelypip.powerups.chain;

import com.steelypip.powerups.alert.Alert;

public class Pair< T > extends Chain< T > {
	
	T head;
	Chain< T > tail;
	
	public Pair( T head, Chain< T > tail ) {
		super();
		this.head = head;
		this.tail = tail;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public T getHead() throws UnsupportedOperationException {
		return this.head;
	}

	@Override
	public Chain< T > getTail() throws UnsupportedOperationException {
		return this.tail;
	}

	@Override
	public int size() {
		return 1 + this.tail.size();
	}
	
	@Override
	public boolean hasSize( final int n ) {
		return n > 0 && this.tail.hasSize( n - 1 );
	}

	@Override
	public T get( final int n ) throws UnsupportedOperationException {
		if ( n <= 0 ) {
			if ( n < 0 ) {
				throw new Alert( "Negative argument to get" ).culprit( "Argument", n );
			} else {
				return this.head;
			}
		} else {
			return this.tail.get( n - 1 );
		}
	}

	@Override
	public boolean hasSizeAtLeast( final int n ) {
		return n <= 1 || this.tail.hasSizeAtLeast( n - 1 );
	}
	
	
	
}
