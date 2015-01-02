package com.steelypip.textworld.main.web;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.steelypip.textworld.main.World;

public class TestWebGameEngine {
	
	WebGameEngine wge;

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void editInitialURL() {
		wge = new WebGameEngine( new World(), null );
		assertTrue( wge.initialURL().matches( "http://localhost:[0-9]+/edit/home" ) );
	}

	@Test
	public void playInitialURL() {
		wge = new WebGameEngine( new World(), null );
		assertTrue( wge.initialURL().matches( "http://localhost:[0-9]+/textworld" ) );
	}

}
