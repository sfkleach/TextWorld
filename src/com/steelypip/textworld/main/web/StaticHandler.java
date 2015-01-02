package com.steelypip.textworld.main.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class StaticHandler implements HttpHandler {

	@Override
	public void handle( HttpExchange t ) throws IOException {
        URI uri = t.getRequestURI();
//        System.out.println("looking for: "+ uri.getPath());
        String path = uri.getPath();
        String resource_name = new File( path ).getName();
        try ( InputStream fs = this.getClass().getResourceAsStream( resource_name ) ) {
	       
			// Object exists and is a file: accept with response code 200.
			String mime = "text/html";
			if ( path.substring( path.length()-3 ).equals( ".js" ) ) mime = "application/javascript";
			if ( path.substring( path.length()-4 ).equals( ".css" ) ) mime = "text/css";            
			
			Headers h = t.getResponseHeaders();
			h.set( "Content-Type", mime );
			t.sendResponseHeaders( 200, 0 );              
			
			try ( OutputStream os = t.getResponseBody() ) {
				final byte[] buffer = new byte[0x10000];
				int count = 0;
				while ( (count = fs.read(buffer) ) >= 0 ) {
				    os.write( buffer, 0, count );
				}
			}
			
        }
    }  

}
