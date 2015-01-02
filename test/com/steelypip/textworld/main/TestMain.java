package com.steelypip.textworld.main;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import com.steelypip.powerups.minxml.FlexiMinXML;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.powerups.minxson.MinXSONParser;
import com.steelypip.powerups.minxson.templates.XHTMLRenderTemplate;

public class TestMain {

	@Before
	public void setUp() throws Exception {
	}

	private MinXML newString( String s ) {
		final MinXML x = new FlexiMinXML( "constant" );
		x.putAttribute( "type", "string" );
		x.putAttribute( "value", s );
		return x;
	}
	
	private MinXML newBoolean( final boolean s ) {
		final MinXML x = new FlexiMinXML( "constant" );
		x.putAttribute( "type", "boolean" );
		x.putAttribute( "value", s ? "true" : "false" );
		return x;
	}
	
	@Test
	public void testHome() {
		final Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( "home.xson" ) );
		MinXSONParser p = new MinXSONParser( reader, 'F', 'I' );
		Map< String, MinXML > map = new TreeMap<>();
		map.put( "list", new MinXSONParser( new StringReader( "[('a','b')]" ), 'U' ).read() );
		new XHTMLRenderTemplate( new PrintWriter( System.out, true ), map ).render( p.read() ); //eval( p.read() ).prettyPrint();
//		p.read().prettyPrint();
	}

	@Test
	public void testPage() {
		final Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( "page.xson" ) );
		MinXSONParser p = new MinXSONParser( reader, 'E', 'F', 'I' );
		Map< String, MinXML > map = new TreeMap<>();
		map.put( "image", newString( "/foo.gif" ) );
		map.put( "active", newBoolean( true ) );
		new XHTMLRenderTemplate( new PrintWriter( System.out, true ), map ).eval( p.read() ).prettyPrint();
//		new XHTMLRenderTemplate( new PrintWriter( System.out, true ), map ).render( p.read() );
	}

	@Test
	public void testIf() {
		final Reader reader = new StringReader( "<div><if test=\"ok\" name=\"active\"{ then: <img width=\"75%\" height=\"auto\" { \"src\": image }/> }/></div>" );
		MinXSONParser p = new MinXSONParser( reader, 'E', 'F', 'I' );
		Map< String, MinXML > map = new TreeMap<>();
		map.put( "image", newString( "/foo.gif" ) );
		map.put( "active", newBoolean( true ) );
		new XHTMLRenderTemplate( new PrintWriter( System.out, true ), map ).eval( p.read() ).prettyPrint();
	}

}
