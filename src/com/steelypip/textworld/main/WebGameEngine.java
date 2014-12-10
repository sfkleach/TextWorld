package com.steelypip.textworld.main;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.alert.Alert;
import com.sun.net.httpserver.HttpServer;

public class WebGameEngine extends GameEngine {

	public WebGameEngine( @NonNull World world, boolean debugging ) {
		super( world, debugging );
		// TODO Auto-generated constructor stub
	}

	public void run() {
		if ( ! Desktop.isDesktopSupported() ) {
			throw new Alert( "Java desktop not supported" );
		}

		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress( 8001 ), 0 );
		} catch ( IOException e ) {
			throw new Alert( "Cannot bind to port 8001" );
		}
		server.createContext( "/textworld", new GameHandler( server, this ) );
		server.createContext( "/static", new StaticHandler() );
		server.setExecutor( null ); // creates a default executor
		server.start();
		
		try {
			Desktop.getDesktop().browse( new URI( "http://localhost:8001/textworld" ) );
		} catch ( IOException | URISyntaxException e ) {
			throw new Alert( "Cannot open the default web browser" );
		}
	}
	
}
