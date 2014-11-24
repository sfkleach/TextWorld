package com.steelypip.powerups.repeaters;

import java.util.Iterator;

/**
 * A Repeater is a kind of iterator. It is not backed by an underlying collection
 * however so the remove method is not supported. Repeaters may be peekable, supporting
 * a 1-item lookahead, or pushable, allowing items to be "unread" by pushing back onto
 * the stream.
 * 
 *  When the stream is exhausted, an attempt to read past the end of the stream will
 *  throw an error. However, next and peek can supply a default values to return in the case
 *  of stream exhaustion. Skipping will not throw an exception at stream end.
 */

public interface Repeater< T > extends Iterator< T > {
	public void skip();
	public T next( T value_if_at_end );
}
