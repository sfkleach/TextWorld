package com.steelypip.textworld.gameclasses;

import java.io.PrintWriter;
import java.util.Objects;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.chain.Chain;
import com.steelypip.powerups.io.StringPrintWriter;
import com.steelypip.textworld.gameclasses.loadable.Avatar;
import com.steelypip.textworld.gameclasses.loadable.Place;
import com.steelypip.textworld.main.World;

public class Turn extends TurnReporter {
	
	boolean recently_seen_location = false;
	final Avatar avatar;

	public Turn( Avatar owner ) {
		this.avatar = owner;
	}

	public Turn( Avatar avatar, PrintWriter pw ) {
		this( avatar );
	}

	public Avatar getOwner() {
		return avatar;
	}
	
	public World getWorld() {
		return this.avatar.getWorld();
	}
	
	public GameObject findByUID( final String string ) {
		return this.getWorld().findByUID( string );
	}
	
	public boolean hasRecentlySeenLocation() {
		return this.recently_seen_location;
	}
	
	public void setRecentlySeenLocation() {
		this.recently_seen_location = true;
	}
	
	public void reportOnLocation() {
		final Thing place = Objects.requireNonNull( this.avatar.getLocation() );
		if ( this.avatar.hasLocationChanged( place ) ) {
			place.reportFirstImpression( this );
		} else {
			place.reportRepeatImpression( this );
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
		if ( ! this.avatar.isGamemaster() && isPrivilegedCommand( command ) ) {
			this.reportln( "Sorry, that's not something I can do right now" );
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
	
	public void cmdLook( Chain< String > command ) {
		this.avatar.getLocation().reportOnLook( this );	
		this.setRecentlySeenLocation();
	}
	
	

	public void cmdGo( Chain< String > command ) {
		if ( this.avatar.getLocation().getExits().isEmpty() ) {
			this.reportln( "There is nowhere to go from here." );
		} else {
			throw Alert.unimplemented();
		}
	}	
	
	
	public void cmdGMTeleport( Chain< String > command ) {
		command = command.getTail();
		if ( command.hasSize( 1 ) ) {
			final GameObject destination = this.findByUID( command.get( 0 ) );
			if ( destination instanceof Place && destination != this.avatar ) {
				this.getWorld().getAt().setLocation( this.avatar, (Place)destination );
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