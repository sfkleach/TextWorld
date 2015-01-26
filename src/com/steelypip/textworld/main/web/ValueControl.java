package com.steelypip.textworld.main.web;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.powerups.minxson.MinXSON;

public class ValueControl {

	public static @NonNull ValueControl newValueControl( @Nullable String field ) {
		return new ValueControl();
	}

//	static final String PROPERTY = "property-";

	public @NonNull MinXML convert( @NonNull MinXML child ) {
		return MinXSON.newString( "" + child );
	}

	static @NonNull MinXML propertyName( final String field ) {
		return MinXSON.newString( field );
	}

}
