package com.steelypip.textworld.main;

import java.util.List;
import java.util.Random;

public class Dice extends Thunk {
	
	final int num_sides;
	final List< Object > keys;
	final List< Object > values;
	final Object default_value;
	Random rand = new Random();
		
	public Dice( final int n, final List< Object > keys, final List< Object > values, final Object default_value ) {
		this.num_sides = n;
		this.keys = keys;
		this.values = values;
		this.default_value = default_value;
	}
	
	private int roll() {
		return this.rand.nextInt( num_sides + 1 );
	}

	@Override
	public Object evaluate() {
		final long n = this.roll();
//		System.err.println( "We rolled " + n );
		int i = 0;
		for ( Object key : this.keys ) {
//			System.err.println( "Comparing against " + key );
			if ( key.equals( n ) ) {
				return this.values.get( i );
			}
			i += 1;
		}
		return this.default_value;
	}

}
