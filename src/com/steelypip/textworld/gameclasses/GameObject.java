package com.steelypip.textworld.gameclasses;

import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.textworld.main.World;

public abstract class GameObject {
	
	protected World world;
	
	public World getWorld() {
		return world;
	}

	public GameObject setWorld( World world ) {
		this.world = world;
		return this;
	}

	public abstract void init( MinXML initial_configuration );
	
	public void show() {
		System.out.println( this.toString() );
	}
	
}
