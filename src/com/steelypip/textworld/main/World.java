package com.steelypip.textworld.main;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.textworld.gameclasses.At;
import com.steelypip.textworld.gameclasses.GameObject;
import com.steelypip.textworld.gameclasses.To;
import com.steelypip.textworld.gameclasses.loadable.Avatar;
import com.steelypip.textworld.gameclasses.loadable.Limbo;

public class World {
	
	public static final String LIMBO = "Limbo";
	public static final String AVATAR = "Avatar";
	public static final String AT = "At";
	public static final String TO = "To";
	
	private Map< String, GameObject > name_space = new TreeMap<>();
	private Avatar avatar = null;
	private Limbo limbo = null;
	private At at = null;
	private To to = null;
	private boolean is_active = true;
	
	private String name = "the game";

	
	public Map< String, GameObject > getNameSpace() {
		return this.name_space;
	}
	
	<T extends GameObject> T getField( final String field, final Class< T > required ) {
		final Object value = this.name_space.get( field );
		if ( value == null ) {
			throw new Alert( "Field is not assigned" ).culprit( "Field", field );
		}
		if ( required.isInstance( value ) ) {
			return required.cast( value );
		} else {
			throw new Alert( "Field is misconfigured" ).culprit( "Field", field ).culprit( "Actual type", value.getClass().getName() );
		}
	}
	
	public void show() {
		for ( Entry< String, GameObject > e : this.name_space.entrySet() ) {
			System.err.print( "Variable " + e.getKey() + ": " );
			System.err.print(  e.getValue().getClass().getName() );
			System.err.println();
		}
	}

	@SuppressWarnings("null")
	public @NonNull Avatar getAvatar() {
		if ( this.avatar != null ) { 
			return this.avatar;
		} else {
			return this.avatar = this.getField( AVATAR, Avatar.class );
		}
	}

	@SuppressWarnings("null")
	public @NonNull Limbo getLimbo() {
		if ( this.limbo != null ) { 
			return this.limbo;
		} else {
			return this.limbo = this.getField( LIMBO, Limbo.class );
		}
	}
	
	@SuppressWarnings("null")
	public @NonNull At getAt() {
		if ( this.at != null ) {
			return this.at;
		} else {
			return this.at = this.getField( AT, At.class );
		}
	}

	@SuppressWarnings("null")
	public @NonNull To getTo() {
		if ( this.to != null ) {
			return this.to;
		} else {
			return this.to = this.getField( TO, To.class );
		}
	}

	public boolean isActive() {
		return this.is_active;
	}

	public void setIsActive( final boolean is_active ) {
		this.is_active = is_active;
	}

	public String getName() {
		return this.name;
	}

	public void setName( String name ) {
		this.name = name;
	}
	
	
	
}
