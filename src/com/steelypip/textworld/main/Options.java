package com.steelypip.textworld.main;

public interface Options {
	
	boolean isDebugging();
	
	void setDebugging( boolean d );
	
	boolean isEditing();
	
	ReadLine getInStream();

}
