package com.steelypip.powerups.minxson;

interface SurfaceSyntax {

	boolean isOpenArrayChar( char ch );

	String tagOfOpenArrayChar( char ch );

	char closingArrayChar( char ch );

	boolean isCloseArrayChar( char ch );
	
	boolean isCloseObjectChar( char ch );
	
	default boolean isCloseChar( final char ch ) {
		return this.isCloseArrayChar( ch ) || isCloseObjectChar( ch );
	}
	
	public boolean isCloseParenthesis( char ch );

	boolean isTerminatorChar( char ch );


}