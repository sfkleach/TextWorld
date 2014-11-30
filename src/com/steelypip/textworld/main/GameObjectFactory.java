package com.steelypip.textworld.main;

import com.steelypip.textworld.gameclasses.Dummy;


public class GameObjectFactory {
	
	public static GameObject createBlank( final String category ) {
		return new Dummy();
	}
	
	
}
