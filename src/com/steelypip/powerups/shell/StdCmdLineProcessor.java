package com.steelypip.powerups.shell;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import com.steelypip.powerups.alert.Alert;

/**
 * The concept is that the main class will override option, processRest and printUsage.
 * Option will test for the different options in this pattern.
 * 
        @Override
        public boolean option( CmdOption option, CmdArgs args ) {
            if ( option.is( 'V', "version", "Print the version number." ) ) {
                ...
            } else if ( option.is( 'l', "list", "List the thingies." ) ) {
               ...
            } else {
               ... 
               return false;
            }
            return true;
        }

        @Override
        public void processRest( CmdArgs rest ) {
         	... 
        }

         @Override
        public void printUsageHeader( final PrintWriter w ) {
            w.println( "Usage: my-program [OPTIONS] TAGS*" );
        } 
        
        */


public abstract class StdCmdLineProcessor extends CmdLineProcessor implements CmdArgs.FileArgProcessor {

	
	@Override
	public void processCmdLineArgs( String [] args ) {
		try {
			super.processCmdLineArgs( args );
		} catch ( Alert e ) {
			e.report();
			this.printUsage( System.err );
			System.exit( -1 );
		}
	}

	public void printUsage( final PrintWriter output ) {
		this.printUsageHeader( output );
		this.printUsageOptions( output );
	}
	
	public void printUsage( final Writer w ) {
		this.printUsage( new PrintWriter( w ) );
	}
	
	public void printUsage( final OutputStream out ) {
		this.printUsage( new OutputStreamWriter( out ) );
	}

	
	public abstract void printUsageHeader( final PrintWriter w );
	
	public void printUsageOptions( final PrintWriter pw ) {
		this.option( 
			new CmdOption() {
				public boolean is( char short_opt, String long_opt, String docstring ) {
					pw.print( "    -" );
					pw.print( short_opt );
					pw.print( "      --" );
					pw.print( long_opt );
					if ( docstring != null && docstring.length() > 0 ) {
						final int n = 12 - long_opt.length() + 2;
						for ( int i = 0; i < Math.max( n, 1 ); i++ ) {
							pw.print( ' ' );
						}
						pw.print(  docstring );
					}
					pw.println();
					return false;
				}
			},
			new CmdArgs.NoArgs() 
		);
		pw.flush();
	}
	
	public void processFile( final File file ) {
		//	You should override this if you intend to make use of CmdArg.processFileArgs.
	}
	
}
