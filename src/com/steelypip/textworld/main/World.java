package com.steelypip.textworld.main;

import java.util.Map;
import java.util.Map.Entry;

public class World {
	
	final Map< String, GameObject > world;

	public World( Map< String, GameObject > world ) {
		super();
		this.world = world;
	}
	
	public void show() {
		for ( Entry< String, GameObject > e : this.world.entrySet() ) {
			System.out.println( "Variable " + e.getKey() );
			e.getValue().show();
			System.out.println();
		}
	}
}
