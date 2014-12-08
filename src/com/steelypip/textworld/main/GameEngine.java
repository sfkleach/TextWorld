package com.steelypip.textworld.main;

import java.util.Scanner;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.chain.Chain;

public class GameEngine {
	
	final @NonNull World world;
	
	public GameEngine( @NonNull World world ) {
		this.world = world;
	}
	
	public void showWorld() {
		this.world.show();
	}

	public void run( final ReadLine in_stream ) {
		while ( this.world.isActive() ) {
			this.world.getAvatar().reportOnLocation();
			final String line = in_stream.readLine();
			if ( line == null ) break;
			final Chain< String > command = Chain.newChain( new Scanner( line ) );
			if ( command.isEmpty() ) continue;
			if ( command.hasSingleMember( "exit" ) || command.hasSingleMember( "quit" ) ) break;
			this.world.getAvatar().processCommand( command );
		}
	}

	public @NonNull World getWorld() {
		return this.world;
	}
	
}
