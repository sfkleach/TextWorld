package com.steelypip.textworld.main;

import org.eclipse.jdt.annotation.NonNull;

public class GameEngine {
	
	final @NonNull World world;
	
	public GameEngine( @NonNull World world ) {
		this.world = world;
	}
	
	public void showWorld() {
		this.world.show();
	}

	public void run( final ReadLine in_stream ) {
		for (;;) {
			final String line = in_stream.readLine();
			if ( line == null ) break;
			if ( "exit".equals( line ) || "quit".equals( line ) ) break;
			System.out.println( "You typed: " + line );
			System.out.println();
		}
	}
	
}
