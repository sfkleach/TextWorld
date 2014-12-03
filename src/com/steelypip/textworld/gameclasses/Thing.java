package com.steelypip.textworld.gameclasses;

import java.util.Set;

import com.steelypip.textworld.gameclasses.loadable.Avatar;

public abstract class Thing extends GameObject {
	
	private String name;
	
	public String getDefaultName() {
		final String class_name = this.getClass().getName();
		final int n = class_name.lastIndexOf( '.' );
		if ( n == -1 ) return class_name;
		return class_name.substring( n + 1 );
	}
	
	public String getName() {
		return this.name != null ? this.name : this.getDefaultName();
	}

	public void setName( String name ) {
		this.name = name;
	}

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
