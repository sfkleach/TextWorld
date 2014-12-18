package com.steelypip.textworld.gameclasses.loadable;

import com.steelypip.textworld.gameclasses.GameObject;
import com.steelypip.textworld.gameclasses.Thing;
import com.steelypip.textworld.gameclasses.Turn;


public class Fixture extends Thing {
	
	public void reportFirstImpression( final Turn turn ) {
		turn.report( this.getSummary() );
		for ( GameObject fixture : this.fixtures ) {
			if ( fixture instanceof Thing ) {
				((Thing)fixture).reportFirstImpression( turn );
			}
		}
	}

}
