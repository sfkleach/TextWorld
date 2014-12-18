package com.steelypip.textworld.gameclasses;

import java.io.PrintWriter;

public abstract class TurnReporter {
	
	PrintWriter print_writer = new PrintWriter( System.out, true );

	public PrintWriter getPrintWriter() {
		return print_writer;
	}

	public void setPrintWriter( PrintWriter print_writer ) {
		this.print_writer = print_writer;
	}

	public void report( final char ch ) {
		this.getPrintWriter().print( ch );
	}

	public void report( final String string ) {
		this.getPrintWriter().print( string );
	}

	public void reportln( final char ch ) {
		this.getPrintWriter().println( ch );
	}

	public void reportln( final String string ) {
		this.getPrintWriter().println( string );
	}


}