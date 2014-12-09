package com.steelypip.textworld.main;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.Nullable;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.chain.Chain;
import com.steelypip.powerups.io.StringPrintWriter;
import com.steelypip.powerups.json.JSONKeywords;
import com.steelypip.powerups.minxml.FlexiMinXML;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.powerups.minxson.MinXSONParser;
import com.steelypip.powerups.minxson.templates.XHTMLRenderTemplate;
import com.steelypip.textworld.gameclasses.loadable.Avatar;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

class GameHandler implements HttpHandler {
	
	private static final String COMMAND = "command=";
	
	HttpServer http_server;
	final GameEngine game_engine;
	final World world;
	final MinXML template;
	boolean welcomed = false;

	private MinXML fetchTemplate( final String page_name ) {
		Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( page_name ) );
		MinXML template = new MinXSONParser( reader, 'E' ).read();
		return template;
	}
	
	public GameHandler( HttpServer http_server, GameEngine game_engine ) {
		this.http_server = http_server;
		this.game_engine = game_engine;
		this.world = game_engine.getWorld();
		this.template = fetchTemplate( "page.xson" );
	}
	
	
	
	private MinXML newString( final String text ) {
		final MinXML minx = new FlexiMinXML( JSONKeywords.KEYS.CONSTANT );
		minx.putAttribute( JSONKeywords.KEYS.CONSTANT_TYPE, JSONKeywords.KEYS.STRING );
		minx.putAttribute( JSONKeywords.KEYS.CONSTANT_VALUE, text );
		return minx;
	}
	
	private MinXML newBoolean( final boolean value ) {
		final MinXML minx = new FlexiMinXML( JSONKeywords.KEYS.CONSTANT );
		minx.putAttribute( JSONKeywords.KEYS.CONSTANT_TYPE, JSONKeywords.KEYS.BOOLEAN );
		minx.putAttribute( JSONKeywords.KEYS.CONSTANT_VALUE, JSONKeywords.KEYS.BOOLEAN_TRUE );
		return minx;
	}
	
	private @Nullable MinXML welcome( final Avatar avatar ) {
		try ( StringPrintWriter pw = new StringPrintWriter() ) {
			avatar.setPrintWriter( pw );
			if ( ! welcomed ) {
				this.game_engine.welcome();
				this.welcomed = true;	
				return newString( pw.toString() );
			} else {
				return null;
			}
		}
	}
	
	private MinXML processCommand( Avatar avatar, final Chain< String > command ) {
		try ( StringPrintWriter pw = new StringPrintWriter() ) {
			avatar.setPrintWriter( pw );
			avatar.processCommand( command );
			return newString( pw.toString() );
		}
	}
	
	private MinXML reportOnLocation( Avatar avatar ) {
		try ( StringPrintWriter pw = new StringPrintWriter() ) {
			avatar.setPrintWriter( pw );
			avatar.reportOnLocation();
			return newString( pw.toString() );
		}
	}
	
	private void message( final String command_line, PrintWriter pw ) {
		Map< String, @Nullable MinXML > environment = new TreeMap<>();
		Avatar avatar = world.getAvatar();
		
		environment.put( "version", newString( Main.getVersion() ) );
		environment.put( "welcome", this.welcome( avatar ) );
		environment.put( "response", null );
		environment.put( "aboutLocation", null );
		environment.put( "active", newBoolean( true ) );
		environment.put( "image", null );
		
		try {
			final Chain< String > command = Chain.newChain( new Scanner( command_line != null ? command_line : "" ) );
			if ( command.hasSingleMember( "exit" ) || command.hasSingleMember( "quit" ) ) {
				this.world.setIsActive( false );
				environment.put( "active", null );
			} else {
				if ( command.isntEmpty() ) {
					environment.put( "response", this.processCommand( avatar, command ) );
				}				
			}
		} catch ( Alert alert ) {
			alert.report();
			if ( ! this.game_engine.isDebugging() ) {
				throw alert;
			}
		}

		if ( this.world.isActive() ) {
			environment.put( "aboutLocation", this.reportOnLocation( avatar ) );
		}
		
		new XHTMLRenderTemplate( pw, environment ).render( 
			this.world.isActive() ? this.template : fetchTemplate( "bye.xson" ) 
		);
	}

	String findCommand( HttpExchange http_exchange ) {
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
			this.message( this.findCommand( http_exchange ), pw );
		}
		if ( !this.world.isActive() ) {
			final Timer timer = new Timer();
			timer.schedule( 
				new TimerTask() { 
					public void run() { 
						GameHandler.this.http_server.stop( 0 ); 
						timer.cancel();
					} 
				}, 
				1000 
			);
		}
	}
	
}