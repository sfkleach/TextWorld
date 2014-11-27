package com.steelypip.textworld.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.minxconf.MinXConfParser;
import com.steelypip.powerups.minxml.MinXML;

public class GameEngine {

	public void run( final ReadLine in_stream ) {
		for (;;) {
			final String line = in_stream.readLine();
			if ( line == null ) break;
			if ( "exit".equals( line ) || "quit".equals( line ) ) break;
			System.out.println( "You typed: " + line );
			System.out.println();
		}
	}

	public Object load( final File f ) {
		System.out.println( "Loading " + f );
		try {
			final MinXML xml = new MinXConfParser( new FileReader( f ) ).read();
		} catch ( FileNotFoundException e ) {
			throw new Alert( "Could not find game file", e ).culprit( "File", f );
		}
		return null;
	}

	
}
