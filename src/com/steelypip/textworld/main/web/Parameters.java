package com.steelypip.textworld.main.web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.steelypip.powerups.alert.Alert;

public class Parameters extends TreeMap< String, List< String > > {
	
	public Parameters() {
		super();
	}

	public Parameters( Comparator< ? super String > comparator ) {
		super( comparator );
	}

	public Parameters( Map< ? extends String, ? extends List< String >> m ) {
		super( m );
	}

	public Parameters( SortedMap< String, ? extends List< String >> m ) {
		super( m );
	}

	private static final long serialVersionUID = -6176826163700567771L;

	public String fetch( final String key ) {
		final List< String > list = this.get( key );
		if ( list == null || list.isEmpty() ) {
			throw new Alert( "Mandatory parameter missing" ).culprit( "Parameter", key );
		} else if ( list.size() > 1 ) {
			throw new Alert( "Non-unique mandatory unique parameter" ).culprit( "Parameter", key );
		} else {
			return list.get( 0 );
		}
	}
	
	public void addParameter( final String key, final String value ) {
		List< String > list = this.get( key );
		if ( list == null ) {
			list = new ArrayList<>();
			this.put( key, list );
		}
		list.add( value );
	}
	
}
