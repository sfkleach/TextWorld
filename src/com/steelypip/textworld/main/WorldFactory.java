package com.steelypip.textworld.main;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.minxconf.MinXConfParser;
import com.steelypip.powerups.minxml.FlexiMinXML;
import com.steelypip.powerups.minxml.MinXML;

public class WorldFactory {
	
	final Map< String, GameObject > world = new TreeMap<>(); 
	final Map< String, MinXML > var_config = new TreeMap<>(); 

	static FileFilter filter = ( File pathname ) -> pathname.exists() && ( pathname.isDirectory() || pathname.getName().matches( ".*=.*\\..*" ) );
	
	public WorldFactory() {
	}
	
	public @NonNull World newWorld() {
		return new World( this.world );
	}
	
	void load( File top_level_folder ) {
		for ( File f : top_level_folder.listFiles( filter ) ) {
			final @NonNull MinXML game_object_config = readMinXML( f );
			final String var = getIdentifier( f );
			this.world.put( var, GameObjectFactory.createBlank( game_object_config.getName() ) );
			var_config.put( var, game_object_config );
		}
		for ( Map.Entry< String, MinXML > e : var_config.entrySet() ) {
			this.world.get( e.getKey() ).init( e.getValue() );
		}
	}
	
	private static @NonNull MinXML readMinXML( File f ) {
		return new FileLoader().load( f );
	}

	static String getIdentifier( File f ) {
		final String name = f.getName();
		int end_of_id = name.indexOf( '=' );
		if ( end_of_id == -1 ) {
			end_of_id = name.indexOf( '.' );
			if ( end_of_id == -1 ) {
				return name;
			}
		}
		return name.substring( 0, end_of_id );
	}
	

}
