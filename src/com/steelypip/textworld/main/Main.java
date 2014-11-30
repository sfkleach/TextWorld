package com.steelypip.textworld.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;

import org.eclipse.jdt.annotation.NonNull;

import jline.ConsoleReader;
import jline.ConsoleReaderInputStream;
import jline.History;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.shell.CmdArgs;
import com.steelypip.powerups.shell.CmdOption;
import com.steelypip.powerups.shell.StdCmdLineProcessor;

public class Main extends StdCmdLineProcessor {
	
	enum Mode {
		ERROR,
		USAGE,
		VERSION,
		CONSOLE,
		WEB
	} 
	
	Mode mode = Mode.CONSOLE;
	ReadLine input = () -> System.console().readLine( "> " );
	LinkedList< File > files = new LinkedList<>();
	
	public static void main( String[] args ) {
		new Main().run( args );
	}
	
	void run( String[] args ) {
		this.processCmdLineArgs( args );
		switch ( mode ) {
		case ERROR:
			this.printUsage( System.err );
			break;
		case USAGE:
			this.printUsage( System.out );
			break;
		case VERSION:
			System.out.print( "TextWorld version 0.1" );
			break;
		case CONSOLE:
			this.runGame();
			break;
		case WEB:
			throw Alert.unimplemented();
			//break;
		}
	}
	
	public @NonNull World newWorld() {
		final WorldFactory w = new WorldFactory();
		this.files.forEach( ( File f ) -> w.load( f ) );
		return w.newWorld();		
	}

	public void runGame() {
		try {
			final GameEngine e = new GameEngine( this.newWorld() );
			System.out.println( "This is some example welcome text" );
			System.out.println( "And this is some more" );
			e.showWorld();
			e.run( this.input );
		} catch ( Alert alert ) {
			alert.report();
		}
	}

	public void printUsageHeader( final PrintWriter err ) {
        err.println( "Usage: textworld [OPTIONS]" );
	}

	@Override
	public void processRest( CmdArgs rest ) {
		rest.processFileArgs( ( File x ) -> this.files.add( x ) );
	}
	
	@SuppressWarnings("resource")
	private ReadLine jlineToReadLine() {
		try {
			final ConsoleReader reader = new ConsoleReader();
			reader.setHistory( new History( new File( System.getProperty( "user.home" ), ".jline-history" ) ) );
			InputStream input_stream = new ConsoleReaderInputStream ( reader, "> " );
			BufferedReader buffered_reader = new BufferedReader( new InputStreamReader( input_stream ) );
			return ( 
				() -> { 
					try { 
						final String line = buffered_reader.readLine();
						if ( line == null ) buffered_reader.close();
						return line;
					} catch ( IOException e ) { 
						throw new Alert( e ); 
					} 
				}
			);
		} catch ( IOException e ) {
			throw new Alert( e );
		}
		
	}

	@Override
	public boolean option( CmdOption option, CmdArgs args ) {
        if ( option.is( 'V', "version", "Print the version number." ) ) {
        	this.mode = Mode.VERSION;
        } else if ( option.is( 'U', "usage", "Prints this usage message." ) ) {
            this.mode = Mode.USAGE;
        } else if ( option.is(  'c', "console", "Run in console mode" ) ) {
        	this.mode = Mode.CONSOLE;
        } else if ( option.is(  'j', "jline", "Enables readline editing for UNIX terminals." ) ) {
        	this.input = this.jlineToReadLine();
        } else {
        	this.mode = Mode.ERROR;
        	return false;
        }
        return true;
	}

}
