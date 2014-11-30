package com.steelypip.powerups.minxconf;

import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.charrepeater.CharRepeater;
import com.steelypip.powerups.minxml.FlexiMinXML;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.powerups.minxml.MinXMLBuilder;
import com.steelypip.powerups.minxson.MinXSONParser;

/**
 * This class implements a parser that consumes MinXConf expressions, 
 * which are designed to be suitable for writing simple settings files. 
 * It is a very thin layer on top of MinXSONParser, effectively translating
 * a MinXConfParser#read() into a MinXSONParser#readBindings().
 */
public class MinXConfParser {
	
	protected final CharRepeater cucharin;
	private MinXSONParser parser;

	public MinXConfParser( CharRepeater rep, MinXMLBuilder parent, char... extensions ) {
		this.parser = new MinXSONParser( rep, parent, extensions );
		this.cucharin = this.parser.getCucharin();
	}

	public MinXConfParser( Reader rep, char... extensions ) {
		this.parser = new MinXSONParser( rep, extensions );
		this.cucharin = this.parser.getCucharin();
	}

	public MinXConfParser( Reader rep, MinXMLBuilder parent, char... extensions ) {
		this.parser = new MinXSONParser( rep, parent, extensions );
		this.cucharin = this.parser.getCucharin();
	}
	
	public @NonNull MinXML read() {
		return this.readBindingsAsMinXML();
	}

	String readKey() {
		this.parser.eatWhiteSpace();
		final char ch = this.cucharin.peekChar( '\0' );
		if ( ch == '"' || ch == '\'' ) {
			return this.parser.readJSONStringText();
		} else if ( MinXSONParser.isIdentifierStart( ch ) ) {
			return this.parser.readIdentifierText();
		} else {
			throw new Alert( "Unexpected character while looking for key" ).culprit( "Character", ch );
		}
	}
	
	void consumeOptionalTerminator() {
		// End of input is a valid terminator!
		while ( this.cucharin.hasNextChar() ) {
			final char ch  = this.cucharin.peekChar();
			if ( ch == ',' || ch == ';' || ch == '\n' ) {
				this.cucharin.skipChar();
				break;
			} else if ( Character.isWhitespace( ch ) ) {
				this.cucharin.skipChar();
				continue;
			} else {
				throw new Alert( "Unexpected character whilst looking for separator/terminator" ).culprit( "Character", ch );
			}
		}
	}
	
	Map< String, @NonNull MinXML > readBindingsAsMap() {
		Map< String, @NonNull MinXML > top_level_bindings = new LinkedHashMap<>();
		final @NonNull String arrayTag = this.parser.getJSONKeywords().ARRAY;
		while ( this.cucharin.hasNextChar() ) {
			boolean extending = false;
			final String key = this.readKey();
			this.parser.eatWhiteSpace();
			if ( this.parser.tryReadChar( '+' ) ) {
				this.parser.mustReadChar( '=' );
				//	We have encountered a '+=' assignment, so we have to initialise the key.
				extending = true;
			} else if ( ! this.parser.tryReadChar( '=' ) ) {
				this.parser.mustReadChar( ':' );
			}
			final MinXML value = this.parser.read();
			if ( value == null ) {
				throw new Alert( "Unexpected end of file while reading value" );
			}
			if ( extending ) {
				if ( ! top_level_bindings.containsKey( key ) ) {
					top_level_bindings.put( key, new FlexiMinXML( arrayTag ) );
				}
				final MinXML array = top_level_bindings.get( key );
				array.add( value );	
			} else {
				top_level_bindings.put( key, value );
			}
			this.consumeOptionalTerminator();
		}
		return top_level_bindings;
	}
	
	@NonNull MinXML mapToMinXML( Map< String, @NonNull MinXML > bindings ) {
		final @NonNull MinXML object = new FlexiMinXML( this.parser.getJSONKeywords().OBJECT );
		final String field = this.parser.getJSONKeywords().FIELD;
		for ( Map.Entry< String, @NonNull MinXML > e : bindings.entrySet() ) {
			//	We exploit the fact that this is a newly built tree, so we don't have to 
			//	worry about the in-place update affected shared structures.
			final @NonNull MinXML v = e.getValue();
			v.putAttribute( field, e.getKey() );
			object.add( v );
		}
		return object;
	}
	
	public @NonNull MinXML readBindingsAsMinXML() {
		return this.mapToMinXML( this.readBindingsAsMap() );
	}
	
	


}
