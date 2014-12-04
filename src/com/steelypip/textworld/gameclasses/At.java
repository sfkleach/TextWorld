package com.steelypip.textworld.gameclasses;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.common.EmptySet;
import com.steelypip.powerups.minxml.MinXML;

public class At extends Function2 {
	
	protected @NonNull Thing default_location; 
	protected final Map< Thing, Thing > contained_by = new IdentityHashMap<>();
	protected final Map< Thing, Set< Thing > > contains = new IdentityHashMap<>();
	
	public At( @NonNull Thing limbo_the_default_location ) {
		this.default_location = limbo_the_default_location;
	}



	public Thing getLocation( Thing thing ) {
		final Thing container = this.contained_by.get( thing );
		return container != null ? container : this.default_location;
	}
	
	private void disconnect( Thing item ) {
		final Thing place = this.contained_by.get( item );
		if ( place != null ) {
			this.contains.get( place ).remove( item );
			this.contained_by.put( item, null );
		}
	}
	
	public void setLocation( Thing item, Thing place ) {
		this.disconnect( item );
		this.contained_by.put( item, place );
		Set< Thing > set = this.contains.get( this );
		if ( set == null ) {
			set = new HashSet< Thing >();
			this.contains.put( place, set );
		}
		set.add( item );
	}
	
	public Set< Thing > getLocation() {
		Set< Thing > set = this.contains.get( this );
		if ( set != null && ! set.isEmpty() ) {
			return set;
		} else {
			//	No need to record this persistently.
			set = new HashSet<Thing>();
			set.add( this.default_location );
			return set;
		}
	}

}
