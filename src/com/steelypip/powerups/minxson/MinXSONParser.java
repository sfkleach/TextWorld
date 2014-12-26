/**
 * Copyright Stephen Leach, 2014
 * This file is part of the MinXML for Java library.
 * 
 * MinXML for Java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MinXML for Java.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */

package com.steelypip.powerups.minxson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.math.BigInteger;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.charrepeater.CharRepeater;
import com.steelypip.powerups.charrepeater.ReaderCharRepeater;
import com.steelypip.powerups.io.LineNumberCounter;
import com.steelypip.powerups.json.JSONKeywords;
import com.steelypip.powerups.minxml.FlexiMinXMLBuilder;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.powerups.minxml.MinXMLBuilder;

/*
 * This class implements a parser for the MinXSON grammar, which is 
 * a hybrid of minimal XML and JSON. It has a number of non-standard
 * extensions that can be enabled.
 * 
 * This parser utilizes a MinXMLBuilder to generate MinXML objects. 
 * It can either be used to read individual expressions one by one off the input 
 * or it can be turned into an iterator and used in a loop. 
 */
public class MinXSONParser implements Iterable< MinXML > {
	
	private static final String TYPE_ATTRIBUTE_PREFIX = "@";
//	private static final char FIELD_ATTRIBUTE_SUFFIX = ':';

	protected JSONKeywords json_keys = JSONKeywords.KEYS;
	protected SurfaceSyntax surface_syntax;
	private final CharRepeater cucharin;
	private MinXMLBuilder parent = null;
	
	private LineNumberCounter line_number; 
	private File file;
	
	private final TreeMap< String, String > extra_attributes = new TreeMap< String, String >();
	private boolean EMBEDDED_EXTENSION = false;
	private boolean TYPE_PREFIX_EXTENSION = false;
	private boolean TUPLE_EXTENSION = false;
	private boolean FIELD_EXTENSION = false;
	private boolean INDEX_EXTENSION = false;
	
	/**
	 * These extensions toggle optional features.
	 * 	Option 'A' switches on _A_ll extensions.
	 *  Option 'E' switches on the _E_mbedded extension.
	 *  Option 'F' switches on the _F_ield extension.
	 *  Option 'T' switches on the _T_ype-prefix extension.
	 *  Option 'U' switches on the t_U_ple extension.
	 * @param extensions a character array encoding the set of extensions needed.
	 * @return the parser, used for method chaining 
	 */
	public MinXSONParser enableExtensions( char[] extensions ) {
		boolean all = false;
		for ( char ch : extensions ) {
			switch ( ch ) {
			case 'A': 
				all = true;
			case 'E': 
				EMBEDDED_EXTENSION = true; 
				if ( !all ) break;
			case 'F':
				FIELD_EXTENSION = true;
				if ( !all ) break;
			case 'I':
				INDEX_EXTENSION = true;
				if ( !all ) break;
			case 'T': 
				TYPE_PREFIX_EXTENSION = true; 
				if ( !all ) break;
			case 'U':
				TUPLE_EXTENSION = true;
				break;
			default:
				throw new Alert( "Unrecognised extension" ).culprit( "Extension flag", ch );
			}
		}
		return this;
	}
	
	public MinXSONParser( CharRepeater rep, MinXMLBuilder parent, char... extensions ) {
		this.parent = parent != null ? parent : new FlexiMinXMLBuilder();
		this.cucharin = rep;
		this.enableExtensions( extensions );
		this.surface_syntax = StdSurfaceSyntax.newSurfaceSyntax( this.json_keys, this.TUPLE_EXTENSION );
	}

	public MinXSONParser( final Reader rep, MinXMLBuilder parent, char... extensions ) {
		this( new ReaderCharRepeater( rep ), parent, extensions );
	}

	public MinXSONParser( final Reader rep, char... extensions ) {
		this( new ReaderCharRepeater( rep ), null, extensions );
	}
	
	public MinXSONParser( final File file, MinXMLBuilder parent, char... extensions ) throws FileNotFoundException {
		final LineNumberReader lnr = new LineNumberReader( new FileReader( file ) );
		lnr.setLineNumber( 1 );
		this.line_number = () -> ( lnr.getLineNumber() );
		this.file = file;
		
		this.cucharin = new ReaderCharRepeater( lnr );
		this.parent = parent != null ? parent : new FlexiMinXMLBuilder();
		this.enableExtensions( extensions );
		this.surface_syntax = StdSurfaceSyntax.newSurfaceSyntax( this.json_keys, this.TUPLE_EXTENSION );
	}

	public MinXSONParser( final File file, char... extensions ) throws FileNotFoundException {
		this( file, null, extensions );
	}

	public CharRepeater getCucharin() {
		return this.cucharin;
	}

	public JSONKeywords getJSONKeywords() {
		return this.json_keys;
	}
	
	public SurfaceSyntax getSurfaceSyntax() {
		return this.surface_syntax;
	}

	private char nextChar() {
		return this.cucharin.nextChar();
	}
	
	private void discardChar( final int n ) {
		for ( int i = 0; i < n; i++ ) {
			this.cucharin.skipChar();
		}
	}
		
	private void discardChar() {
		this.cucharin.skipChar();
	}
		
	private char peekChar() {
		return this.cucharin.peekChar();
	}
	
	private char peekChar( final char default_char ) {
		return this.cucharin.peekChar( default_char );
	}
	
	private boolean hasNextExpression() {
		this.eatWhiteSpace();
		return this.cucharin.peekChar( '\0' ) == '<';
	}
	
	public void mustReadChar( final char ch_want ) {
		if ( this.cucharin.isNextChar( ch_want ) ) {
			this.cucharin.skipChar();
		} else {
			if ( this.cucharin.hasNextChar() ) {
				throw new Alert( "Unexpected character" ).culprit( "Wanted", "" + ch_want ).culprit( "Received", "" + this.cucharin.peekChar() );
			} else {
				throw new Alert( "Unexpected end of stream" );
			}			
		}
	}
	
	private int mustReadOneOf( final String t ) {
		final char ch = this.nextChar();
		final int n = t.indexOf( ch );
		if ( n == -1 ) {
			throw new Alert( "Unexpected character" ).culprit( "Expected one of", t ).culprit( "Received", ch );
		}
		return n;
	}
	
	public boolean tryReadChar( final char ch_want ) {
		if ( this.cucharin.isNextChar( ch_want ) ) {
			this.cucharin.skipChar();
			return true;
		} else {
			return false;
		}
	}
	
	private boolean	tryReadString( final String want ) {
		if ( this.cucharin.isNextString( want ) ) {
			this.discardChar( want.length() );
			return true;
		} else {
			return false;
		}
	}
	
	private boolean	tryPeekString( final String want ) {
		return this.cucharin.isNextString( want );
	}
	
	public boolean tryEatComment() {
		final char ch  = this.cucharin.peekChar( '\0' );
		if ( ch == '/' ) {
			this.cucharin.skipChar();
			final char nch = this.cucharin.peekChar( '\0' );
			if ( nch == '/' ) {
				this.cucharin.skipUntil( '\n' );
				this.cucharin.pushChar( '\n' ); 	//	But don't consume the newline.
				return true;
			} else if ( nch == '*' ) {
				this.cucharin.skipChar();
				for (;;) {
					this.cucharin.skipUntil( '*' );
					while ( this.tryReadChar( '*' ) ) {
						//	skip.
					}
					if ( this.cucharin.nextChar() == '/' ) break;
				}
				return true;
			} else {
				this.cucharin.pushChar( ch );
				return false;
			}
		} else {
			return false;
		}
	}
	
	public void eatWhiteSpace() {
		while ( this.cucharin.hasNextChar() ) {
			final char ch = this.cucharin.peekChar();
			if ( ch == '#' ) {
				this.cucharin.skipChar();
				if ( this.peekChar( '\0' ) == '!' && this.parent.isAtNestingLevel( 0 ) ) {
					//	Shebang - note that this is coded quite carefully to leave 
					//	the options open for other interpretations of #.
					this.cucharin.skipUntil( '\n' );
				} else {
					this.cucharin.pushChar( '#' );
					return;
				}
			} else if ( this.tryEatComment() ) {
				//	Continue.
			} else if ( Character.isWhitespace( ch ) ) {
				this.cucharin.skipChar();
			} else {
				return;
			}
		}
	}
	
	private void startTagOpen( final String tag ) {
		this.parent.startTagOpen( tag );
		for ( Map.Entry< String, String > m : this.extra_attributes.entrySet() ) {
			this.parent.put( m.getKey(), m.getValue() );
		}
		this.extra_attributes.clear();
	}

	private void startTagClose( final String tag ) {
		this.parent.startTagClose( tag );
	}

	private void endTag( final String tag ) {
		this.parent.endTag( tag );
	}

	private void put( final String key, final String value ) {
		this.parent.put( key, value );
	}
	
	private static boolean isNameChar( final char ch ) {
		return Character.isLetterOrDigit( ch ) || ch == '-' || ch == '.';
	}
	
	private String readName() {
		final StringBuilder name = new StringBuilder();
		while ( this.cucharin.hasNextChar() ) {
			final char ch = this.cucharin.nextChar();
			if ( isNameChar( ch ) ) {
				name.append( ch );
			} else {
				this.cucharin.pushChar( ch );
				break;
			}
		}
		return name.toString();
	}
	
	private String readEscapeContent() {
		final StringBuilder esc = new StringBuilder();
		for (;;) {
			final char ch = this.nextChar();
			if ( ch == ';' ) break;
			esc.append( ch );
			if ( esc.length() > 4 ) {
				throw new Alert( "Malformed escape" ).culprit( "Sequence", esc );
			}
		}
		return esc.toString();
	}
	
	private char entityLookup( final String symbol ) {
		Character c = CharacterEntityLookup.lookup( symbol );
		if ( c != null ) {
			return c;
		} else {
			throw new Alert( "Unexpected escape sequence after &" ).culprit( "Sequence", symbol );
		}
	}
	
	private char readEscape() {
		final String esc = this.readEscapeContent();
		if ( esc.length() >= 2 && esc.charAt( 0 ) == '#' ) {
			try {
				final int n = Integer.parseInt( esc.toString().substring( 1 ) );
				return (char)n;
			} catch ( NumberFormatException e ) {
				throw new Alert( "Unexpected numeric sequence after &#", e ).culprit( "Sequence", esc );
			}
		} else {
			return this.entityLookup( esc );
		}	
	}
	
	private String readAttributeValue() {
		final StringBuilder attr = new StringBuilder();
		final char q = this.nextChar();
		if ( q != '"' && q != '\'' ) throw new Alert( "Attribute value not quoted" ).culprit( "Character", q );
		for (;;) {
			char ch = this.nextChar();
			if ( ch == q ) break;
			if ( ch == '&' ) {
				attr.append( this.readEscape() );
			} else {
				if ( ch == '<' ) {
					throw new Alert( "Forbidden character in attribute value" ).hint( "Use an entity reference" ).culprit( "Character", ch );
				}
				attr.append( ch );
			}
		}
		return attr.toString();
	}	

	
	void consumeOptionalTerminator() {
		// End of input is a valid terminator!
		while ( this.cucharin.hasNextChar() ) {
			final char ch  = this.peekChar();
			if ( this.surface_syntax.isTerminatorChar( ch ) ) {
				this.discardChar();
				break;
			} else if ( Character.isWhitespace( ch ) ) {
				this.discardChar();
				continue;
			} else if ( ch == '<' ) {
				break;
			} else if ( this.surface_syntax.isCloseChar( ch ) ) {
				break;
			} else {
				throw new Alert( "Unexpected character whilst looking for separator/terminator" ).culprit( "Character", ch );
			}
		}
	}

	private void discardXMLComment( final char ch ) {
		if ( ch == '!' ) {
			if ( this.cucharin.isNextChar( '-' ) ) {
				//	This section discards XML comments.
				this.cucharin.skipChar();
				this.mustReadChar( '-' );
				int count_minuses = 0;
				for (;;) {
					final char nch = this.nextChar();
					if ( nch == '-' ) {
						count_minuses += 1;
					} else if ( nch == '>' && count_minuses >= 2 ) {
						break;
					} else {
						if ( count_minuses >= 2 ) {
							throw new Alert( "Invalid XML comment" ).hint( "Detected -- within the body of comment" ).culprit( "Character following --", (int)nch );
						}
						count_minuses = 0;
					}
				}
			} else {
				//	This section discards the DTD compopnents. This is an 
				//	optional extension of the MinXSON language designed to make
				//	importing from XML less onerous.
				for (;;) {
					final char nch = this.nextChar();
					if ( nch == '>' ) break;
					if ( nch == '<' ) this.discardXMLComment( this.nextChar() );
				}
			}
		} else {
			//	This is responsible for consuming the Prolog <?xml version=.... ?>.
			//	Also processing instructions: <? PITarget .... ?>.  This is an 
			//	optional extension of the MinXSON language designed to make
			//	importing from XML less onerous.
			this.cucharin.skipUntil( '>' );
		}
	}

	
	private boolean charEndsAttributes( final char c ) {
		return c == '/' || c == '>' || this.surface_syntax.isOpenArrayChar( c ) || c == '{';
	}

	/**
	 * Consumes a sequence of attributes and any subsequent whitespace.
	 */
	private Map< String, String > readExtraAttributes( final String initial_key ) {
		Map< String, String > attributes = new TreeMap<>();
		String initial_value = this.readAttributeValue();
		attributes.put( initial_key, initial_value );
//		this.extra_attributes.put( initial_key, initial_value );
		for (;;) {
			this.eatWhiteSpace();
			final char c = peekChar();
			if ( this.charEndsAttributes( c ) ) {
				break;
			}
			String key = this.readName();
			this.eatWhiteSpace();
			this.mustReadChar( '=' );
			this.eatWhiteSpace();
			final String value = readAttributeValue();
			attributes.put(  key,  value  );
		}
		return attributes;
	}
	
	public void readNamelessStartTag( String name ) {
		//	We have attributes without an element name.
		//	The strategy is to read the attributes without processing them
		//	and then allow the next item to be processed as normal.
		Map< String, String > attributes = this.readExtraAttributes( name );
//		this.eatWhiteSpace();
		this.mustReadExpr();
		
		final MinXML e = this.parent.partBuild();
		e.putAllAttributes( attributes );
		this.parent.addElement( e );
		
		this.eatWhiteSpace();
		this.mustReadChar( '/' );
		this.mustReadChar( '>' );
	}	
	
	
	void parseConstant( final String sofar, final String type ) {
		this.startTagOpen( json_keys.CONSTANT );
		this.put( json_keys.CONSTANT_TYPE, type );
		this.put( json_keys.CONSTANT_VALUE, sofar );
		this.startTagClose( json_keys.CONSTANT );
		this.endTag( json_keys.CONSTANT );
	}
	
	void readNumber() {
		boolean is_floating_point = false;
		StringBuilder sofar = new StringBuilder();
		boolean done = false;
		do {
			final char ch = this.peekChar( ' ' );
			switch ( ch ) {
				case '-':
				case '+':
					break;
				case '.':
					is_floating_point = true;
					break;
				default:
					if ( ! Character.isDigit( ch ) ) {
						done = true;
					}
					break;
			}
			if ( done ) break;
			sofar.append( ch );
			this.discardChar();
		} while ( ! done );
		
		//	We have slightly different tests for floating point versus integers.
		final String s = sofar.toString();
		try {
			if ( is_floating_point ) {
				Double.parseDouble( s );
			} else {
				new BigInteger( s );
			}
			this.parseConstant( s, is_floating_point ? json_keys.FLOAT : json_keys.INTEGER );	
		} catch ( NumberFormatException e ) {
			throw new Alert( "Malformed number" ).culprit( "Bad number", sofar );
		}
	}
	
	void readEscapeChar( final StringBuilder sofar ) {
		final char ch = this.nextChar();
		switch ( ch ) {
			case '\'':
			case '"':
			case '/':
			case '\\':
				sofar.append(  ch  );
				break;
			case 'n':
				sofar.append( '\n' );
				break;
			case 'r':
				sofar.append( '\r' );
				break;
			case 't':
				sofar.append( '\t' );
				break;
			case 'f':
				sofar.append( '\f' );
				break;
			case 'b':
				sofar.append( '\b' );
				break;
			case '&':
				sofar.append( this.readEscape() );
				break;
			default:
				sofar.append( ch );
				break;
		}
	}
	

	void readJSONString() {
		this.parseConstant( this.readJSONStringText(), json_keys.STRING );
	}

	public String readJSONStringText() {
		final char quote_char = this.nextChar();
		StringBuilder sofar = new StringBuilder();
		boolean done = false;
		
		while( ! done ) {
			final char ch = this.nextChar();
			switch ( ch ) {
				case '"':
				case '\'':
					if ( ch == quote_char ) {
						done = true;
					} else {
						sofar.append( ch );
					}
					break;
				case '\\':
					this.readEscapeChar( sofar );
					break;
				default:
					sofar.append( ch );
					break;
			}
		}
		return sofar.toString();
	}
	
	static boolean isStartOfNumber( final char ch ) {
		return Character.isDigit( ch ) || ch == '-' || ch == '+';
	}
	
	public static boolean isIdentifierStart( final char ch ) {
		return Character.isLetter( ch ) || ch == '_';
	}
	
	static boolean isIdentifierContinuation( final char ch ) {
		return isNameChar( ch );
	}

	void parseId( final String sofar ) {
		this.startTagOpen( json_keys.ID );
		this.put( json_keys.ID_NAME, sofar );
		this.startTagClose( json_keys.ID );
		this.endTag( json_keys.ID ); 
	}
	
	void readIdentifier() {
		final String identifier = readIdentifierText();
		if ( "true".equals( identifier ) || "false".equals( identifier ) ) {
			this.parseConstant( identifier, json_keys.BOOLEAN );
		} else if ( identifier.equals( "null" ) ) {
			this.parseConstant( identifier, json_keys.NULLEAN );
		} else {
			if ( this.INDEX_EXTENSION ) {
				this.eatWhiteSpace();
				if ( this.tryReadChar( '[' ) ) {
					this.startTagOpen( "indexByPosition" );
					this.startTagClose( "indexByPosition" );
					this.startTagOpen( json_keys.ID );
					this.put( json_keys.ID_NAME, identifier );
					this.startTagClose( json_keys.ID );
					this.startTagClose( json_keys.ID );
					this.mustReadExpr();
					this.endTag( "indexByPosition" );
				} else {
					parseId( identifier );
				}
			} else {
				parseId( identifier );
			}
		}
	}

	public String readIdentifierText() {
		StringBuilder sofar = new StringBuilder();
		for (;;) {
			final char ch = this.peekChar( ' ' );	// A character that's not part of an identifier.
			if ( isIdentifierContinuation( ch ) ) {
				this.discardChar();
				sofar.append( ch );
			} else {
				break;
			}
		}
		return sofar.toString();
	}	
	
	private void readArray( final char ch ) {
		this.discardChar();
		final String tag = this.surface_syntax.tagOfOpenArrayChar( ch );
		this.startTagOpen( tag );
		this.startTagClose( tag );
		
		final char closing_char = this.surface_syntax.closingArrayChar( ch );
		this.eatWhiteSpace();
		if ( ! this.tryReadChar( closing_char ) ) {
			for (;;) {
				this.mustReadExpr();
				this.consumeOptionalTerminator();
				this.eatWhiteSpace();
				if ( this.tryReadChar( closing_char ) ) break;
			}
		}
		
		this.endTag( tag );
		
		if ( this.surface_syntax.isCloseParenthesis( closing_char ) ) {
			MinXML parenthetical_expression = this.parent.partBuild();
			if ( parenthetical_expression.size() == 1 ) {
				parenthetical_expression = parenthetical_expression.getFirst();
			} else if ( ! this.TUPLE_EXTENSION ) {
				throw new Alert( "Parentheses used as tuple without tuple extension enabled" );
			}
			this.parent.addElement( parenthetical_expression );
		}
	}
	
	private void readObject() {
		this.discardChar();
		this.startTagOpen( json_keys.OBJECT );
		this.startTagClose( json_keys.OBJECT );
		this.eatWhiteSpace();
		if ( ! this.tryReadChar( '}' ) ) {
			for (;;) {
				final char nch = this.peekChar( '\0' );
				final String field;
				if ( nch == '"' ) {
					field = readJSONStringText();
				} else if ( isIdentifierStart( nch ) ) {
					field = readIdentifierText();
				} else {
					throw new Alert( "Field name required" ).culprit( "At character", nch );
				}
				this.eatWhiteSpace();
				if ( ! this.tryReadChar( '=' ) ) {
					this.mustReadChar( ':' );
				}
				this.mustReadExpr();
				final MinXML e = this.parent.partBuild();
				e.putAttribute( this.json_keys.FIELD, field );
				this.parent.addElement( e );
				this.consumeOptionalTerminator();
				this.eatWhiteSpace();
				if ( this.tryReadChar( '}' ) ) break;
			}
		}
		
		this.endTag( json_keys.OBJECT );

	}
	
	void readTypeTag() {
		final boolean is_string = this.cucharin.isNextChar( '"' ) || this.cucharin.isNextChar( '\'' );
		final String name = is_string ? this.readJSONStringText() : this.readName();
		this.extra_attributes.put( json_keys.TYPE, name );
		this.readWithoutPending();
	}
	
	private void processAttributes() {
		for (;;) {
			this.eatWhiteSpace();
			char c = peekChar();
			if ( c == '/' || c == '>' || this.surface_syntax.isOpenArrayChar( c ) || c == '{'  ) break;
			final String key = this.readName();
			
			this.eatWhiteSpace();
			final int n = this.mustReadOneOf( "=:" );
			this.eatWhiteSpace();
			final String value = n == 0 ? this.readAttributeValue() : this.readJSONStringText();
			this.put( key, value );
		}
	}
	
	private void normalStartTag( final String name ) {
		//	This section is a normal start/standalone tag.
		this.startTagOpen( name );
		
		this.processAttributes();
		this.startTagClose( name );
		
		this.eatWhiteSpace();
		final char nch = this.nextChar();
		if ( nch == '/' ) {
			//	It was a standalone tag.
			this.mustReadChar( '>' );
			this.endTag( name );
		} else if ( nch == '>' ) {
			//	It was a start tag.
			this.eatWhiteSpace();
			if ( ! this.tryPeekString( "</" ) ) {
				for (;;) {
					this.mustReadExpr();
					this.consumeOptionalTerminator();
					this.eatWhiteSpace();
					if ( this.tryPeekString( "</" ) ) break;					
				}
			}
			this.discardChar();
			this.discardChar();
			this.eatWhiteSpace();
			if ( ! this.tryReadChar( '>' ) ) {
				String end_tag = this.readName();
				if ( ! end_tag.equals( name ) ) {
					throw new Alert( "Mismatched tags" ).culprit( "Expecting", name ).culprit( "Actual", end_tag );
				}
				this.eatWhiteSpace();
				this.mustReadChar( '>' );
			}
			this.endTag( name );
		} else if ( this.EMBEDDED_EXTENSION && ( this.surface_syntax.isOpenArrayChar( nch ) || nch == '{' ) ) {
			this.cucharin.pushChar( nch );
			for ( MinXML kid : this.mustReadExprPartBuild() ) {
				this.parent.addElement( kid );
			}
			this.endTag( name );
			this.eatWhiteSpace();
			this.mustReadChar( '/' );
			this.mustReadChar( '>' );
		} else {
			throw new Alert( "Invalid continuation" ).culprit( "Character", nch );
		}
	}
	
	private void readStartTag() {
		final String name = this.readName();
		this.eatWhiteSpace();
		if ( this.EMBEDDED_EXTENSION ) {
			if ( this.tryReadChar( '=' ) ) {
				readNamelessStartTag( name );
			} else if ( name.isEmpty() ) {
				this.mustReadExpr();
				this.eatWhiteSpace();
				this.mustReadChar( '/' );
				this.mustReadChar( '>' );
			} else {
				this.normalStartTag( name );
			} 
		} else {
			this.normalStartTag( name );
		}
	}
	
	private void readTag() {
		this.discardChar();	//	Throw away leading <.
		this.eatWhiteSpace();
		final char ch = this.peekChar();
		if ( ch == '/' ) {
			throw new Alert( "Unmatched end tag" );
		} else if ( ch == '!' || ch == '?' ) {
			this.discardChar();
			this.discardXMLComment( ch );
			this.readOneTag();
			return;
		} else {
			this.readStartTag();
		}
	}

	private void readWithoutPending() {
		this.eatWhiteSpace();
		if ( ! this.cucharin.hasNextChar() ) return;
		final char ch = this.peekChar();
		if ( ch == '<' ) {
			this.readTag();
		} else if ( isStartOfNumber( ch ) ) {
			this.readNumber();
		} else if ( ch == '"' || ch == '\'' ) {
			this.readJSONString();
		} else if ( isIdentifierStart( ch ) ) {
			this.readIdentifier();
		} else if ( this.surface_syntax.isOpenArrayChar( ch ) ) {
			this.readArray( ch );
		} else if ( ch == '{' ) {
			this.readObject();
		} else if ( this.TYPE_PREFIX_EXTENSION  && this.tryReadString( TYPE_ATTRIBUTE_PREFIX ) ) {
			this.readTypeTag();
		} else {
			throw new Alert( "Unexpected character" ).hint( "At the start of an item" ).culprit( "Character", ch );
		}		
	}
	
	private boolean readOneTag() {		 
		this.eatWhiteSpace();
		if ( this.cucharin.hasNextChar() ){
			this.readWithoutPending();
			return true;
		} else {
			return false;
		}
	}
	
	
	private boolean readExpr() {
		final int n = this.parent.nestingLevel();
		boolean read_tag = false;
		while ( this.readOneTag() ) {
			read_tag = true;
			if ( this.parent.isAtNestingLevel( n ) ) break;
		}
		if ( ! this.parent.isAtNestingLevel( n ) ) {
			throw new Alert( "Unexpected end of input" );
		}
		return read_tag;
	}
	
	private void mustReadExpr() {
		if ( ! this.readExpr() ) {
			throw new Alert( "Unexpected end of input" );
		}
	}

	private MinXML mustReadExprPartBuild() {
		this.mustReadExpr();
		return this.parent.partBuild();
	}
	
	/**
	 * Reads a MinXSON expression off the input and returns a MinXML object.
	 * 
	 * @return a MinXML object or null if the end of input has been reached.
	 */
	public MinXML read() {
		try {
			this.readExpr();
			return parent.build( null );
		} catch ( Alert alert ) {
			if ( this.line_number != null ) {
				alert.culprit( "Line number", this.line_number.getLineNumber() );
			} 
			if ( this.file != null ) {
				alert.culprit( "File", this.file );
			}
			throw alert;
		}
	}

	/**
	 * This method returns an iterator which consumes the input
	 * stream and yields MinXML trees.
	 */
	public Iterator< MinXML > iterator() {
		return new Iterator< MinXML >() {

			@Override
			public boolean hasNext() {
				return MinXSONParser.this.hasNextExpression();
			}

			@Override
			public MinXML next() {
				return MinXSONParser.this.read();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}

}
