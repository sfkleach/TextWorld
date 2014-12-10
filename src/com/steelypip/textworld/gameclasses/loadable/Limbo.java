package com.steelypip.textworld.gameclasses.loadable;

import com.steelypip.textworld.gameclasses.Turn;


public class Limbo extends Place {

	public void reportOnLook( final Turn turn ) {
		turn.reportln( "You cannot see anything." );
	}

}
