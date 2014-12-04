package com.steelypip.textworld.gameclasses;

import java.lang.reflect.Field;

import com.steelypip.powerups.alert.Alert;

public class FieldActiveValue< T > implements ActiveValue< T > {
	
	Object subject;
	Field field;
	
	public FieldActiveValue( Object subject, Field field ) {
		this.subject = subject;
		this.field = field;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get() {
		try {
			return (T) this.field.get( this.subject );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			throw new Alert( "Trying to access protected field", e );
		}
	}

	@Override
	public void set( final T value ) {
		try {
			this.field.set( this.subject, value );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			throw new Alert( "Trying to access protected field", e );
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setDefinition( Object definition ) {
		this.set( (T)definition );
	}

}
