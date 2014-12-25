package com.steelypip.textworld.main;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.Nullable;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.chain.Chain;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.powerups.minxson.templates.XHTMLRenderTemplate;
import com.steelypip.textworld.gameclasses.Turn;
import com.steelypip.textworld.gameclasses.loadable.Avatar;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This is the class that implements the wiki-style editor. It responds to 
 * the following URLs:
 * 
 * http://<hostname>/edit/home
 * http://<hostname>/edit/game-object?id=<ID>
 * http://<hostname>/edit/new-game-object
 * http://<hostname>/edit/debug
 *
 */


public class EditHandler extends WikiPage {
	
	final WebGameEngine game_engine;
	final World world;
	final MinXML home_template;
	
	public EditHandler( WebGameEngine game_engine ) {
		this.game_engine = game_engine;
		this.world = game_engine.getWorld();
		this.home_template = fetchTemplate( "home.xson" );
	}

	Map< String, @Nullable MinXML > homeEnv() {
		final Map< String, @Nullable MinXML > environment = new TreeMap<>();
		final Avatar avatar = world.getAvatar();
		final Turn turn = new Turn( avatar );
		
		environment.put( "version", newString( Main.getVersion() ) );
//		environment.put( "welcome", this.welcome( turn ) );
		environment.put( "response", null );
		environment.put( "aboutLocation", null );
		environment.put( "active", newBoolean( true ) );
		environment.put( "image", null );
		
//		try {
//			final Chain< String > command = Chain.newChain( new Scanner( command_line != null ? command_line : "" ) );
//			if ( command.hasSingleMember( "exit" ) || command.hasSingleMember( "quit" ) ) {
//				this.world.setIsActive( false );
//				environment.put( "active", null );
//			} else {
//				if ( command.isntEmpty() ) {
//					environment.put( "response", this.processCommand( turn, command ) );
//				}				
//			}
//		} catch ( Alert alert ) {
//			alert.report();
//			if ( ! this.game_engine.isDebugging() ) {
//				throw alert;
//			}
//		}
//
//		if ( this.world.isActive() ) {
//			environment.put( "aboutLocation", this.reportOnLocation( turn ) );
//			environment.put( "image", newString( avatar.getLocation().getImage() ) );
//		}
		
		return environment;		
	}
	
	private void renderPage( MinXML template, Map< String, @Nullable MinXML > environment, Map< String, List< String > > parameters, HttpExchange http_exchange ) {
		try ( final PrintWriter pw = new PrintWriter( new OutputStreamWriter( http_exchange.getResponseBody() ) ) ) {
			try {
				http_exchange.sendResponseHeaders( 200, 0 );
				new XHTMLRenderTemplate( pw, environment ).render( template ); 
			} catch ( Exception e ) {
				System.err.println( "RUNTIME ERROR!" );
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void handle( HttpExchange http_exchange ) throws IOException {
		final URI uri = http_exchange.getRequestURI();
		final String path = uri.getPath();
		final int n = path.lastIndexOf( '/' );
		final String word = n == -1 ? path : path.substring( n + 1 );
		final Map< String, List< String > > parameters = this.unpackRequestFields( http_exchange );
		switch ( word ) {
		case "home":
			this.renderPage( this.home_template, this.homeEnv(), parameters, http_exchange );
			break;
		default:
			throw Alert.unimplemented();
		}
	}

}
