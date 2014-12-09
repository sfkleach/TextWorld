package com.steelypip.textworld.main;

import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.json.JSONKeywords;
import com.steelypip.powerups.minxml.MinXML;
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
		PrintWriter pw = this.getAvatar().getPrintWriter();
		for ( Entry< String, GameObject > e : this.name_space.entrySet() ) {
			pw.print( "Variable " + e.getKey() + ": " );
			pw.print(  e.getValue().getClass().getName() );
			pw.println();
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

	public GameObject findByUID( final String uid ) {
//		for ( String key : this.name_space.keySet() ) {
//			System.out.println( "key = " + key );
//		}
		return this.name_space.get( uid );
	}	
	
	public Object convertFromMinXML( final MinXML field_value ) {
//		field_value.prettyPrint( new OutputStreamWriter( System.out ) );
		if ( field_value.hasName( JSONKeywords.KEYS.CONSTANT ) ) {
			if ( field_value.hasAttribute( JSONKeywords.KEYS.CONSTANT_TYPE, JSONKeywords.KEYS.STRING ) ) {
				return field_value.getAttribute( JSONKeywords.KEYS.CONSTANT_VALUE );
			} else {
				throw Alert.unimplemented( "Non-string field" );
 			}
		} else if ( 
			field_value.hasName( JSONKeywords.KEYS.ID ) && 
			field_value.hasAttribute( JSONKeywords.KEYS.ID_NAME )
		) {
			final String variable = field_value.getAttribute( JSONKeywords.KEYS.ID_NAME );
			final GameObject game_object = this.name_space.get( variable );
			if ( game_object != null ) {
				return game_object;
			} else{
				throw new Alert( "Reference to undefined variable" ).culprit( "Variable", variable );
			}
		} else {
			throw Alert.unimplemented( "Field not a constant or identifer" );
		}
	}
	
}
