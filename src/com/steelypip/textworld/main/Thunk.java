package com.steelypip.textworld.main;

import com.steelypip.textworld.gameclasses.Turn;

public abstract class Thunk implements Reportable {
	
	public abstract Object evaluate();

	@Override
	public void report( Turn turn ) {
		final Object r = this.evaluate();
		turn.report( r );
	}
		
	

}
