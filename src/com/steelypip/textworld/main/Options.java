package com.steelypip.textworld.main;

import java.io.File;

public interface Options {
	
	boolean isDebugging();
	
	void setDebugging( boolean d );
	
	boolean isEditing();
	
	ReadLine getInStream();

	File getGameFolder();

}
