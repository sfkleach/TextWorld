package com.steelypip.textworld.gameclasses.loadable;

import java.util.Objects;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.chain.Chain;
import com.steelypip.textworld.gameclasses.Agent;
import com.steelypip.textworld.gameclasses.At;
import com.steelypip.textworld.gameclasses.GameObject;
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
	
	PreviousLocation previous_location = new PreviousLocation();
	boolean gamemaster = false;

	
	public boolean isGamemaster() {
		return this.gamemaster;
	}

	public void setGamemaster( final boolean gamemaster ) {
		this.gamemaster = gamemaster;
	}

	public void reportOnLocation() {
		final World world = this.getWorld();
		final At at = world.getAt();
		final Thing place = Objects.requireNonNull( at.getLocation( this ) );
		if ( previous_location.hasLocationChanged( place ) ) {
			this.report( "You are " );
			this.report( place.containingPreposition() );
			this.report( ' ' );
			this.report( place.getName() );
			this.reportln( '.' );
		}
	}
	
	public static boolean isPrivilegedCommand( Chain< String > command ) {
		for ( String word : command ) {
			if ( word.length() >= 2 && word.charAt( 0 ) == '!' ) {
				return true;
			}
		}		
		return false;
	}

	public void processCommand( Chain< String > command ) {
		if ( ! this.isGamemaster() && isPrivilegedCommand( command ) ) {
			System.err.println( "Sorry, that's not something I can do right now" );
			return;
		} else if ( command.isEmpty() ) {
			return;
		}
		final String op = command.getHead();
		if ( "look".compareToIgnoreCase( op  ) == 0 ) {
			this.cmdLook( command );
		} else if ( "go".compareToIgnoreCase( op  ) == 0 ) {
			this.cmdGo( command );
		} else if ( "!teleport" .compareToIgnoreCase( op  ) == 0 ) {
			this.cmdGMTeleport( command );
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
	
	public void cmdLook( Chain< String > command ) {
		this.getLocation().reportOnLook( this );	
	}
	
	public void cmdGo( Chain< String > command ) {
		if ( this.getLocation().getExits().isEmpty() ) {
			this.reportln( "There is nowhere to go from here." );
		} else {
			throw Alert.unimplemented();
		}
	}
	
	public void cmdGMTeleport( Chain< String > command ) {
		command = command.getTail();
		if ( command.hasSize( 1 ) ) {
			final GameObject destination = this.findByUID( command.get( 0 ) );
			if ( destination instanceof Place && destination != this ) {
				this.getWorld().getAt().setLocation( this, (Place)destination );
			} else {
				System.err.println( "Not the UID of a valid destination" );
				System.err.println( "  * The UID was " + command.get( 0 ) );
				System.err.println( "  * We found " + destination );
			}
		} else if ( command.hasSizeAtLeast( 2 ) ) {
			final GameObject subject = this.findByUID( command.get( 0 ) );
			if ( "to".equals( command.get( 1 ) ) ) {
				command = command.getTail();
			}
			if ( command.hasSize( 1 ) ) {
				final GameObject destination = this.findByUID( command.get( 1 ) );
				if ( subject instanceof Thing && destination instanceof Place && destination != subject ) {
					this.getWorld().getAt().setLocation( (Thing)subject, (Place)destination );
				} else {
					System.err.println( "Not the UIDs of a valid subject and destination" );				
				}
			} else {
				cmdGMTeleportUsage();
			}
		} else {
			cmdGMTeleportUsage(); 			
		}
	}

	private void cmdGMTeleportUsage() {
		System.err.println( "Usage: !teleport <L:uid> - teleports yourself to location L" ); 
		System.err.println( "       !teleport <T:uid> [to] <L:uid> - teleports thing T to location L" );
	}


	

}
