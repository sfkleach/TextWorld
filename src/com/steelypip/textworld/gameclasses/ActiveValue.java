package com.steelypip.textworld.gameclasses;

import java.io.OutputStreamWriter;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.json.JSONKeywords;
import com.steelypip.powerups.minxml.MinXML;

public interface ActiveValue< T > {

	public T get();
	
	public void set( T t );
	
	@SuppressWarnings("unchecked")
	default void dynSet( Object t ) {
//		System.err.println( "Dynset with " + t );
		this.set( (T)t );
	}
	
	public void setDefinition( Object definition );
	
	default void define( MinXML config ) {
		this.setDefinition( convertFromMinXML( config ) );
	}

	static Object convertFromMinXML( final MinXML field_value ) {
//		field_value.prettyPrint( new OutputStreamWriter( System.out ) );
		if ( field_value.hasName( JSONKeywords.KEYS.CONSTANT ) ) {
			if ( field_value.hasAttribute( JSONKeywords.KEYS.CONSTANT_TYPE, JSONKeywords.KEYS.STRING ) ) {
				return field_value.getAttribute( JSONKeywords.KEYS.CONSTANT_VALUE );
			} else {
				throw Alert.unimplemented( "Non-string field" );
 			}
		} else {
			throw Alert.unimplemented( "Field not a constant" );
		}
	}
	
}
