package com.steelypip.textworld.main;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.textworld.gameclasses.Dummy;
import com.steelypip.textworld.gameclasses.GameObject;


public class GameObjectFactory {
	
	public static GameObject createBlank( final @NonNull String category, final @NonNull World world ) {
		final String className = "com.steelypip.textworld.gameclasses.loadable." + category;
		try {
			Class< ? > gClass = Class.forName( className );
			try {
				Object object = gClass.newInstance();
				try {
					final GameObject game_object = (GameObject)object;
					game_object.setWorld( world );
					return game_object;
				} catch ( ClassCastException e ) {
					throw new Alert( "GameObject expected" ).hint( "This category is not a GameObject" ).culprit( "Category", category ).culprit( "Actual class", object.getClass().getName() );
				}
			} catch ( InstantiationException e ) {
				throw new Alert( "Cannot instantiate this category" ).culprit( "Category", category );
			} catch ( IllegalAccessException e ) {
				throw Alert.internalError( e );
			}
		} catch ( ClassNotFoundException e ) {
			return new Dummy( world );
		}
	}
	
	
}
