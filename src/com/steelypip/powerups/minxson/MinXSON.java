package com.steelypip.powerups.minxson;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.json.JSONKeywords;
import com.steelypip.powerups.minxml.FlexiMinXML;
import com.steelypip.powerups.minxml.MinXML;

public interface MinXSON {

	static public MinXML newBoolean( final boolean value ) {
		final MinXML minx = new FlexiMinXML( JSONKeywords.KEYS.CONSTANT );
		minx.putAttribute( JSONKeywords.KEYS.CONSTANT_TYPE, JSONKeywords.KEYS.BOOLEAN );
		minx.putAttribute( JSONKeywords.KEYS.CONSTANT_VALUE, JSONKeywords.KEYS.BOOLEAN_TRUE );
		return minx;
	}

	static public @NonNull MinXML newString( final String text ) {
		final MinXML minx = new FlexiMinXML( JSONKeywords.KEYS.CONSTANT );
		minx.putAttribute( JSONKeywords.KEYS.CONSTANT_TYPE, JSONKeywords.KEYS.STRING );
		minx.putAttribute( JSONKeywords.KEYS.CONSTANT_VALUE, text );
		return minx;
	}

	public static @NonNull MinXML newTuple( String... keys ) {
		final MinXML minx = new FlexiMinXML( "tuple" );
		for ( String key : keys ) {
			minx.add( newString( key ) );
		}
		return minx;
	}

}
