package com.steelypip.textworld.gameclasses;


public interface ActiveValue< T > {

	public T get();
	
	public void set( T t );
	
	@SuppressWarnings("unchecked")
	default void dynSet( final Object t ) {
		this.set( (T)t );
	}
	
	public void setDefinition( Object definition );
	
}
