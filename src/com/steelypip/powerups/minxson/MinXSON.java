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
		final MinXML minx = new FlexiMinXML( JSONKeywords.KEYS.TUPLE );
		for ( String key : keys ) {
			minx.add( newString( key ) );
		}
		return minx;
	}

	public static @NonNull MinXML newTuple( @NonNull MinXML ... data ) {
		final MinXML minx = new FlexiMinXML( JSONKeywords.KEYS.TUPLE );
		for ( @NonNull MinXML k : data ) {
			minx.add( k );
		}
		return minx;		
	}

	public static @NonNull MinXML newArray( @NonNull MinXML ... data ) {
		final MinXML minx = new FlexiMinXML( JSONKeywords.KEYS.ARRAY );
		for ( @NonNull MinXML k : data ) {
			minx.add( k );
		}
		return minx;			
	}

}
