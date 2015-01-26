package com.steelypip.textworld.main.web;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.minxml.FlexiMinXML;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.powerups.minxson.MinXSON;
import com.steelypip.textworld.gameclasses.GameObject;
import com.steelypip.textworld.main.Main;
import com.steelypip.textworld.main.World;
import com.steelypip.textworld.main.WorldFactory;

class HomePage extends StdTemplatePage {
	
	HomePage( final File game_folder, final String template_name ) {
		super( game_folder, template_name );
	}
	
	public @NonNull Map< String, @Nullable MinXML > environment( final Parameters parameters ) {
		final Map< String, @Nullable MinXML > environment = new TreeMap<>();
		environment.put( "version", MinXSON.newString( Main.getVersion() ) );
		final MinXML pages = new FlexiMinXML( "pages" );
		environment.put( "list", pages );			
		for ( File file_name : this.game_folder.listFiles( WorldFactory.FILTER ) ) {
			final String key = WorldFactory.getIdentifier( file_name );
			try {
				pages.add( MinXSON.newTuple( "object?file=" + URLEncoder.encode( file_name.getName(), "UTF-8" ), URLEncoder.encode( key, "UTF-8" ) ) );
			} catch ( UnsupportedEncodingException e ) {
				throw Alert.unreachable();
			}
		}
		return environment;		
	}
}