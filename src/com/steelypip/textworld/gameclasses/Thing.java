package com.steelypip.textworld.gameclasses;

import java.util.Set;

import com.steelypip.textworld.gameclasses.loadable.Avatar;

public abstract class Thing extends GameObject {
	
	public Thing getLocation() {
		final At at = this.getWorld().getAt();
		return at.getLocation( this );
	}
	
	public void setLocation( final Thing place ) {
		System.out.print( "Setting the location of ... " );
		System.out.print( this );
		System.out.print( " to " );
		System.out.println( place );
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
	
	public ActiveValue< ? extends Object > spawnsAtAsActiveValue() {
		return (
			new ActiveValue< Thing >() {

				@Override
				public Thing get() {
					return Thing.this.getLocation();
				}

				@Override
				public void set( Thing place ) {
					Thing.this.setLocation( place );
				}

				@Override
				public void setDefinition( Object definition ) {
					Thing.this.setLocation( (Thing)definition );
				}
				
			}
		);
	}
	

	
	public void reportOnLook( final Avatar avatar ) {
		avatar.report( "You are " );
		avatar.report( this.containingPreposition() );
		avatar.report( ' ' );
		avatar.report( this.getName() );
		avatar.reportln( '.' );
	}
	
}
