package com.steelypip.textworld.gameclasses;

import java.io.OutputStreamWriter;

import com.steelypip.powerups.minxml.MinXML;

public class Dummy extends Thing {
	
	MinXML configuration;

	@Override
	public void init( MinXML configuration ) {
		this.configuration = configuration;
	}
	
	@Override
	public void show() {
		this.configuration.prettyPrint(
			new OutputStreamWriter( System.out )
		);
	}
	
}