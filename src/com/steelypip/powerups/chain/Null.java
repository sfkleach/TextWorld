package com.steelypip.powerups.chain;

public class Null< T > extends Chain< T > {

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public T getHead() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Chain< T > getTail() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public T get( int n ) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean hasSize( final int n ) {
		return n == 0;
	}
	
	

}
