package com.steelypip.powerups.repeaters;

public interface PeekableRepeater<T > extends Repeater< T > {
	T peek();
	T peek( T value_if_at_end );
	//void putBack( T prev );
}
