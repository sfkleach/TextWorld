package com.steelypip.textworld.gameclasses;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.json.JSONKeywords;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.textworld.main.World;

public abstract class GameObject extends PropertyObject {
	
	protected World world;

	public GameObject() {
	}

	public GameObject( World world ) {
		this.world = world;
	}

	public World getWorld() {
		if ( this.world == null ) {
			throw new Alert( "Trying to access the world of an uninitialised game object" ).culprit(  "Game object", this );
		}
		return this.world;
	}

	public void setWorld( World world ) {
		this.world = world;
	}


	private String unique_id;
	private ConstantActiveValue< String > name = new ConstantActiveValue< String >( this.getDefaultName() );
	private ConstantActiveValue< String > summary = new ConstantActiveValue< String >( this.getDefaultName() );

	public String getUid() {
		return this.unique_id;
	}

	private String getDefaultName() {
		final String name = this.getClass().getName();
		final int last_dot = name.lastIndexOf( '.' );
		return last_dot == -1 ? name : name.substring( last_dot + 1 ); 
	}
	
	public String getName() {
		return this.name.get();
	}

	public void setName( final String name ) {
		this.name.set( name );
	}
	
	public String getSummary() {
		return this.summary.get();
	}
	
	public void setSummary( final String name ) {
		this.summary.set( name );
	}
	
	public void initDefaultName( final String uid ) {
		this.unique_id = uid;
		this.name.set( uid );
		this.summary.set( uid );
	}
	
	public ActiveValue< ? extends Object > nameAsActiveValue() {
		return this.name;
	}
	
	public ActiveValue< ? extends Object > summaryAsActiveValue() {
		return this.summary;
	}
	
	public ActiveValue< ? extends Object > uidAsActiveValue() {
		return this.newFieldActiveValue( "uid" );
	}
	
	public void init( final String uid, final MinXML initial_configuration ) {
		this.initDefaultName( uid );
		for ( MinXML field_value : initial_configuration ) {
			if ( field_value.hasAttribute( JSONKeywords.KEYS.FIELD ) ) {
				try {
					this.define( field_value.getAttribute( JSONKeywords.KEYS.FIELD ), this.world.convertFromMinXML( field_value ) );
				} catch ( Alert alert ) {
					alert.report();
				}
			}
		}
	}
	
	public void show() {
		System.out.println( this.toString() );
	}

	public GameObject findByUID( final String string ) {
		return this.getWorld().findByUID( string );
	}


}
