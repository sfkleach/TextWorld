package com.steelypip.textworld.main.web;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.textworld.main.GameEngine;
import com.steelypip.textworld.main.Options;
import com.steelypip.textworld.main.World;
import com.sun.net.httpserver.HttpServer;

public class WebGameEngine extends GameEngine {
	
	public final static int port = 8002;

	public WebGameEngine( World world, Options options   ) {
		super( world, options );
	}
	
	String initialURL() {
		return String.format( "http://localhost:%s/%s", port, this.options.isEditing() ? "edit/home" : "textworld" );
	}

	public void run() {
		if ( ! Desktop.isDesktopSupported() ) {
			throw new Alert( "Java desktop not supported" );
		}

		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress( port ), 0 );
		} catch ( IOException e ) {
			throw new Alert( "Cannot bind to port" ).culprit( "Port", port );
		}
		server.createContext( "/textworld", new GameHandler( server, this ) );
		server.createContext( "/edit", new WikiEditorHandler( options ) );
		server.createContext( "/static", new StaticHandler() );
		server.setExecutor( null ); // creates a default executor
		server.start();
		
		try {
			Desktop.getDesktop().browse( new URI( this.initialURL() ) );
		} catch ( IOException | URISyntaxException e ) {
			throw new Alert( "Cannot open the default web browser" );
		}
	}
	
}
