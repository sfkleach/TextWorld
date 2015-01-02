package com.steelypip.powerups.minxson.templates;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.json.JSONKeywords;
import com.steelypip.powerups.minxml.FlexiMinXML;
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
	
	MinXML dereference( final String name, MinXML default_value ) {
		if ( name == null ) {
			throw new Alert( "ID with missing name" );
		}
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
	
	MinXML dereference( final MinXML minx, MinXML default_value ) {
		return this.dereference( minx.getAttribute( JSONKeywords.KEYS.ID_NAME ), default_value );	
	}
	
	private @Nullable MinXML evalIf( MinXML expr, MinXML default_value ) {
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
			for ( MinXML kid : expr ) {
				if ( kid.hasAttribute( JSONKeywords.KEYS.FIELD ) ) {	
					if ( kid.hasAttribute( JSONKeywords.KEYS.FIELD, field_value ) ) {
						return this.eval( kid );
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
			throw new Alert( "Cannot render this expression as a string" ).culprit( "Expression", minx );
		}
	}
	
	public String evalAsString( final MinXML minx ) {
		final MinXML evaluated = this.eval( minx );
		if ( evaluated == null ) return null;
		if ( evaluated.hasName( JSONKeywords.KEYS.CONSTANT ) ) {
			return evaluated.getAttribute( JSONKeywords.KEYS.CONSTANT_VALUE, "" );
		} else {
			throw new Alert( "Cannot convert to string" ).culprit( "Value", evaluated );
		}
	}
	
	public int evalAsInt( final MinXML minx ) {
		final MinXML evaluated = this.eval( minx );
		if ( evaluated == null ) throw new Alert( "Evaluates to null not an integer" ).culprit( "Expression", minx );
		if ( evaluated.hasName( JSONKeywords.KEYS.CONSTANT ) ) {
			return Integer.parseInt( minx.getAttribute( JSONKeywords.KEYS.CONSTANT_VALUE, "" ) );
		} else {
			throw new Alert( "Cannot convert to string" ).culprit( "Value", evaluated );
		}
	}
	
	public MinXML eval( final MinXML minx ) {
		final @NonNull String interned = minx.getInternedName(); 
		if ( interned == JSONKeywords.KEYS.CONSTANT ) {
			return minx;
		} else if ( interned == JSONKeywords.KEYS.ID ) {
			return dereference( minx, null ); 
		} else if ( interned == JSONKeywords.KEYS.IF ) {
			return evalIf( minx, null );
		} else if ( interned == "indexByPosition" ) {
			final MinXML src = this.eval( minx.getFirst() );
			final int n = this.evalAsInt( minx.get( 1 ) );
			return src.get( n );
		} else if ( interned == "for" ) {
			return this.evalFor( minx );
		} else {
			return evalConstructed( minx );
		}			
	}

	private MinXML evalFor( MinXML minx ) {
		final String loop_expr_var = minx.getAttribute( "in" );
		final String loop_var = minx.getAttribute( "var" );
		final MinXML loop_var_previous_value = this.environment.get( loop_var );
		final MinXML evaluated = new FlexiMinXML( JSONKeywords.KEYS.ARRAY );
		try {
			final MinXML sequence = this.dereference( loop_expr_var, null );
			if ( sequence != null ) {
				for ( MinXML eval_kid :sequence ) {
					if ( eval_kid != null ) {
						this.environment.put( loop_var, eval_kid );
						for ( MinXML expr : minx ) {
							final MinXML e = this.eval( expr );
							if ( e != null ) {
								evaluated.add( e );
							}
						}
					}
				}
			}
		} finally {
			this.environment.put( loop_var, loop_var_previous_value );
		}
		return evaluated;
	}

	private MinXML evalConstructed( final MinXML minx ) {
		final MinXML evaluated = new FlexiMinXML( minx.getName() );
		boolean changed = false;
		for ( MinXML kid : minx ) {
			if ( kid.hasAttribute( JSONKeywords.KEYS.FIELD ) ) {
				changed = addEvaluatedAttribute( evaluated, changed, kid );
			} else {
				final MinXML eval_kid = this.eval( kid );
				changed = changed || ( kid != eval_kid );
				if ( eval_kid != null ) {
					evaluated.add( eval_kid );
				}
			}
		}
		if ( changed ) {
			evaluated.putAllAttributes( minx );
			return evaluated;
		} else {
			return minx;
		}
	}

	private boolean addEvaluatedAttribute( final MinXML evaluated, boolean changed, MinXML entry  ) {
		final String value = this.evalAsString( entry );
		if ( value != null ) {
			final String key = entry.getAttribute( JSONKeywords.KEYS.FIELD );
			evaluated.putAttribute( key, value );
			changed = true;
		}
		return changed;
	}
	
	public void renderMinXML( final MinXML minx ) {
		final @NonNull String interned = minx.getInternedName(); 
		if ( interned == JSONKeywords.KEYS.CONSTANT ) {
			MinXMLWriter.printValue( print_writer, minx.getAttribute( JSONKeywords.KEYS.CONSTANT_VALUE, "" ) );
		} else if ( interned == JSONKeywords.KEYS.ARRAY ) {
			for ( MinXML kid : minx ) {
				this.renderMinXML( kid );
			}
		} else {
			print_writer.print( '<' );
			print_writer.print( interned );
			for ( Map.Entry< String, String > entry : minx.asMapEntries() ) {
				this.printAttribute( entry.getKey(), entry.getValue() );
			}
			if ( minx.isEmpty() ) {
				print_writer.print( "/>" );
			} else {
				print_writer.print( '>' );
				for ( MinXML kid : minx ) {
					this.renderMinXML( kid );
				}
				print_writer.print( "</" );
				print_writer.print( interned );
				print_writer.print( '>' );
			}
		}		
	}
	
	public void render( final MinXML minx ) {
		this.renderMinXML( this.eval( minx ) );
		this.print_writer.flush();
	}
	
}
