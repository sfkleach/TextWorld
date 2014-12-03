package com.steelypip.textworld.gameclasses.loadable;

import java.util.Objects;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.chain.Chain;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.textworld.gameclasses.Agent;
import com.steelypip.textworld.gameclasses.At;
import com.steelypip.textworld.gameclasses.Thing;
import com.steelypip.textworld.main.World;

public class Avatar extends Agent {

	static class PreviousLocation {
		
		Thing current_place = null;
		
		public boolean hasLocationChanged( Thing place ) {
			if ( this.current_place == place ) return false;
			this.current_place = place;
			return true;
		}
		
	}
	
	static PreviousLocation previous_location = new PreviousLocation();
	
	public void reportOnLocation() {
		final World world = this.getWorld();
		final At at = world.getAt();
		final Thing place = Objects.requireNonNull( at.getLocation( this ) );
		if ( previous_location.hasLocationChanged( place ) ) {
			this.report( "You are " );
			this.report( place.containingPreposition() );
			this.report( ' ' );
			this.reportln( place.getName() );
		}
	}
	
	public void cmdLook() {
		this.getLocation().reportOnLook( this );	
	}
	
	public void cmdGo() {
		if ( this.getLocation().getExits().isEmpty() ) {
			this.reportln( "There is nowhere to go from here." );
		} else {
			throw Alert.unimplemented();
		}
	}

	public void processCommand( Chain< String > command ) {
		if ( command.hasSingleMember( "look" ) ) {
			this.cmdLook();
		} else if ( command.hasSingleMember( "go" ) ) {
			this.cmdGo();
		} else {
			this.reportln( "Sorry, I don't understand that." );
		}
	}

	public void report( final char ch ) {
		System.out.print( ch );
	}

	public void report( final String string ) {
		System.out.print( string );
	}

	public void reportln( final char ch  ) {
		System.out.println( ch );
	}

	public void reportln( final String string ) {
		System.out.println( string );
	}

}
