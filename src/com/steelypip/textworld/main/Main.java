package com.steelypip.textworld.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import jline.ConsoleReader;
import jline.ConsoleReaderInputStream;
import jline.History;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.shell.CmdArgs;
import com.steelypip.powerups.shell.CmdOption;
import com.steelypip.powerups.shell.StdCmdLineProcessor;
import com.steelypip.textworld.main.console.ConsoleGameEngine;
import com.steelypip.textworld.main.web.WebGameEngine;

public class Main extends StdCmdLineProcessor implements Options {
	
	enum Mode {
		ERROR,
		USAGE,
		VERSION,
		CONSOLE,
		WEB
	} 
	
	private Mode mode = Mode.CONSOLE;
	private boolean debugging = false;
	private boolean gamemaster = false;
	private boolean editing = false; 
	private ReadLine input = () -> System.console().readLine( "> " );
	private LinkedList< File > files = new LinkedList<>();
	
	static Logger logger = Logger.getLogger( "com.steelypip.textworld" );
	
	public static void main( String[] args ) {
		new Main().run( args );
	}
	
	public static String getVersion() {
		try ( BufferedReader lines = new BufferedReader( new InputStreamReader( Main.class.getResourceAsStream( "version.txt" ) ) ) ) {
			return lines.readLine();
		} catch ( IOException e ) {
			return "<version info unavailable>";
		}		
	}
	
	public ReadLine getInStream() {
		return input;
	}

	public boolean isDebugging() {
		return debugging;
	}

	public void setDebugging( boolean debugging ) {
		this.debugging = debugging;
	}

	public boolean isEditing() {
		return editing;
	}

	void run( String[] args ) {
		this.processCmdLineArgs( args );
		logger.setLevel( this.debugging ? Level.INFO : Level.WARNING );
		switch ( mode ) {
		case ERROR:
			this.printUsage( System.err );
			break;
		case USAGE:
			this.printUsage( System.out );
			break;
		case VERSION:
			System.out.print( "TextWorld version " );
			System.out.println( getVersion() );
			break;
		case CONSOLE:
			this.runGameInConsoleMode();
			break;
		case WEB:
			this.runGameInWebMode();
			break;
		}
	}
	
	public @NonNull World newWorld() {
		final WorldFactory w = new WorldFactory();
		w.addBuiltIns();
		this.files.forEach( ( File f ) -> w.load( f ) );
		World world = w.newWorld();
		world.getAvatar().setGamemaster( this.gamemaster );
		return world;
	}

	private void runGameInWebMode() {
		WebGameEngine console_game_engine = null;
		try {
			final World world = this.editing ? null : this.newWorld();
			console_game_engine = new WebGameEngine( world, this );
			console_game_engine.run();
		} catch ( Alert alert ) {
			alert.report();
			if ( console_game_engine == null || ! console_game_engine.isDebugging() ) {
				throw alert;
			}
		}
	}


	private void runGameInConsoleMode() {
		ConsoleGameEngine console_game_engine = null;
		try {	
			console_game_engine = new ConsoleGameEngine( this.newWorld(), this );
			console_game_engine.run();
		} catch ( Alert alert ) {
			alert.report();
			if ( console_game_engine == null || ! console_game_engine.isDebugging() ) {
				throw alert;
			}
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
        } else if ( option.is( 'w', "web", "Run in web browser" ) ) {
        	this.mode = Mode.WEB;
        } else if ( option.is( 'e', "edit", "Run in web browser, starting in edit mode" ) ) {
        	this.mode = Mode.WEB;
        	this.editing = true;
        } else if ( option.is(  'j', "jline", "Enables readline editing for UNIX terminals." ) ) {
        	this.input = this.jlineToReadLine();
        } else if ( option.is( 'D', "debugging", "Enables debug output" ) ) {
        	this.debugging = true;
        } else if ( option.is( 'M', "gamemaster", "Enables game-master commands" ) ) {
        	this.gamemaster = true;
        } else {
        	this.mode = Mode.ERROR;
        	return false;
        }
        return true;
	}

}
