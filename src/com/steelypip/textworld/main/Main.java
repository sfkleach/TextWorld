package com.steelypip.textworld.main;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.Scanner;

import jline.ConsoleReader;
import jline.ConsoleReaderInputStream;
import jline.History;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.chain.Chain;
import com.steelypip.powerups.shell.CmdArgs;
import com.steelypip.powerups.shell.CmdOption;
import com.steelypip.powerups.shell.StdCmdLineProcessor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Main extends StdCmdLineProcessor {
	
	enum Mode {
		ERROR,
		USAGE,
		VERSION,
		CONSOLE,
		WEB
	} 
	
	Mode mode = Mode.CONSOLE;
	boolean debugging = false;
	boolean gamemaster = false;
	ReadLine input = () -> System.console().readLine( "> " );
	LinkedList< File > files = new LinkedList<>();
	
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

	static class GameHandler implements HttpHandler {
		
		private static final String COMMAND = "command=";
		HttpServer http_server;
		final GameEngine game_engine;
		final World world;
		boolean welcomed = false;

		
		public GameHandler( HttpServer http_server, GameEngine game_engine ) {
			this.http_server = http_server;
			this.game_engine = game_engine;
			this.world = game_engine.getWorld();
		}
		
		private void message( final String command_line, PrintWriter pw ) {
			System.err.println( "COMMAND " + command_line );
			pw.println( "<html>" );
			pw.println( "<head>" );
			pw.println( "</head>" );
			pw.println( "<body>" );
			pw.println( "<pre>" );
			if ( ! welcomed ) {
				this.game_engine.welcome();
				this.welcomed = true;	
			}
			try {
				final Chain< String > command = Chain.newChain( new Scanner( command_line != null ? command_line : "" ) );
				if ( command.hasSingleMember( "exit" ) || command.hasSingleMember( "quit" ) ) {
					this.world.setIsActive( false );
				} else {
					if ( command.isntEmpty() ) {
						this.world.getAvatar().processCommand( command );
					}				
				}
			} catch ( Alert alert ) {
				alert.report();
				if ( ! this.game_engine.isDebugging() ) {
					throw alert;
				}
			}			
			pw.println( "</pre>" );
			if ( this.game_engine.getWorld().isActive() ) {
				pw.println( "<pre>" );
				this.world.getAvatar().reportOnLocation();
				pw.println( "<form method=\"get\">" );
				pw.println( "<input type=\"text\" name=\"command\"/>" );
				pw.println( "</form>" );
				pw.println( "</pre>" );
			}
			pw.println( "</body>" );
			pw.println( "</html>" );
		}

		String getCommand( HttpExchange http_exchange ) {
			final String query = http_exchange.getRequestURI().getRawQuery();
			if ( query != null ) {
				for ( String binding : query.split( "&" ) ) {
					if ( binding.startsWith( COMMAND ) ) {
						try {
							return URLDecoder.decode( binding.substring( COMMAND.length() ), "UTF-8" );
						} catch ( UnsupportedEncodingException e ) {
							throw Alert.unreachable();
						}
					}
				}
			}
			return null;
		}
		
		@Override
		public void handle( HttpExchange http_exchange ) throws IOException {
			http_exchange.sendResponseHeaders( 200, 0 );
			try ( final PrintWriter pw = new PrintWriter( new OutputStreamWriter( http_exchange.getResponseBody() ) ) ) {
				this.game_engine.getWorld().getAvatar().setPrintWriter( pw );
				this.message( this.getCommand( http_exchange ), pw );
			}
			if ( !this.world.isActive() ) {
				this.http_server.stop( 0 );
			}
		}
		
	}
	
	private void runGameInWebMode() {
		if ( Desktop.isDesktopSupported() ) {
			HttpServer server;
			try {
				server = HttpServer.create(new InetSocketAddress( 8001 ), 0 );
			} catch ( IOException e ) {
				throw new Alert( "Cannot bind to port 8001" );
			}
			server.createContext("/textworld", new GameHandler( server, new GameEngine( this.newWorld() ) ) );
			server.setExecutor( null ); // creates a default executor
			server.start();
			
			try {
				Desktop.getDesktop().browse( new URI( "http://localhost:8001/textworld" ) );
			} catch ( IOException | URISyntaxException e ) {
				throw new Alert( "Cannot open the default web browser" );
			}
		} else {
			throw new Alert( "Java desktop not supported" );
		}
	}

	public void runGameInConsoleMode() {
		GameEngine game_engine = null;
		try {	
			game_engine = new GameEngine( this.newWorld() );
			game_engine.setDebugging( this.debugging );
			game_engine.welcome();
			game_engine.run( this.input );
		} catch ( Alert alert ) {
			alert.report();
			if ( game_engine == null || ! game_engine.isDebugging() ) {
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
