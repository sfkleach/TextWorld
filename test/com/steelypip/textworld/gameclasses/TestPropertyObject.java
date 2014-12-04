package com.steelypip.textworld.gameclasses;

import static org.junit.Assert.*;

import com.steelypip.powerups.minxml.FlexiMinXMLBuilder;
import com.steelypip.powerups.minxml.MinXMLBuilder;
import com.steelypip.textworld.gameclasses.ConstantActiveValue;

import org.junit.Test;

public class TestPropertyObject {

	static class Basic extends PropertyObject {
		ConstantActiveValue< String > x = new ConstantActiveValue<>( "foo" );
		public ActiveValue< String > fooAsActiveValue() { 
			return x;
		}
	}
	
	@Test
	public void testGet() {
		PropertyObject object = new Basic();
		assertEquals( "foo", object.get( "foo" ) );
	}

	@Test
	public void testGetSet() {
		PropertyObject object = new Basic();
		object.set( "foo", "beta" );
		assertEquals( "beta", object.get( "foo" ) );
	}

	@Test
	public void testDefine() {
		PropertyObject object = new Basic();
		MinXMLBuilder b = new FlexiMinXMLBuilder();
		b.startTagOpen( "constant" );
		b.put( "type", "string" );
		b.put( "value", "gamma" );
		b.endTag( null );
		object.define( "foo", b.build() );
		assertEquals( "gamma", object.get( "foo" ) );
	}

}
