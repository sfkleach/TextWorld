package com.steelypip.textworld.main;

import java.io.File;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.json.JSONKeywords;
import com.steelypip.powerups.minxml.FlexiMinXML;
import com.steelypip.powerups.minxml.MinXML;

public class ObjectFolderLoader {
	
	final @NonNull MinXML object;  
	
	public ObjectFolderLoader( final String name ) {
		this.object = new FlexiMinXML( "object" );
	}
	
	public ObjectFolderLoader() {
		this( "object" );
	}
	
	/**
	 * Note that in this case we strip after the first dot. There is
	 * a subtle case where there are multiple dots in the name e.g. foo.tar.gz.
	 * @param f the file name to convert into a field name
	 * @return a field name
	 */
	static String asFieldName( final File f ) {
		final String name = f.getName();
		final int first_dot_position = name.indexOf( '.' );
		final int first_equals_position = name.indexOf( '=' );
		final int position = Math.min( first_dot_position, first_equals_position );
		if ( position == -1 ) return name;
		return name.substring( 0, position );
	}

	public @NonNull MinXML load( final File file ) {
		for ( File subfile : file.listFiles() ) {
			final @NonNull MinXML subdata = new FileLoader().load( subfile );
			subdata.putAttribute( JSONKeywords.KEYS.FIELD, asFieldName( subfile ) );
			this.object.add( subdata );
		}
		return this.object;
	}
	
}
