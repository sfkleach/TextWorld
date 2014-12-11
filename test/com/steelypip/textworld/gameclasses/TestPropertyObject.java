package com.steelypip.textworld.gameclasses;

import static org.junit.Assert.*;

import com.steelypip.powerups.minxml.FlexiMinXMLBuilder;
import com.steelypip.powerups.minxml.MinXMLBuilder;
import com.steelypip.textworld.main.World;

import org.junit.Test;

public class TestPropertyObject {

	static class Basic extends PropertyObject {
		ActiveValue.Slot< String > x = new ActiveValue.Slot<>( "foo" );
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
		object.define( "foo", new World().evaluateMinXML( b.build() ) );
		assertEquals( "gamma", object.get( "foo" ) );
	}
	
	@Test
	public void testCanonise() {
		assertEquals( "lookAsActiveValue", PropertyObject.canonise( "look" ) );
		assertEquals( "spawnsAtAsActiveValue", PropertyObject.canonise( "spawns-at" ) );
	}

}
