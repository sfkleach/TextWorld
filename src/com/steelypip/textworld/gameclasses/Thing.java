package com.steelypip.textworld.gameclasses;

import java.util.Set;

import com.steelypip.textworld.gameclasses.loadable.Avatar;

public abstract class Thing extends GameObject {
	

	public Thing getLocation() {
		final At at = this.getWorld().getAt();
		return at.getLocation( this );
	}
	
	public void setLocation( final Thing place ) {
		final At at = this.getWorld().getAt();
		at.setLocation( this, place );
	}

	public Set< Thing > getExits() {
		final To to = this.getWorld().getTo();
		return to.leadsToLocations();
	}
	
	public String containingPreposition() {
		return "in";
	}
	
	public void reportOnLook( final Avatar avatar ) {
		avatar.reportln( "You cannot see anything." );
	}
	
}
