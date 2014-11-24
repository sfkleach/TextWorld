package com.steelypip.powerups.shell;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.repeaters.PeekableRepeater;


public abstract class CmdArgs implements PeekableRepeater< String > {
	
	public abstract void check();

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public void clear() {
		while ( this.hasNext() ) {
			this.next();
		}
	}

	public void skip() {
		if ( this.hasNext() ) {
			this.next();
		}
	}

	public String next( String value_if_at_end ) {
		if ( this.hasNext() ) {
			return this.next();
		} else {
			return value_if_at_end;
		}
	}
	
	public String peek( String value_if_at_end ) {
		if ( this.hasNext() ) {
			return peek();
		} else {
			return value_if_at_end;
		}
	}

	public static class NoArgs extends CmdArgs {

		@Override
		public void check() {
			//	All is OK.
		}

		public boolean hasNext() {
			return false;
		}

		public String next() {
			throw new Alert( "Missing argument for command line option" );
		}

		public String peek() {
			throw new Alert( "Missing argument for command line option" );
		}

	}

	
	public static class LimitedOptionalArg extends CmdArgs {
		
		private LinkedList< String > args;

		public LimitedOptionalArg( LinkedList< String > args ) {
			this.args = args;
		}

		public void check() {
			//	All is OK. Does not matter whether or not 1 is taken.
		}

		public boolean hasNext() {
			return args != null && !this.args.isEmpty(); 
		}

		public String next() {
			String answer = this.args.removeFirst();
			if ( answer.startsWith( "-" ) ) {
				//	Violates limited argument restriction.
				throw new Alert( "Missing argument for command line between two options" ).culprit( "Next option", answer );
			}
			this.args = null;	//	And prevent any more being removed. Maximum of 1.
			return answer;
		}

		public String peek() {
			return this.args.getFirst();
		}
		
	}
	
	public static class UnlimitedMandatoryArg extends CmdArgs {
		
		private String value;

		public UnlimitedMandatoryArg( String value ) {
			this.value = value;
		}

		@Override
		public void check() {
			if ( this.value != null ) {
				throw new Alert( "Unused argument to command-line option" ).culprit( "Argument", this.value );
			}
		}

		public boolean hasNext() {
			return this.value != null;
		}

		public String next() {
			if ( this.value == null ) {
				throw new Alert( "Command line option looking for second parameter" );
			}
			String answer = value;
			this.value = null;			//	Prevent repeats.
			return answer;
		}

		public String peek() {
			return this.value;
		}		
		
	}
	
	public static class RestArgs extends CmdArgs {
		
		final private LinkedList< String > args;
		
		public RestArgs( LinkedList< String > args ) {
			this.args = args;
		}
		
		public boolean hasNext() {
			return !this.args.isEmpty();
		}

		public String next() {
			return this.args.removeFirst();
		}

		@Override
		public void check() {
			if ( !this.args.isEmpty() ) {
				Alert a = new Alert( "Unprocessed arguments" );
				for ( String x : args ) {
					a.culprit( "Unused", x  );
				}
				throw a;
			}
		}

		public String peek() {
			return this.args.getFirst();
		}
			
	}

	public void copyTo( List< String > tags ) {
		while ( this.hasNext() ) {
			tags.add( this.next() );
		}
	}
	
	interface FileArgProcessor {
		void processFile( File file );
	}
	
	public final void processFileArgs( FileArgProcessor p ) {
		while ( this.hasNext() ) {
			p.processFile( new File( this.next() ) );
		}
	}

	public List< String > toList() {
		final List< String > result = new ArrayList< String >();
		while ( this.hasNext()){
		    result.add( this.next() );
		}
		return result;
	}


	
}
