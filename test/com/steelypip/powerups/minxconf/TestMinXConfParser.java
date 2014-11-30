package com.steelypip.powerups.minxconf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import org.junit.Test;

import com.steelypip.powerups.minxml.MinXML;

public class TestMinXConfParser {

	@Test
	public void testReadEmpty() {
		MinXML m = new MinXConfParser( new StringReader( "" ) ).read();
		assertEquals( "<object/>", m.toString() );
	}
	
	@Test
	public void testReadBindings1() {
		MinXML m = new MinXConfParser( new StringReader( "foo:1, bar:2" ) ).read();
		assertEquals( "<object><constant field=\"foo\" type=\"integer\" value=\"1\"/><constant field=\"bar\" type=\"integer\" value=\"2\"/></object>", m.toString() );
	}
	
	@Test
	public void testReadBindings2() {
		MinXML m = new MinXConfParser( new StringReader( "foo:1\nbar:2" ) ).read();
		assertEquals( "<object><constant field=\"foo\" type=\"integer\" value=\"1\"/><constant field=\"bar\" type=\"integer\" value=\"2\"/></object>", m.toString() );
	}
	
	@Test
	public void testPlusEquals() {
		final StringBuilder input = new StringBuilder();
		input.append( "foo = <foo/>\n" );
		input.append( "bar += <bar1/>\n" );
		input.append( "gort = <gort/>\n" );
		input.append( "bar += <bar2/>\n" );
		MinXML m = new MinXConfParser( new StringReader( input.toString() ) ).read();
		assertEquals( "<foo field=\"foo\"/>", m.get( 0 ).toString() );
		assertEquals( "array", m.get( 1 ).getName() );
		assertTrue( m.get( 1 ).hasAttribute( "field", "bar" ) );
		assertEquals( "<bar1/>", m.get( 1 ).get( 0 ).toString() );
		assertEquals( "<bar2/>", m.get( 1 ).get( 1 ).toString() );
		assertEquals( "<gort field=\"gort\"/>", m.get( 2 ).toString() );
	}
	
	/* Added purely to check out the tutorial text.
	@Test
	public void fake() throws FileNotFoundException {
		MinXML m = new MinXConfParser( new FileReader( new File( "data.minxconf" ) ) ).read();
		m.prettyPrint( new PrintWriter( System.out ) );
		//assertEquals( "<object><constant field=\"foo\" type=\"integer\" value=\"1\"/><constant field=\"bar\" type=\"integer\" value=\"2\"/></object>", m.toString() );
	}*/
	
}
