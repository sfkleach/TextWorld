package com.steelypip.textworld.main;

import java.io.PrintWriter;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.textworld.gameclasses.Turn;

public abstract class GameEngine {
	
	public abstract void run();
	
	boolean debugging;
	final @NonNull World world;
	
	public GameEngine( final @NonNull World world, final boolean debugging ) {
		this.world = world;
		this.debugging = debugging;
	}
	
	public World getWorld() {
		return world;
	}

	public boolean isDebugging() {
		return debugging;
	}

	public void setDebugging( boolean debugging ) {
		this.debugging = debugging;
	}
		
	public void welcome( Turn turn ) {
		PrintWriter pw = turn.getPrintWriter();
		pw.println( "Welcome to TextWorld 0.1" );
		pw.print( "Entering: " );
		pw.println( this.getWorld().getName() );
		pw.println();
		if ( this.isDebugging() ) {
			this.showWorld();
		}
	}
	
	public void showWorld() {
		this.world.show();
	}
	

}