package com.steelypip.textworld.main;

public abstract class Thunk implements AsString {
	
	public abstract Object evaluate();

	@Override
	public String asString() {
		final Object r = this.evaluate();
		return r != null ? r.toString() : null;
	}
		
	

}
