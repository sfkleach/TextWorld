package com.steelypip.textworld.main.web;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.Before;
import org.junit.Test;

import com.steelypip.powerups.common.EmptyMap;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.powerups.minxson.MinXSON;
import com.steelypip.powerups.minxson.templates.XHTMLRenderTemplate;

public class TestObjectPage {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		ObjectPage page = new ObjectPage( new File( "Example" ), "object.xson" );
		Parameters p = new Parameters();
		p.addParameter( "file", "Avatar=Avatar.minxconf" );
		@NonNull Map< String, @Nullable MinXML > e = page.environment( p );
//		e.put( "file", MinXSON.newString( "foo" ) );
		new XHTMLRenderTemplate( null, e ).eval( page.getTemplate() ).prettyPrint();
	}

}
