package com.steelypip.textworld.main.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.powerups.minxson.templates.XHTMLRenderTemplate;
import com.steelypip.textworld.main.Options;
import com.sun.net.httpserver.HttpExchange;

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


public class WikiEditorHandler extends WikiPage {
	
	final Options options;
//	final WebGameEngine game_engine;
//	final World world;
	final TemplatePage home_page;
	final TemplatePage object_page;
	
	public WikiEditorHandler( Options options ) {
		this.options = options;
//		this.game_engine = game_engine;
//		this.world = game_engine.getWorld();
		this.home_page = new HomePage( options.getGameFolder(), "home.xson" );
		this.object_page = new ObjectPage( options.getGameFolder(), "object.xson" );
	}

	private void renderPage( TemplatePage page, final Parameters parameters, HttpExchange http_exchange ) {
		final MinXML template = page.getTemplate();
		final Map< String, @Nullable MinXML > environment = page.environment( parameters );
		try ( final PrintWriter pw = new PrintWriter( new OutputStreamWriter( http_exchange.getResponseBody() ), true ) ) {
			http_exchange.sendResponseHeaders( 200, 0 );
			new XHTMLRenderTemplate( pw, environment ).render( template );
		} catch ( IOException e ) {
			throw new Alert( e );
		}
	}
	
	@Override
	public void handle( HttpExchange http_exchange ) throws IOException {
		try {
			final URI uri = http_exchange.getRequestURI();
			final String path = uri.getPath();
			final int n = path.lastIndexOf( '/' );
			final String word = n == -1 ? path : path.substring( n + 1 );
			final Parameters parameters = this.unpackRequestFields( http_exchange );
			switch ( word ) {
			case "home":
				this.renderPage( this.home_page, parameters, http_exchange );
				break;
			case "object":
				this.renderPage( this.object_page, parameters, http_exchange );
				break;
			default:
				throw Alert.unimplemented();
			}
		} catch ( Throwable t ) {
			new Alert( t ).report();
			throw t;
		}
	}
		

}
