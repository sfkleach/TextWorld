package com.steelypip.textworld.main;

import java.io.PrintWriter;

import com.steelypip.textworld.gameclasses.Turn;

public abstract class GameEngine {
	
	public abstract void run();
	
	final protected World world;
	final protected Options options;
	
	public GameEngine( final World world, final Options options   ) {
		this.world = world;
		this.options = options;
	}
	
	public World getWorld() {
		return world;
	}

	public boolean isDebugging() {
		return this.options.isDebugging();
	}

	public void setDebugging( boolean debugging ) {
		this.options.setDebugging( debugging );
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