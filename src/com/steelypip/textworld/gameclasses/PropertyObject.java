package com.steelypip.textworld.gameclasses;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import com.steelypip.powerups.alert.Alert;

public class PropertyObject {
	
	static Logger logger = Logger.getLogger( PropertyObject.class.getName() );
	
	private <T> ActiveValue< T > newFieldActiveValue( final Class< ? > cuclass, final String field_name ) {
		if ( cuclass == null ) {
			throw new Alert( "Cannot find field" );
		} else {
			logger.info( "Looking in class " + cuclass.getName() + " for " + field_name );
		}
		Field f;
		try {
			f = cuclass.getDeclaredField( field_name );
			f.setAccessible( true );
			return new FieldProperty< T >( this, f );
		} catch ( NoSuchFieldException e ) {
			return this.newFieldActiveValue( cuclass.getSuperclass(), field_name );
		} catch ( SecurityException e ) {
			throw new Alert( "Cannot access field", e );
		}
	}
	
	public <T> ActiveValue< T > newFieldActiveValue( final String field_name ) {
		try {
			return this.newFieldActiveValue( this.getClass(), field_name );
		} catch ( Alert e ) {
			throw e.culprit( "Class name", this.getClass().getName() ).culprit( "Field", field_name );
		}
	}
	
	public Object get( final String key ) {
		final String name = canonise( key );
		try {
			Method m = this.getClass().getMethod( name );
			@SuppressWarnings("unchecked")
			ActiveValue< ? extends Object > active_value = (ActiveValue< ? extends Object > )m.invoke( this );
			return active_value.get();
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {

			throw new Alert( "Get - problem invoking property method", e );
		} catch ( NoSuchMethodException | SecurityException e ) {
			throw new Alert( "No such property" ).culprit( "Property name", key ).culprit( "Method name", name ).culprit( "Object", this );
		}
	}
	
	public void set( final String key, final Object value ) {
//		System.out.println( "Setting" );
		final String name = canonise( key );
		try {
			Method m = this.getClass().getMethod( name );
			@SuppressWarnings("unchecked")
			ActiveValue< ? extends Object > active_value = ( ActiveValue< ? extends Object > )m.invoke( this );
			active_value.setDefinition( value );
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			throw new Alert( "Set - problem invoking property method", e );
		} catch ( SecurityException e ) {
			throw new Alert( "Property access denied", e ).culprit( "Property name", key ).culprit( "Method name", name ).culprit( "Object", this );
		} catch ( NoSuchMethodException e ) {
			throw new Alert( "No such property", e ).culprit( "Property name", key ).culprit( "Method name", name ).culprit( "Object", this );
		}
	}
		
	public void define( final String key, final Object value ) {
		final String name = canonise( key );
		try {
			Method m = this.getClass().getMethod( name );
			@SuppressWarnings("unchecked")
			ActiveValue< Object > active_value = ( ActiveValue< Object > )m.invoke( this );
			active_value.setDefinition( value );
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
//			e.printStackTrace();
			throw new Alert( "Define - problem invoking property method", e );
		} catch ( SecurityException e ) {
			throw new Alert( "Property access denied", e ).culprit( "Property name", key ).culprit( "Method name", name ).culprit( "Object", this );
		} catch ( NoSuchMethodException e ) {
			throw new Alert( "No such property", e ).culprit( "Property name", key ).culprit( "Method name", name ).culprit( "Object", this );
		}
	}
		
	public static String canonise( final String key ) {
		final StringBuilder b = new StringBuilder();
		boolean caps_on = false;
		for ( int i = 0; i < key.length(); i++ ) {
			char ch = key.charAt( i );
			if ( ch == '-' ) {
				caps_on = true;
			} else {
				if ( caps_on ) {	
					ch = Character.toUpperCase( ch );
					caps_on = false;
				}
				b.append( ch );
			}
		}
		b.append( "AsActiveValue" );
		return b.toString();
	}


}
