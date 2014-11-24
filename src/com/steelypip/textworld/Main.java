package com.steelypip.textworld;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.shell.CmdArgs;
import com.steelypip.powerups.shell.CmdOption;
import com.steelypip.powerups.shell.StdCmdLineProcessor;

public class Main extends StdCmdLineProcessor {
	
	enum Mode {
		USAGE,
		VERSION,
		CONSOLE,
		WEB
	} 
	
	Mode mode = Mode.CONSOLE;
	
	public static void main( String[] args ) {
		new Main().run( args );
	}
	
	void run( String[] args ) {
		this.processCmdLineArgs( args );
		switch ( mode ) {
		case USAGE:
			this.printUsage( System.err );
			break;
		case VERSION:
			System.out.print( "TextWorld version 0.1" );
			break;
		case CONSOLE:
			throw Alert.unimplemented();
			//break;
		case WEB:
			throw Alert.unimplemented();
			//break;
		}
	}

	public void printUsageHeader( final PrintWriter err ) {
        err.println( "Usage: textworld [OPTIONS]" );
	}

	@Override
	public void processRest( CmdArgs rest ) {
		//	These are the game folders.
	}

	@Override
	public boolean option( CmdOption option, CmdArgs args ) {
        if ( option.is( 'V', "version", "Print the version number." ) ) {
        	this.mode = Mode.VERSION;
        } else if ( option.is(  'c', "console", "Run in console mode" ) ) {
        	this.mode = Mode.CONSOLE;
        } else {
        	this.mode = Mode.USAGE;
        	return false;
        }
        return true;
	}

}
