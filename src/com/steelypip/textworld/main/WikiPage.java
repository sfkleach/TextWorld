package com.steelypip.textworld.main;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.json.JSONKeywords;
import com.steelypip.powerups.minxml.FlexiMinXML;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.powerups.minxson.MinXSONParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class WikiPage implements HttpHandler {

	static final String POST = "POST";
	static final String GET = "GET";
	private static final String UTF_8 = "UTF-8";

	public WikiPage() {
		super();
	}
	
	public Map< String, List< String > > convertGETData( final String query ) {
		if ( query == null ) return null;
		final Map< String, List< String > > form_data = new TreeMap<>();
		for ( String binding : query.split( "&" ) ) {
			final int n = binding.indexOf( '=' );
			if ( n == -1 ) throw new Alert( "Malformed POST data" );
			final String key = binding.substring( 0, n );
			try {
				final String value = URLDecoder.decode( binding.substring( n + 1 ), UTF_8 );
				List< String > list = form_data.get( key );
				if ( list == null ) {
					list = new LinkedList< String >();
					form_data.put( key, list );
				}
				list.add( value );				
			} catch ( UnsupportedEncodingException e ) {
				throw new Alert( "Invalid encoding of POST data" );
			}
		}
		return form_data;
	}
	
	static Map< String, List< String > > convertPOSTData( final InputStream input ) {
		final Map< String, List< String > > form_data = new TreeMap<>();
		try ( Scanner scanner = new Scanner( input, UTF_8 ) ) {
			scanner.useDelimiter( "&" );
			while ( scanner.hasNext() ) {
				final String entry = scanner.next();
				final int n = entry.indexOf( '=' );
				if ( n == -1 ) throw new Alert( "Malformed POST data" );
				final String key = entry.substring( 0, n );
				try {
					final String value = URLDecoder.decode( entry.substring( n + 1 ), UTF_8 );
					List< String > list = form_data.get( key );
					if ( list == null ) {
						list = new LinkedList< String >();
						form_data.put( key, list );
					}
					list.add( value );
				} catch ( UnsupportedEncodingException e ) {
					throw new Alert( "Invalid encoding of POST data" );
				}
			}
		}
		return form_data;
	}
	


	public Map< String, List< String > > unpackRequestFields( HttpExchange http_exchange ) {
		switch ( http_exchange.getRequestMethod() ) {
		case GET:
			return convertGETData( http_exchange.getRequestURI().getRawQuery() );
		case POST:
			return convertPOSTData( http_exchange.getRequestBody() );
		default:
			return null;
		}		
	}

	public MinXML fetchTemplate( final String page_name ) {
		final Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( page_name ) );
		final MinXML template = new MinXSONParser( reader, 'E' ).read();
		return template;
	}

	protected MinXML newString( final String text ) {
		final MinXML minx = new FlexiMinXML( JSONKeywords.KEYS.CONSTANT );
		minx.putAttribute( JSONKeywords.KEYS.CONSTANT_TYPE, JSONKeywords.KEYS.STRING );
		minx.putAttribute( JSONKeywords.KEYS.CONSTANT_VALUE, text );
		return minx;
	}

	protected MinXML newBoolean( final boolean value ) {
		final MinXML minx = new FlexiMinXML( JSONKeywords.KEYS.CONSTANT );
		minx.putAttribute( JSONKeywords.KEYS.CONSTANT_TYPE, JSONKeywords.KEYS.BOOLEAN );
		minx.putAttribute( JSONKeywords.KEYS.CONSTANT_VALUE, JSONKeywords.KEYS.BOOLEAN_TRUE );
		return minx;
	}

}