package com.steelypip.powerups.shell;

import com.sun.istack.internal.NotNull;

public abstract class CmdOption {
	
	public abstract boolean is( char c, String string, String docstring );

	public boolean is( char c, String string ) {
		return this.is( c, string, "" );
	}
	
	
	
	static public class LongCmdOption extends CmdOption {

		final String long_option;

		
		public LongCmdOption( final @NotNull String s ) {
			assert s != null;
			this.long_option = s;
		}

		public boolean is( char c, String string, String docstring ) {
			return this.long_option.equals(  string );
		}
		
	}

	static public class ShortCmdOption extends CmdOption {
		
		final char short_option;

		public ShortCmdOption( final char ch ) {
			this.short_option = ch;
		}
		
		public boolean is( char c, String string, String docstring ) {
			return c == this.short_option;
		}
	}

}
