package com.steelypip.powerups.minxson.templates;

import java.io.PrintWriter;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.json.JSONKeywords;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.powerups.minxml.MinXMLWriter;

public class XHTMLRenderTemplate {

	final Map< String, @Nullable MinXML > environment; 
	final PrintWriter print_writer;

	public XHTMLRenderTemplate( PrintWriter print_writer ) {
		this( print_writer, null );
	}
	
	public XHTMLRenderTemplate( PrintWriter print_writer, Map< String, @Nullable MinXML > environment ) {
		this.print_writer = print_writer;
		this.environment = environment;
	}
	
	MinXML dereference( final MinXML minx, MinXML default_value ) {
		final String name = minx.getAttribute( JSONKeywords.KEYS.ID_NAME );
		if ( name == null ) {
			throw new Alert( "ID with missing name" );
		}
//		System.err.println( "Deferencing " + name );
		if ( this.environment != null ) {
			final MinXML replacement = this.environment.get( name );
			if ( replacement != null ) {
				return replacement;
			} else {
				return default_value;
			}
		} else {
			throw new Alert( "No environment provided but trying to dereference variable" ).culprit( "Variable", name );
		}		
	}
	
	private @Nullable MinXML evalIf( MinXML expr, MinXML default_value ) {
//		System.err.println( "Evaluating " + expr );
		MinXML r = this.dereference( expr, default_value );
		if ( expr.hasAttribute( JSONKeywords.KEYS.IF_TEST, JSONKeywords.KEYS.IF_TEST_OK ) ) {
			boolean is_null = ( 
				r == null ||
				( 
					r.hasName( JSONKeywords.KEYS.CONSTANT ) &&
					r.hasAttribute( JSONKeywords.KEYS.CONSTANT_TYPE, "null" )
				)
			);
			final String field_value = is_null ? JSONKeywords.KEYS.IF_ELSE : JSONKeywords.KEYS.IF_THEN;
	//		System.err.println( "Field value " + field_value ); 
			for ( MinXML kid : expr ) {
				if ( kid.hasAttribute( JSONKeywords.KEYS.FIELD ) ) {	
					if ( kid.hasAttribute( JSONKeywords.KEYS.FIELD, field_value ) ) {
						return kid;
					}
				} else {
					throw new Alert( "Field missing on branch of if" ).culprit( "Element", kid );
				}
			}
			return null;
		} else {
			throw Alert.unimplemented();
		}
	}
	
	private void printAttribute( final String key, final String value ) {
		print_writer.print( ' ' );
		print_writer.print( key );
		print_writer.print( '=' );
		print_writer.print( '"' );
		MinXMLWriter.printValue( print_writer, value );
		print_writer.print( '"' );		
	}
	
	String asStringValue( final MinXML minx ) {
		final @NonNull String interned = minx.getInternedName(); 
		if ( interned == JSONKeywords.KEYS.CONSTANT ) {
			return minx.getAttribute( JSONKeywords.KEYS.CONSTANT_VALUE, "" );
		} else if ( interned == JSONKeywords.KEYS.ID ) {
			final MinXML replacement = dereference( minx, null );
			if ( replacement != null ) {
				return this.asStringValue( replacement );
			} else {
				return null;
			}
		} else {
			throw new Alert( "Cannot render this expression as a string" );
		}
	}
	
	public void render( final MinXML minx ) {
		final @NonNull String interned = minx.getInternedName(); 
		if ( interned == JSONKeywords.KEYS.CONSTANT ) {
			MinXMLWriter.printValue( print_writer, minx.getAttribute( JSONKeywords.KEYS.CONSTANT_VALUE, "" ) );
		} else if ( interned == JSONKeywords.KEYS.ID ) {
			final MinXML replacement = dereference( minx, null );
			if ( replacement != null ) {
				this.render( replacement );
			} 
		} else if ( interned == JSONKeywords.KEYS.ARRAY ) {
			for ( MinXML kid : minx ) {
				this.render( kid );
			}
		} else if ( interned == JSONKeywords.KEYS.IF ) {
			final MinXML result = evalIf( minx, null );
			if ( result != null ) {
				this.render( result );
			}
		} else {
			print_writer.print( '<' );
			print_writer.print( interned );
			for ( Map.Entry< String, String > entry : minx.asMapEntries() ) {
				if ( JSONKeywords.KEYS.FIELD.equals( entry.getKey() ) ) continue;
				this.printAttribute( entry.getKey(), entry.getValue() );
			}
			for ( MinXML kid : minx ) {
				if ( kid.hasAttribute( JSONKeywords.KEYS.FIELD ) ) {
					final String field_value = kid.getAttribute( JSONKeywords.KEYS.FIELD );
					this.printAttribute( field_value, this.asStringValue( kid ) );
				}
			}
			if ( minx.isEmpty() ) {
				print_writer.print( "/>" );
			} else {
				print_writer.print( '>' );
				
				for ( MinXML kid : minx ) {
					if ( kid.hasAttribute( JSONKeywords.KEYS.FIELD ) ) continue;
					this.render( kid );
				}
				
				print_writer.print( "</" );
				print_writer.print( interned );
				print_writer.print( '>' );
			}
		}
	}
	
}
