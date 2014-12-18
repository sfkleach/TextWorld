package com.steelypip.textworld.gameclasses;

import com.steelypip.textworld.main.Reportable;


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
	
}
