package com.steelypip.textworld.main;

import java.util.Scanner;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.chain.Chain;
import com.steelypip.textworld.gameclasses.Turn;

public class ConsoleGameEngine extends GameEngine {
	
	final ReadLine in_stream;
	
	public ConsoleGameEngine( @NonNull World world, final ReadLine in_stream, final boolean debugging ) {
		super( world, debugging );
		this.in_stream = in_stream;
	}
	
	@Override
	public void run() {
		Turn turn = new Turn( this.world.getAvatar() );
		this.welcome( turn );
		for (;;) {
			turn.reportOnLocation();
			if ( ! this.world.isActive() ) break;
			final String line = in_stream.readLine();
			if ( line == null ) break;
			final Chain< String > command = Chain.newChain( new Scanner( line ) );
			if ( command.isEmpty() ) continue;
			if ( command.hasSingleMember( "exit" ) || command.hasSingleMember( "quit" ) ) break;
			turn = new Turn( this.world.getAvatar() );
			turn.processCommand( command );
		}
	}
	
}
