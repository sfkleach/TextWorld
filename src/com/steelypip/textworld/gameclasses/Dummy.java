package com.steelypip.textworld.gameclasses;

import java.io.OutputStreamWriter;

import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.textworld.main.World;

public class Dummy extends Thing {
	
	MinXML configuration;

	public Dummy( World world ) {
		this.setWorld( world );
	}

	@Override
	public void init( final String uid, final MinXML configuration ) {
		super.init( uid, configuration );
		this.configuration = configuration;
	}
	
	@Override
	public void show() {
		this.configuration.prettyPrint(
			new OutputStreamWriter( System.out )
		);
	}
	
}