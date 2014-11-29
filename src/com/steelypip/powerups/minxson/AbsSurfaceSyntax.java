package com.steelypip.powerups.minxson;

interface AbsSurfaceSyntax {

	boolean isOpenArrayChar( char ch );

	String tagOfOpenArrayChar( char ch );

	char closingArrayChar( char ch );

	boolean isCloseArrayChar( char ch );

}