package com.steelypip.textworld.gameclasses.loadable;


public class Limbo extends Place {

	public void reportOnLook( final Avatar avatar ) {
		avatar.reportln( "You cannot see anything." );
	}

}
