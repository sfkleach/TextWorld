package com.steelypip.textworld.gameclasses.loadable;

import com.steelypip.textworld.gameclasses.Agent;
import com.steelypip.textworld.gameclasses.Thing;

public class Avatar extends Agent {
	
	public static class PreviousLocation {
		
		Thing current_place = null;
		
		public boolean hasLocationChanged( Thing place ) {
			if ( this.current_place == place ) return false;
			this.current_place = place;
			return true;
		}
		
	}
	
	PreviousLocation previous_location = new PreviousLocation();
	
	public boolean hasLocationChanged( final Thing place ) {
		return this.previous_location.hasLocationChanged( place );
	}
	
	boolean gamemaster = false;

	
	public boolean isGamemaster() {
		return this.gamemaster;
	}

	public void setGamemaster( final boolean gamemaster ) {
		this.gamemaster = gamemaster;
	}

	
//	public void processCommand( Chain< String > command ) {
//		final Turn turn = new Turn( this );
//		turn.processCommand( command );
////		if ( ! this.isGamemaster() && isPrivilegedCommand( command ) ) {
////			this.print_writer.println( "Sorry, that's not something I can do right now" );
////			return;
////		} else if ( command.isEmpty() ) {
////			return;
////		}
////		final String op = command.getHead();
////		if ( "look".compareToIgnoreCase( op  ) == 0 ) {
////			turn.cmdLook( command );
////		} else if ( "go".compareToIgnoreCase( op  ) == 0 ) {
////			turn.cmdGo( command );
////		} else if ( "!teleport" .compareToIgnoreCase( op  ) == 0 ) {
////			turn.cmdGMTeleport( command );
////		} else {
////			turn.reportln( "Sorry, I don't understand that." );
////		}
//	}

//	public void report( final char ch ) {
//		this.print_writer.print( ch );
//	}
//
//	public void report( final String string ) {
//		this.print_writer.print( string );
//	}
//
//	public void reportln( final char ch  ) {
//		this.print_writer.println( ch );
//	}
//
//	public void reportln( final String string ) {
//		this.print_writer.println( string );
//	}

}
