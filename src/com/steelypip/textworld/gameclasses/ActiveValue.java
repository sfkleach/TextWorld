package com.steelypip.textworld.gameclasses;

import com.steelypip.textworld.main.AsString;


public interface ActiveValue< T > {
	
	static class Slot< T > implements ActiveValue< T > {
		
		protected T value;
		
		public Slot( final T value ) {
			this.value = value;
		}

		@Override
		public T get() {
			return value;
		}

		@Override
		public void set( T t ) {
			this.value = t;
		}
		
	}
	
	public T get();
	
	public void set( T t );
	
	@SuppressWarnings("unchecked")
	default void setDefinition( Object t ) {
		this.set( (T)t );
	}

	default public String getAsString( final String default_value ) {
		final T value = this.get();
		if ( value == null ) return default_value;
		if ( value instanceof String ) return (String)value;
		if ( value instanceof AsString ) return ((AsString)value).asString();
		return value.toString();
	}
	
}
