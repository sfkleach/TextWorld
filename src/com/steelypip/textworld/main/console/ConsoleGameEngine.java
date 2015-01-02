package com.steelypip.textworld.main.console;

import java.util.Scanner;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.chain.Chain;
import com.steelypip.textworld.gameclasses.Turn;
import com.steelypip.textworld.main.GameEngine;
import com.steelypip.textworld.main.Options;
import com.steelypip.textworld.main.ReadLine;
import com.steelypip.textworld.main.World;

public class ConsoleGameEngine extends GameEngine {
	
	
	public ConsoleGameEngine( @NonNull World world, Options options  ) {
		super( world, options );
	}
	
	@Override
	public void run() {
		Turn turn = new Turn( this.world.getAvatar() );
		this.welcome( turn );
		for (;;) {
			turn.reportOnLocation();
			if ( ! this.world.isActive() ) break;
			final String line = options.getInStream().readLine();
			if ( line == null ) break;
			final Chain< String > command = Chain.newChain( new Scanner( line ) );
			if ( command.isEmpty() ) continue;
			if ( command.hasSingleMember( "exit" ) || command.hasSingleMember( "quit" ) ) break;
			turn = new Turn( this.world.getAvatar() );
			turn.processCommand( command );
		}
	}
	
}
