package com.steelypip.textworld.gameclasses;

import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.json.JSONKeywords;
import com.steelypip.powerups.minxml.MinXML;

public class PropertyObject {
	
	public ActiveValue< ? extends Object > newFieldActiveValue( final String field_name ) {
		Field f;
		try {
			f = this.getClass().getDeclaredField( field_name );
		} catch ( NoSuchFieldException | SecurityException e ) {
			throw new Alert( "Cannot access field", e );
		}
		f.setAccessible( true );
		return new FieldActiveValue< String >( this, f );
	}
	
	public Object get( final String key ) {
		final String name = canonise( key );
		try {
			Method m = this.getClass().getMethod( name );
			@SuppressWarnings("unchecked")
			ActiveValue< ? extends Object > active_value = (ActiveValue< ? extends Object > )m.invoke( this );
			return active_value.get();
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			throw new Alert( "Problem invoking property method", e );
		} catch ( NoSuchMethodException | SecurityException e ) {
			throw new Alert( "No such property" ).culprit( "Property name", key ).culprit( "Method name", name ).culprit( "Object", this );
		}
	}
	
	public void set( final String key, final Object value ) {
//		System.out.println( "Setting" );
		final String name = canonise( key );
		try {
			Method m = this.getClass().getMethod( name );
			ActiveValue< ? > active_value = ( ActiveValue< ? > )m.invoke( this );
			active_value.dynSet( value );
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			throw new Alert( "Problem invoking property method", e );
		} catch ( SecurityException e ) {
			throw new Alert( "Property access denied", e ).culprit( "Property name", key ).culprit( "Method name", name ).culprit( "Object", this );
		} catch ( NoSuchMethodException e ) {
			throw new Alert( "No such property", e ).culprit( "Property name", key ).culprit( "Method name", name ).culprit( "Object", this );
		}
	}
		
	public void define( final String key, final MinXML value ) {
//		System.out.println( "Defining " + key  );
		final String name = canonise( key );
		try {
			Method m = this.getClass().getMethod( name );
			@SuppressWarnings("unchecked")
			ActiveValue< Object > active_value = ( ActiveValue< Object > )m.invoke( this );
			active_value.define( value );
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			throw new Alert( "Problem invoking property method", e );
		} catch ( SecurityException e ) {
			throw new Alert( "Property access denied", e ).culprit( "Property name", key ).culprit( "Method name", name ).culprit( "Object", this );
		} catch ( NoSuchMethodException e ) {
			throw new Alert( "No such property", e ).culprit( "Property name", key ).culprit( "Method name", name ).culprit( "Object", this );
		}
	}
		
	private static String canonise( final String key ) {
		return key + "AsActiveValue";
	}


}
