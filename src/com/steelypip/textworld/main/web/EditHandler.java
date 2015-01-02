package com.steelypip.textworld.main.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.chain.Chain;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.powerups.minxson.templates.XHTMLRenderTemplate;
import com.steelypip.textworld.gameclasses.Turn;
import com.steelypip.textworld.gameclasses.loadable.Avatar;
import com.steelypip.textworld.main.World;
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
	final TemplatePage home_page;
	final TemplatePage object_page;
	
	public EditHandler( WebGameEngine game_engine ) {
		this.game_engine = game_engine;
		this.world = game_engine.getWorld();
		this.home_page = new HomePage( this.world, "home.xson" );
		this.object_page = new ObjectPage( this.world, "object.xson" );
	}

	private void renderPage( TemplatePage page, final Map< String, List< String > > parameters, HttpExchange http_exchange ) {
		final MinXML template = page.getTemplate();
		final Map< String, @Nullable MinXML > environment = page.environment( parameters );
		try ( final PrintWriter pw = new PrintWriter( new OutputStreamWriter( http_exchange.getResponseBody() ), true ) ) {
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
			this.renderPage( this.home_page, parameters, http_exchange );
			break;
		case "object":
			this.renderPage( this.object_page, parameters, http_exchange );
			break;
		default:
			throw Alert.unimplemented();
		}
	}

}
