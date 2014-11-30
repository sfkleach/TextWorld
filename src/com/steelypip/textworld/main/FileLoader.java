package com.steelypip.textworld.main;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.minxconf.MinXConfParser;
import com.steelypip.powerups.minxml.MinXML;

public class FileLoader {
	
	static String extension( final File f ) {
		final String name = f.getName();
		final int start_of_extension = name.lastIndexOf( '.' );
		if ( start_of_extension == -1 ) {
			return "";
		} else {
			return name.substring( start_of_extension );
		}
	}

	public @NonNull MinXML load( File f ) {
		if ( f.isFile() ) {
			switch ( extension( f ) ) {
			case ".mxc":
			case ".minxconf":
				//	It would be a good idea to move this into MinXConf.
				try {	
					return new MinXConfParser( f, 'E', 'F', 'U' ).read();
				} catch ( FileNotFoundException e ) {
					//	We filter out non-existent files, so this is purely defensive.
					throw new Alert( "Cannot find file", e ).culprit( "File", f );
				}
			default:
				throw new Alert( "No processing defined for file" ).culprit( "File", f );
			}
		} else if ( f.isDirectory() ) {
			switch ( extension( f ) ) {
			case ".object":
				return new ObjectFolderLoader().load( f );
			default:
				throw new Alert( "No processing defined for this folder" ).culprit( "Folder", f );
			}
		} else {
			throw new Alert( "No processing defined for the file defined by this pathname" ).culprit( "Pathname", f );
		}
	}
	
}
