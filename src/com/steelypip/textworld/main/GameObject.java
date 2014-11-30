package com.steelypip.textworld.main;

import com.steelypip.powerups.minxml.MinXML;

public interface GameObject {
	
	void init( MinXML initial_configuration );
	
	void show();
	
}
