package com.steelypip.textworld.main.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.json.JSONKeywords;
import com.steelypip.powerups.minxconf.MinXConfParser;
import com.steelypip.powerups.minxml.FlexiMinXML;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.powerups.minxml.MinXMLParser;
import com.steelypip.powerups.minxson.MinXSON;
import com.steelypip.textworld.gameclasses.GameObject;
import com.steelypip.textworld.main.Main;
import com.steelypip.textworld.main.World;

class ObjectPage extends StdTemplatePage {
	
	public static final String FILE = "file";

	ObjectPage( final File game_folder, final String template_name ) {
		super( game_folder, template_name );
	}
	
	private static boolean isInFileHierarchy( File root_folder, File subfile ) {
		try {
			final String root_path = root_folder.getCanonicalPath();
			final String subfile_path = subfile.getCanonicalPath();
			return subfile_path.startsWith( root_path );
		} catch ( IOException e ) {
			return false;
		}
	}
	
	private File locateBackingStore( final String file_name ) {
		final File file = new File( game_folder, file_name );
		if ( ! isInFileHierarchy( game_folder, file ) ) {
			//	Looks like we are being hacked.
			throw new Alert( "Trying to access files outside of file hierarchy" ).culprit( "File name", file_name );
		} 
		return file;
	}
	
	public @NonNull Map< String, @Nullable MinXML > environment( final Parameters parameters ) {
		try {
			final File backing_store = locateBackingStore( parameters.fetch( FILE ) );
			final Map< String, @Nullable MinXML > env = new TreeMap<>();
			env.put( "version", MinXSON.newString( Main.getVersion() ) );
			env.put( "file", MinXSON.newString( parameters.fetch( FILE ) ) );
			env.put( "properties", propertyList( backing_store ) );
			return env;
		} catch ( Exception e ) {
			throw new Alert( e );
		}
	}
	
	private @NonNull MinXML propertyList( final File backing_store ) {
		final @NonNull MinXML list = MinXSON.newArray();
		try{
			final MinXML minx = new MinXConfParser( backing_store, 'E', 'F', 'U' ).read();
			Objects.requireNonNull( minx );
			for ( @NonNull MinXML child : minx ) {
				final @Nullable String field = child.getAttribute( JSONKeywords.KEYS.FIELD  );
				if ( field != null ) {
					ValueControl control = ValueControl.newValueControl( field );
					list.add( MinXSON.newTuple( ValueControl.propertyName( field ), control.convert( child ) ) ); 
				}
			}
			return list;
		} catch ( Throwable e ) {
			throw new Alert( e );
		}		
	}

}