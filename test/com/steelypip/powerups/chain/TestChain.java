package com.steelypip.powerups.chain;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestChain {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testNewChainFromArray() {
		Chain< String > chain = Chain.newChain( new String[] { "foo", "bar" } );
		assertFalse( chain.isEmpty() );
		assertTrue( chain.isntEmpty() );
		assertEquals( "foo", chain.getHead() );
		assertEquals( "bar", chain.getTail().getHead() );
		assertEquals( 2, chain.size() );
		assertTrue( chain.hasSize( 2 ) );
	}

	@Test
	public void testNewChainFromIterable() {
		List< String > list = new ArrayList<>();
		list.add( "foo" );
		list.add( "bar" );
		Chain< String > chain = Chain.newChain( list );
		assertFalse( chain.isEmpty() );
		assertTrue( chain.isntEmpty() );
		assertEquals( "foo", chain.getHead() );
		assertEquals( "bar", chain.getTail().getHead() );
		assertEquals( 2, chain.size() );
		assertTrue( chain.hasSize( 2 ) );
	}

	@Test void testGet() {
		List< String > list = new ArrayList<>();
		list.add( "foo" );
		list.add( "bar" );
		Chain< String > chain = Chain.newChain( list );
		assertEquals( "foo", chain.get( 0 ) );
		assertEquals( "bar", chain.get( 1 ) );
	}
	
}
