package com.steelypip.powerups.minxson;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.json.JSONKeywords;

/**
 * This class abstracts some of the surface syntax of MinXSON and in
 * doing so gives the parser some extensibility.
 *
 */
class StdSurfaceSyntax implements SurfaceSyntax {
	
	static class Bracket {
		
		char opener;
		char closer;
		String tag;
		
		public Bracket( char opener, char closer, String tag ) {
			this.opener = opener;
			this.closer = closer;
			this.tag = tag;
		}
		
	}
	
	private Bracket[] brackets;
	
	private StdSurfaceSyntax( Bracket[] brackets ) {
		this.brackets = brackets;
	}
	
	public static StdSurfaceSyntax newSurfaceSyntax( JSONKeywords json_keys, boolean tuple_extension ) {
		final Bracket square = new Bracket( '[', ']', json_keys.ARRAY ); 
		final Bracket parentheses = new Bracket( '(', ')', json_keys.TUPLE );
		if ( tuple_extension ) {
			final Bracket tortoise = new Bracket( '〔', '〕', json_keys.TUPLE ); 
			return new StdSurfaceSyntax( new Bracket[] { parentheses, square, tortoise } );
		} else {
			return new StdSurfaceSyntax( new Bracket[] { parentheses, square } );			
		}
	}
	

	@Override
	public boolean isOpenArrayChar( final char ch ) {
		for ( Bracket b : this.brackets ) {
			if ( b.opener == ch ) return true;
		}
		return false;
	}
	
	@Override
	public String tagOfOpenArrayChar( final char ch ) {
		for ( Bracket b : this.brackets ) {
			if ( b.opener == ch ) return b.tag;
		}
		throw Alert.internalError();
	}
	
	@Override
	public char closingArrayChar( final char ch ) {
		for ( Bracket b : this.brackets ) {
			if ( b.opener == ch ) return b.closer;
		}
		throw Alert.internalError();
	}
	
	@Override
	public boolean isCloseArrayChar( final char ch ) {
		for ( Bracket b : this.brackets ) {
			if ( b.closer == ch ) return true;
		}
		return false;
	}

	@Override
	public boolean isCloseObjectChar( final char ch ) {
		return ch == '}';
	}

	@Override
	public boolean isCloseParenthesis( final char ch ) {
		return ch == ')';
	}
	
	@Override
	public boolean isTerminatorChar( final char ch ) {
		return ch == ',' || ch == ';';
	}

}
