package com.steelypip.powerups.shell;

import java.util.Arrays;
import java.util.LinkedList;

import com.steelypip.powerups.alert.Alert;

public abstract class CmdLineProcessor {

	public abstract void processRest( CmdArgs rest );
	public abstract boolean option( CmdOption option, CmdArgs args );
	
	public void processShortOption( char ch, CmdArgs args ) {
		if ( !this.option( new CmdOption.ShortCmdOption( ch ), args ) ) {
			throw new Alert( "Invalid short option" );			
		}
	}
	
	public void processLongOption( String key, CmdArgs args ) {
		if ( !this.option( new CmdOption.LongCmdOption( key ), args ) ) {
			throw new Alert( "Invalid long option" );			
		}
	}
	
	public void processCmdLineArgs( String[] args ) {
		LinkedList< String > list = new LinkedList< String >( Arrays.asList( args ) );
		this.processOptions( list );
		final CmdArgs rest = new CmdArgs.RestArgs( list );
		this.processRest( rest );
		rest.check();
	}	

	protected void processOptions( LinkedList< String > list ) {
		while ( ! list.isEmpty() ) {
			final String arg = list.removeFirst();
			try {
				if ( arg.startsWith( "--" ) ) {
					if ( arg.equals( "--" ) ) {
						//	End of options.
						break;
					} else {
						//	Long option.
						int narg = arg.indexOf( '=' );
						//	TODO: I think this is an error. We should only process arguments up to the next option?!
						String key = narg == -1 ? arg.substring( 2 ) : arg.substring( 2, narg );
						String value = narg == -1 ? null : arg.substring( narg + 1 );
						
						CmdArgs args = makeUnaryArg( list, value );
						this.processLongOption( key, args );
						args.check();
					}
				} else if ( arg.startsWith( "-" ) ) {
					//	Expand the short options.
					int narg = arg.indexOf( '=' );
					//	TODO: I think this is an error. We should only process arguments up to the next option?!
					String key = narg == -1 ? arg.substring( 1 ) : arg.substring( 1, narg );
					String value = narg == -1 ? null : arg.substring( narg + 1 );
					for ( 
						int n = 0;	 
						n < key.length();
						n++
					) {
						char ch = key.charAt( n );
						boolean is_last = n + 1 == key.length();
						if ( is_last ) {
							CmdArgs args = makeUnaryArg( list, value );
							this.processShortOption( ch, args );
							args.check();
						} else {
							CmdArgs none = new CmdArgs.NoArgs();
							this.processShortOption( ch, none );
							none.check();
						}
					}
				} else {
					list.addFirst(  arg  );	//	push back.
					break;
				}
			} catch ( Alert a ) {
				throw a.culprit( "Option", arg );
			}
		}
	}

	private CmdArgs makeUnaryArg( LinkedList< String > list, String value ) {
		return value == null ? new CmdArgs.LimitedOptionalArg( list ) : new CmdArgs.UnlimitedMandatoryArg( value );
	}


}
