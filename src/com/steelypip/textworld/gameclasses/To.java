package com.steelypip.textworld.gameclasses;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.steelypip.powerups.common.EmptySet;
import com.steelypip.powerups.minxml.MinXML;

public class To extends Relation1 {
	
	final Map< Thing, Set< Thing > > leads_to = new IdentityHashMap<>();
	
	@Override
	public void init( MinXML initial_configuration ) {
	}

	public boolean leadsTo( Thing thing ) {
		Set< Thing > set = this.leads_to.get( this );
		return set != null && set.contains( thing );
	}
	
	public void setRouteTo( final Thing place ) {
		Set< Thing > set = this.leads_to.get( this );
		if ( set == null ) {
			set = new HashSet< Thing >();
			this.leads_to.put( place, set );
		}
		set.add( place );
	}
	
	public boolean hasNoExits() {
		Set< Thing > set = this.leads_to.get( this );
		return set == null || set.isEmpty();
	}
	
	public Set< Thing > leadsToLocations() {
		Set< Thing > set = this.leads_to.get( this );
		if ( set != null ) {
			return set;
		} else {
			return new EmptySet< Thing >();
		}
	}
}
