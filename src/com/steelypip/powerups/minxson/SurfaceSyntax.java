package com.steelypip.powerups.minxson;

interface SurfaceSyntax {

	boolean isOpenArrayChar( char ch );

	String tagOfOpenArrayChar( char ch );

	char closingArrayChar( char ch );

	boolean isCloseArrayChar( char ch );
	
	public boolean isCloseParenthesis( char ch );

	boolean isTerminatorChar( char ch );


}