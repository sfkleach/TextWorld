package com.steelypip.powerups.minxson.templates;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Before;
import org.junit.Test;

import com.steelypip.powerups.io.StringPrintWriter;
import com.steelypip.powerups.minxml.FlexiMinXML;
import com.steelypip.powerups.minxml.FlexiMinXMLBuilder;
import com.steelypip.powerups.minxson.templates.XHTMLRenderTemplate;

public class TestXHTMLRenderTemplate {
	
	StringPrintWriter print_writer = new StringPrintWriter();
	XHTMLRenderTemplate renderer = new XHTMLRenderTemplate( print_writer );

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		FlexiMinXMLBuilder b = new FlexiMinXMLBuilder();
		b.startTagOpen( "p" );
		b.startTagOpen( "constant" );
		b.put( "value", "Hello, world!" );
		b.endTag( null );
		b.endTag( null );
		renderer.render( b.build() );
		assertEquals( "<p>Hello, world!</p>", print_writer.toString() );
	}


}
