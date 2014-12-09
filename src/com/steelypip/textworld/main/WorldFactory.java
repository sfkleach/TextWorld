package com.steelypip.textworld.main;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.minxml.FlexiMinXML;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.textworld.gameclasses.At;
import com.steelypip.textworld.gameclasses.GameObject;
import com.steelypip.textworld.gameclasses.To;
import com.steelypip.textworld.gameclasses.loadable.Avatar;
import com.steelypip.textworld.gameclasses.loadable.Limbo;

public class WorldFactory {
	
	static class Initialisation {
		GameObject game_object;
		String uid;
		MinXML minx;
		File file_name;

		public Initialisation( GameObject game_object, String uid, MinXML minx, File file_name ) {
			if ( game_object == null ) {
				throw new Alert( "Setting up null game object" ).culprit( "UID", uid );
			}
			this.game_object = game_object;
			this.uid = uid;
			this.minx = minx;
			this.file_name = file_name;
		}

		public void init() {
			System.out.println( "Initialisation for " + file_name );
			game_object.init( this.uid,  minx );
		}
	}
	
	final private @NonNull World the_world = new World();
	final private Map< String, GameObject > name_space = the_world.getNameSpace();

	final private static FileFilter filter = ( File pathname ) -> pathname.exists() && ( pathname.isDirectory() || pathname.getName().matches( "[^=.]*=[^=.]*\\.[^=.]*$" ) ); 
	
	
	public void addBuiltIns() {
		Limbo the_default_location = new Limbo();
		this.put( World.AT, new At( the_default_location ) );
		this.put( World.TO, new To() );
		this.put( World.LIMBO, the_default_location );
		this.put( World.AVATAR, new Avatar() );
	}
	
	public void put( String var, GameObject game_object ) {
		game_object.setWorld( this.the_world );
		this.name_space.put( var, game_object );
		game_object.init( var, new FlexiMinXML( "object" ) );
	}
	
	public void load( File top_level_folder ) {
		this.the_world.setName( top_level_folder.getName() );
		if ( top_level_folder.isDirectory() ) {
			final List< Initialisation > initialisations = new ArrayList<>();
			for ( File f : top_level_folder.listFiles( filter ) ) {
				if ( f == null ) throw Alert.internalError();
				final @NonNull String var = getIdentifier( f );
				GameObject game_object = this.name_space.get( var );
				if ( game_object == null ) {
					final @NonNull String type = getType( f );
					game_object = GameObjectFactory.createBlank( type, this.the_world );
					this.name_space.put( var, game_object );
				}
				final @NonNull MinXML game_object_config = readMinXML( f );
				initialisations.add( new Initialisation( game_object, var, game_object_config, f ) );
			}
			for ( Initialisation i : initialisations ) {
				i.init();
			}
		} else {
			throw new Alert( "Directory needed" ).hint( "Might not exist" ).culprit( "Folder name", top_level_folder );
		}
	}
	
	private static @NonNull MinXML readMinXML( File f ) {
		return new FileLoader().load( f );
	}

	@SuppressWarnings("null")
	public static @NonNull String getIdentifier( final File f ) {
		final @NonNull String name = f.getName();
		int end_of_id = name.indexOf( '=' );
		if ( end_of_id == -1 ) {
			end_of_id = name.lastIndexOf( '.' );
			if ( end_of_id == -1 ) {
				return name;
			}
		}
		return name.substring( 0, end_of_id );
	}
	
	@SuppressWarnings("null")
	public static @NonNull String getType( final @NonNull File f ) {
		final String name = f.getName();
		int start_of_type = name.indexOf( '=' );
		if ( start_of_type == -1 ) {
			start_of_type = 0;
		} else {
			start_of_type += 1;
		}
		int end_of_type = name.lastIndexOf( '.' );
		if ( end_of_type == -1 ) {
			end_of_type = name.length();
		}
		if ( end_of_type < start_of_type ) {
			start_of_type = 0;
		}
		return name.substring( start_of_type, end_of_type );
	}


	public @NonNull World newWorld() {
		return this.the_world;
	}



}
