package com.steelypip.textworld.gameclasses;

public class ConstantActiveValue< T > implements ActiveValue< T > {
	
	private T value;
	
	public ConstantActiveValue( T value ) {
		this.value = value;
	}

	@Override
	public T get() {
		return this.value;
	}

	@Override
	public void set( final T new_value ) {
//		System.err.println( "Was " + this.value );
		this.value = new_value;
//		System.err.println( "Is " + this.value );
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setDefinition( Object definition ) {
//		System.out.println( "Defining new value: " + definition );
		this.value = (T)definition;
	}

	
	
}
