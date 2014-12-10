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
	
	public void reportFirstImpression( final Turn turn ) {
		turn.report( "You are " );
		turn.report( this.containingPreposition() );
		turn.report( ' ' );
		turn.report( this.getSummary() );
		turn.reportln( '.' );
		turn.setRecentlySeenLocation();
	}
	
	public void reportRepeatImpression( final Turn turn ) {
		if ( ! turn.hasRecentlySeenLocation() ) {
			turn.report( "You are " );
			turn.report( this.containingPreposition() );
			turn.report( ' ' );
			turn.report( this.getName() );
			turn.reportln( '.' );
		}
	}
	
	public void reportOnLook( final Turn turn ) {
		this.reportFirstImpression( turn );
	}
	
}
