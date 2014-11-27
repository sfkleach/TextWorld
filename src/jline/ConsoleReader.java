/**
 *	jline - Java console input library
 *	Copyright (c) 2002,2003 Marc Prud'hommeaux mwp1@cornell.edu
 *	
 *	This library is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU Lesser General Public
 *	License as published by the Free Software Foundation; either
 *	version 2.1 of the License, or (at your option) any later version.
 *	
 *	This library is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *	Lesser General Public License for more details.
 *	
 *	You should have received a copy of the GNU Lesser General Public
 *	License along with this library; if not, write to the Free Software
 *	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jline;

import java.io.*;
import java.util.*;
import java.text.MessageFormat;

/**
 * A reader for console applications. It supports custom tab-completion,
 * saveable command history, and command line editing. On some
 * platforms, platform-specific commands will need to be
 * issued before the reader will function properly. See
 * {@link Terminal#initializeTerminal} for convenience methods for
 * issuing platform-specific setup commands.
 *
 * @author <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class ConsoleReader
	implements ConsoleOperations {

	String prompt;

	public static final String CR = System.getProperty( "line.separator" );


	/**
	 * Map that contains the operation name to keymay operation mapping.
	 */
	public static SortedMap KEYMAP_NAMES;

	static {
		Map names = new TreeMap();

		names.put( "MOVE_TO_BEG", new Short( MOVE_TO_BEG ) );
		names.put( "MOVE_TO_END", new Short( MOVE_TO_END ) );
		names.put( "PREV_CHAR", new Short( PREV_CHAR ) );
		names.put( "NEWLINE", new Short( NEWLINE ) );
		names.put( "KILL_LINE", new Short( KILL_LINE ) );
		names.put( "PASTE", new Short( PASTE ) );
		names.put( "CLEAR_SCREEN", new Short( CLEAR_SCREEN ) );
		names.put( "NEXT_HISTORY", new Short( NEXT_HISTORY ) );
		names.put( "PREV_HISTORY", new Short( PREV_HISTORY ) );
		names.put( "REDISPLAY", new Short( REDISPLAY ) );
		names.put( "KILL_LINE_PREV", new Short( KILL_LINE_PREV ) );
		names.put( "DELETE_PREV_WORD", new Short( DELETE_PREV_WORD ) );
		names.put( "NEXT_CHAR", new Short( NEXT_CHAR ) );
		names.put( "REPEAT_PREV_CHAR", new Short( REPEAT_PREV_CHAR ) );
		names.put( "SEARCH_PREV", new Short( SEARCH_PREV ) );
		names.put( "REPEAT_NEXT_CHAR", new Short( REPEAT_NEXT_CHAR ) );
		names.put( "SEARCH_NEXT", new Short( SEARCH_NEXT ) );
		names.put( "PREV_SPACE_WORD", new Short( PREV_SPACE_WORD ) );
		names.put( "TO_END_WORD", new Short( TO_END_WORD ) );
		names.put( "REPEAT_SEARCH_PREV", new Short( REPEAT_SEARCH_PREV ) );
		names.put( "PASTE_PREV", new Short( PASTE_PREV ) );
		names.put( "REPLACE_MODE", new Short( REPLACE_MODE ) );
		names.put( "SUBSTITUTE_LINE", new Short( SUBSTITUTE_LINE ) );
		names.put( "TO_PREV_CHAR", new Short( TO_PREV_CHAR ) );
		names.put( "NEXT_SPACE_WORD", new Short( NEXT_SPACE_WORD ) );
		names.put( "DELETE_PREV_CHAR", new Short( DELETE_PREV_CHAR ) );
		names.put( "ADD", new Short( ADD ) );
		names.put( "PREV_WORD", new Short( PREV_WORD ) );
		names.put( "CHANGE_META", new Short( CHANGE_META ) );
		names.put( "DELETE_META", new Short( DELETE_META ) );
		names.put( "END_WORD", new Short( END_WORD ) );
		names.put( "NEXT_CHAR", new Short( NEXT_CHAR ) );
		names.put( "INSERT", new Short( INSERT ) );
		names.put( "REPEAT_SEARCH_NEXT", new Short( REPEAT_SEARCH_NEXT ) );
		names.put( "PASTE_NEXT", new Short( PASTE_NEXT ) );
		names.put( "REPLACE_CHAR", new Short( REPLACE_CHAR ) );
		names.put( "SUBSTITUTE_CHAR", new Short( SUBSTITUTE_CHAR ) );
		names.put( "TO_NEXT_CHAR", new Short( TO_NEXT_CHAR ) );
		names.put( "UNDO", new Short( UNDO ) );
		names.put( "NEXT_WORD", new Short( NEXT_WORD ) );
		names.put( "DELETE_NEXT_CHAR", new Short( DELETE_NEXT_CHAR ) );
		names.put( "CHANGE_CASE", new Short( CHANGE_CASE ) );
		names.put( "COMPLETE", new Short( COMPLETE ) );
		names.put( "EXIT", new Short( EXIT ) );

		KEYMAP_NAMES = new TreeMap( Collections.unmodifiableMap( names ) );
	}


	/**
	 * The map for logical operations.
	 */
	private final short[] keybindings;


	/**
	 * If true, issue an audible keyboard bell when appropriate.
	 */
	private boolean bellEnabled = true;


	/**
	 * The current character mask.
	 */
	private Character mask = null;


	/**
	 * The null mask.
	 */
	private static final Character NULL_MASK = new Character( (char)0 );


	/**
	 * The number of tab-completion candidates above which a warning
	 * will be prompted before showing all the candidates.
	 */
	private int autoprintThreshhold = Integer.getInteger( "jline.completion.threshold", 100 ).intValue(); // same default as bash


	/**
	 * The Terminal to use.
	 */
	private final Terminal terminal;


	private CompletionHandler completionHandler
		= new CandidateListCompletionHandler();


	InputStream in;
	final Writer out;
	final CursorBuffer buf = new CursorBuffer();
	static PrintWriter debugger;
	History history = new History();
	final List completors = new LinkedList();

	private Character echoCharacter = null;


	/**
	 * Create a new reader using {@link FileDescriptor#in} for input
	 * and {@link System#out} for output. {@link FileDescriptor#in} is
	 * used because it has a better chance of being unbuffered.
	 */
	public ConsoleReader() throws IOException {
		this( new FileInputStream( FileDescriptor.in ), new PrintWriter( System.out ) );
	}


	/**
	 * Create a new reader using the specified {@link InputStream}
	 * for input and the specific writer for output, using the
	 * default keybindings resource.
	 */
	public ConsoleReader( final InputStream in, final Writer out )
		throws IOException {
		this( in, out, null );
	}


	public ConsoleReader( final InputStream in, final Writer out, final InputStream bindings ) throws IOException {
		this( in, out, bindings, Terminal.getTerminal() );
	}


	/**
	 * Create a new reader.
	 *
	 * @param in       the input
	 * @param out      the output
	 * @param bindings the key bindings to use
	 * @param term     the terminal to use
	 */
	public ConsoleReader( InputStream in, Writer out, InputStream bindings, Terminal term ) throws IOException {
		this.terminal = term;
		setInput( in );
		this.out = out;
		if ( bindings == null ) {
			String bindingFile = (
				System.getProperty(
					"jline.keybindings",
					new File( System.getProperty( "user.home", ".jlinebindings.properties" ) ).getAbsolutePath()
				)
			);

			if ( !( new File( bindingFile ).isFile() ) ) {
				bindings = ConsoleReader.class.getResourceAsStream( "keybindings.properties" );
			} else {
				bindings = new FileInputStream( new File( bindingFile ) );
			}
		}

		this.keybindings = new short[ Byte.MAX_VALUE * 2 ];

		Arrays.fill( this.keybindings, UNKNOWN );

		/**
		 *	Loads the key bindings. Bindings file is in the format:
		 *
		 *	keycode: operation name
		 */
		if ( bindings != null ) {
			Properties p = new Properties();
			p.load( bindings );
			bindings.close();

			for ( Iterator i = p.keySet().iterator(); i.hasNext(); ) {
				String val = (String)i.next();
				try {
					Short code = new Short( val );
					String op = (String)p.getProperty( val );

					Short opval = (Short)KEYMAP_NAMES.get( op );

					if ( opval != null ) {
						keybindings[ code.shortValue() ] = opval.shortValue();
					}
				} catch ( NumberFormatException nfe ) {
					consumeException( nfe );
				}
			}
		}


		/**
		 *	Perform unmodifiable bindings.
		 */
		keybindings[ ARROW_START ] = ARROW_START;
	}


	public Terminal getTerminal() {
		return this.terminal;
	}

	/**
	 * Set the stream for debugging. Development use only.
	 */
	public void setDebug( final PrintWriter debugger ) {
		this.debugger = debugger;
	}


	/**
	 * Set the stream to be used for console input.
	 */
	public void setInput( final InputStream in ) {
		this.in = in;
	}


	/**
	 * Returns the stream used for console input.
	 */
	public InputStream getInput() {
		return this.in;
	}


	/**
	 * Read the next line and return the contents of the buffer.
	 */
	public String readLine()
		throws IOException {
		return readLine( (String)null );
	}


	/**
	 * Read the next line with the specified character mask. If null, then
	 * characters will be echoed. If 0, then no characters will be echoed.
	 */
	public String readLine( final Character mask )
		throws IOException {
		return readLine( null, mask );
	}


	/**
	 * @param bellEnabled if true, enable audible keyboard bells if
	 *                    an alert is required.
	 */
	public void setBellEnabled( final boolean bellEnabled ) {
		this.bellEnabled = bellEnabled;
	}


	/**
	 * @return true is audible keyboard bell is enabled.
	 */
	public boolean getBellEnabled() {
		return this.bellEnabled;
	}


	/**
	 * Query the terminal to find the current width;
	 *
	 * @return the width of the current terminal.
	 * @see	 Terminal#getTerminalWidth
	 */
	public int getTermwidth() {
		return Terminal.setupTerminal().getTerminalWidth();
	}


	/**
	 * Query the terminal to find the current width;
	 *
	 * @return the height of the current terminal.
	 * @see	 Terminal#getTerminalHeight
	 */
	public int getTermheight() {
		return Terminal.setupTerminal().getTerminalHeight();
	}


	/**
	 * @param autoprintThreshhold the number of candidates to print
	 *                            without issuing a warning.
	 */
	public void setAutoprintThreshhold( final int autoprintThreshhold ) {
		this.autoprintThreshhold = autoprintThreshhold;
	}


	/**
	 * @return the number of candidates to print without issing a warning.
	 */
	public int getAutoprintThreshhold() {
		return this.autoprintThreshhold;
	}


	int getKeyForAction( short logicalAction ) {
		for ( int i = 0; i < keybindings.length; i++ ) {
			if ( keybindings[ i ] == logicalAction ) {
				return i;
			}
		}

		return -1;
	}


	/**
	 * Clear the echoed characters for the specified character code.
	 */
	int clearEcho( int c ) throws IOException {
		// if the terminal is not echoing, then just return...
		if ( !terminal.getEcho() ) {
			return 0;
		}

		// otherwise, clear
		int num = countEchoCharacters( (char)c );
		back( num );
		drawBuffer( num );

		return num;
	}


	int countEchoCharacters( char c ) {
		// tabs as special: we need to determine the number of spaces
		// to cancel based on what out current cursor position is
		if ( c == 9 ) {
			int tabstop = 8; // will this ever be different?
			int position = getCursorPosition();
			return tabstop - ( position % tabstop );
		}

		return getPrintableCharacters( c ).length();
	}


	/**
	 * Return the number of characters that will be printed when the
	 * specified character is echoed to the screen. Adapted from
	 * cat by Torbjorn Granlund, as repeated in stty by
	 * David MacKenzie.
	 */
	StringBuffer getPrintableCharacters( char ch ) {
		StringBuffer sbuff = new StringBuffer();
		if ( ch >= 32 ) {
			if ( ch < 127 ) {
				sbuff.append( ch );
			} else if ( ch == 127 ) {
				sbuff.append( '^' );
				sbuff.append( '?' );
			} else {
				sbuff.append( 'M' );
				sbuff.append( '-' );
				if ( ch >= 128 + 32 ) {
					if ( ch < 128 + 127 ) {
						sbuff.append( (char)( ch - 128 ) );
					} else {
						sbuff.append( '^' );
						sbuff.append( '?' );
					}
				} else {
					sbuff.append( '^' );
					sbuff.append( (char)( ch - 128 + 64 ) );
				}
			}
		} else {
			sbuff.append( '^' );
			sbuff.append( (char)( ch + 64 ) );
		}

		return sbuff;
	}


	int getCursorPosition() {
		// FIXME: does not handle anything but a line with a prompt
		return ( prompt == null ? 0 : prompt.length() ) + buf.cursor; // absolute position
	}


	public String readLine( final String prompt )
		throws IOException {
		return readLine( prompt, null );
	}


	/**
	 * Read a line from the <i>in</i> {@link InputStream}, and
	 * return the line (without any trailing newlines).
	 *
	 * @param prompt the prompt to issue to the console, may be null.
	 * @return	a line that is read from the terminal, or null if there
	 * was null input (e.g., <i>CTRL-D</i> was pressed).
	 */
	public String readLine( final String prompt, final Character mask )
		throws IOException {
		this.mask = mask;
		this.prompt = prompt;

		if ( prompt != null && prompt.length() > 0 ) {
			out.write( prompt );
			out.flush();
		}

		// if the terminal is unsupported, just use plain-java reading
		if ( !terminal.isSupported() ) {
			//	Original line is buggy as reported by perfecthash.  Replacement by Steve Leach.
//			return new BufferedReader( new InputStreamReader( in ) ).readLine();
			final Reader r = new InputStreamReader( in );
			final StringBuffer b = new StringBuffer();
			for (;;) {
				final int ich = r.read();
				if ( ich == -1 ) return b.length() == 0 ? null : b.toString();;
				//	Note that the end-of-line is allowed to accumulate.
				b.append( (char)ich );
				//	Now to detect the end of line condition.
				final int n = b.indexOf( CR );
				if ( n >= 0 ) {
					//	Strip off the end of line & break.
					b.setLength( n );
					break;
				}
			}
			return b.toString();
		}

		int c;

		while ( true ) {
			if ( ( c = readCharacter() ) == -1 ) {
				return null;
			}

			boolean success = true;

			// extract the appropriate key binding
			short code = keybindings[ c ];

			if ( debugger != null ) {
				debug( "    translated: " + (int)c + ": " + code );
			}

			switch ( code ) {
				case EXIT: // ctrl-d
					if ( buf.buffer.length() == 0 ) {
						return null;
					}
				case COMPLETE: // tab
					success = complete();
					break;
				case MOVE_TO_BEG:
					success = setCursorPosition( 0 );
					break;
				case KILL_LINE: // CTRL-K
					success = killLine();
					break;
				case KILL_LINE_PREV: // CTRL-U
					success = resetLine();
					break;
				case ARROW_START:
					// debug ("ARROW_START");

					switch ( c = readCharacter() ) {
						case ARROW_PREFIX:
							// debug ("ARROW_PREFIX");

							switch ( c = readCharacter() ) {
								case ARROW_LEFT: // left arrow
									// debug ("LEFT");
									success = moveCursor( -1 ) != 0;
									break;
								case ARROW_RIGHT: // right arrow
									// debug ("RIGHT");
									success = moveCursor( 1 ) != 0;
									break;
								case ARROW_UP: // up arrow
									// debug ("UP");
									success = moveHistory( false );
									break;
								case ARROW_DOWN: // down arrow
									// debug ("DOWN");
									success = moveHistory( true );
									break;
								default:
									break;

							}
							break;
						default:
							break;
					}
					break;
				case NEWLINE: // enter
					printNewline(); // output newline
					return finishBuffer();
				case DELETE_PREV_CHAR: // backspace
					success = backspace();
					break;
				case MOVE_TO_END:
					success = moveToEnd();
					break;
				case PREV_CHAR:
					success = moveCursor( -1 ) != 0;
					break;
				case NEXT_CHAR:
					success = moveCursor( 1 ) != 0;
					break;
				case NEXT_HISTORY:
					success = moveHistory( true );
					break;
				case PREV_HISTORY:
					success = moveHistory( false );
					break;
				case REDISPLAY:
					break;
				case PASTE:
					success = paste();
					break;
				case DELETE_PREV_WORD:
					success = deletePreviousWord();
					break;
				case PREV_WORD:
					success = previousWord();
					break;
				case NEXT_WORD:
					success = nextWord();
					break;

				case UNKNOWN:
				default:
					putChar( c, true );
			}

			if ( !( success ) ) {
				beep();
			}

			flushConsole();
		}
	}


	/**
	 * Move up or down the history tree.
	 */
	private final boolean moveHistory( final boolean next )
		throws IOException {
		if ( next && !history.next() ) {
			return false;
		} else if ( !next && !history.previous() ) {
			return false;
		}

		setBuffer( history.current() );
		return true;
	}


	/**
	 * Paste the contents of the clipboard into the console buffer
	 *
	 * @return true if clipboard contents pasted
	 */
	public boolean paste()
		throws IOException {
		java.awt.datatransfer.Clipboard clipboard
			= java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
		if ( clipboard == null ) {
			return false;
		}

		java.awt.datatransfer.Transferable transferable
			= clipboard.getContents( null );

		if ( transferable == null ) {
			return false;
		}

		try {
			Object content = transferable.getTransferData( java.awt.datatransfer.DataFlavor.plainTextFlavor );

			if ( content == null ) {
				return false;
			}

			String value;

			if ( content instanceof Reader ) {
				// TODO: we might want instead connect to the input stream
				// so we can interpret individual lines
				value = "";
				String line = null;
				for ( BufferedReader read = new BufferedReader( (Reader)content );
					( line = read.readLine() ) != null; ) {
					if ( value.length() > 0 ) {
						value += "\n";
					}

					value += line;
				}
			} else {
				value = content.toString();
			}


			if ( value == null ) {
				return true;
			}

			putString( value );

			return true;
		} catch ( java.awt.datatransfer.UnsupportedFlavorException ufe ) {
			ufe.printStackTrace();
			return false;
		}
	}


	/**
	 * Kill the buffer ahead of the current cursor position.
	 *
	 * @return true if successful
	 */
	public boolean killLine()
		throws IOException {
		int cp = buf.cursor;
		int len = buf.buffer.length();
		if ( cp >= len ) {
			return false;
		}

		int num = buf.buffer.length() - cp;
		clearAhead( num );
		for ( int i = 0; i < num; i++ ) {
			buf.buffer.deleteCharAt( len - i - 1 );
		}
		return true;
	}


	/**
	 * Use the completors to modify the buffer with the
	 * appropriate completions.
	 *
	 * @return true if successful
	 */
	private final boolean complete() throws IOException {
		// debug ("tab for (" + buf + ")");

		if ( completors.size() == 0 ) {
			return false;
		} else {

			List candidates = new LinkedList();
			String bufstr = buf.buffer.toString();
			int cursor = buf.cursor;

			int position = -1;

			for ( Iterator i = completors.iterator(); i.hasNext(); ) {
				Completor comp = (Completor)i.next();
				if ( ( position = comp.complete( bufstr, cursor, candidates ) ) != -1 ) {
					break;
				}
			}

			// no candidates? Fail.
			if ( candidates.size() == 0 ) {
				return false;
			}

			//	Sort and eliminate duplicates before passing on.
			return completionHandler.complete( this, new ArrayList( new TreeSet( candidates ) ), position );
		}
	}


	public CursorBuffer getCursorBuffer() {
		return buf;
	}


	/**
	 * Output the specified {@link Collection} in proper columns.
	 *
	 * @param stuff the stuff to print
	 */
	public void printColumns( final Collection stuff )
		throws IOException {
		if ( stuff == null || stuff.size() == 0 ) {
			return;
		}

		int width = getTermwidth();
		int maxwidth = 0;
		for ( Iterator i = stuff.iterator(); i.hasNext();
			maxwidth = Math.max( maxwidth, i.next().toString().length() ) ) {
			;
		}

		StringBuffer line = new StringBuffer();

		for ( Iterator i = stuff.iterator(); i.hasNext(); ) {
			String cur = (String)i.next();

			if ( line.length() + maxwidth > width ) {
				printString( line.toString().trim() );
				printNewline();
				line.setLength( 0 );
			}

			pad( cur, maxwidth + 3, line );
		}

		if ( line.length() > 0 ) {
			printString( line.toString().trim() );
			printNewline();
			line.setLength( 0 );
		}
	}


	/**
	 * Append <i>toPad</i> to the specified <i>appendTo</i>, as
	 * well as (<i>toPad.length () - len</i>) spaces.
	 *
	 * @param toPad    the {@link String} to pad
	 * @param len      the target length
	 * @param appendTo the {@link StringBuffer} to which to append the
	 *                 padded {@link String}.
	 */
	private final void pad( final String toPad,
		final int len, final StringBuffer appendTo ) {
		appendTo.append( toPad );
		for ( int i = 0; i < ( len - toPad.length() );
			i++, appendTo.append( ' ' ) ) {
			;
		}
	}


	/**
	 * Add the specified {@link Completor} to the list of handlers
	 * for tab-completion.
	 *
	 * @param completor the {@link Completor} to add
	 * @return	true if it was successfully added
	 */
	public boolean addCompletor( final Completor completor ) {
		return completors.add( completor );
	}


	/**
	 * Remove the specified {@link Completor} from the list of handlers
	 * for tab-completion.
	 *
	 * @param completor the {@link Completor} to remove
	 * @return	true if it was successfully removed
	 */
	public boolean removeCompletor( final Completor completor ) {
		return completors.remove( completor );
	}


	/**
	 * Returns an unmodifiable list of all the completors.
	 */
	public Collection getCompletors() {
		return Collections.unmodifiableList( completors );
	}


	/**
	 * Erase the current line.
	 *
	 * @return false if we failed (e.g., the buffer was empty)
	 */
	final boolean resetLine()
		throws IOException {
		if ( buf.cursor == 0 ) {
			return false;
		}

		backspaceAll();

		return true;
	}


	/**
	 * Move the cursor position to the specified absolute index.
	 */
	public final boolean setCursorPosition( final int position ) throws IOException {
		return moveCursor( position - buf.cursor ) != 0;
	}


	/**
	 * Set the current buffer's content to the specified
	 * {@link String}. The visual console will be modified
	 * to show the current buffer.
	 *
	 * @param buffer the new contents of the buffer.
	 */
	private final void setBuffer( final String buffer )
		throws IOException {
		// don't bother modifying it if it is unchanged
		if ( buffer.equals( buf.buffer.toString() ) ) {
			return;
		}

		// obtain the difference between the current buffer and the new one
		int sameIndex = 0;
		for ( int i = 0, l1 = buffer.length(), l2 = buf.buffer.length();
			i < l1 && i < l2; i++ ) {
			if ( buffer.charAt( i ) == buf.buffer.charAt( i ) ) {
				sameIndex++;
			} else {
				break;
			}
		}

		int diff = buf.buffer.length() - sameIndex;

		backspace( diff ); // go back for the differences
		killLine(); // clear to the end of the line
		buf.buffer.setLength( sameIndex ); // the new length
		putString( buffer.substring( sameIndex ) ); // append the differences
	}


	/**
	 * Clear the line and redraw it.
	 */
	public final void redrawLine()
		throws IOException {
		printCharacter( RESET_LINE );
		flushConsole();
		drawLine();
	}


	/**
	 * Output put the prompt + the current buffer
	 */
	public final void drawLine()
		throws IOException {
		if ( prompt != null ) {
			printString( prompt );
		}
		printString( buf.buffer.toString() );
	}


	/**
	 * Output a platform-dependant newline.
	 */
	public final void printNewline() throws IOException {
		printString( CR );
		flushConsole();
	}


	/**
	 * Clear the buffer and add its contents to the history.
	 *
	 * @return the former contents of the buffer.
	 */
	final String finishBuffer() {
		String str = buf.buffer.toString();

		// we only add it to the history if the buffer is not empty
		if ( str.length() > 0 ) {
			history.addToHistory( str );
		}

		history.moveToEnd();

		buf.buffer.setLength( 0 );
		buf.cursor = 0;
		return str;
	}


	/**
	 * Write out the specified string to the buffer and the
	 * output stream.
	 */
	public final void putString( final String str ) throws IOException {
		buf.insert( str );
		printString( str );
		drawBuffer();
	}


	/**
	 * Output the specified string to the output stream (but not the
	 * buffer).
	 */
	public final void printString( final String str ) throws IOException {
		printCharacters( str.toCharArray() );
	}


	/**
	 * Output the specified character, both to the buffer
	 * and the output stream.
	 */
	private final void putChar( final int c, final boolean print )
		throws IOException {
		buf.insert( (char)c );

		if ( print ) {
			// no masking...
			if ( mask == null ) {
				printCharacter( c );
			}
			// null mask: don't print anything...
			else if ( mask.charValue() == 0 ) {
				;
			}
			// otherwise print the mask...
			else {
				printCharacter( mask.charValue() );
			}
			drawBuffer();
		}
	}


	/**
	 * Redraw the rest of the buffer from the cursor onwards. This
	 * is necessary for inserting text into the buffer.
	 *
	 * @param clear the number of characters to clear after the
	 *              end of the buffer
	 */
	private final void drawBuffer( final int clear ) throws IOException {
		// debug ("drawBuffer: " + clear);

		char[] chars = buf.buffer.substring( buf.cursor ).toCharArray();
		printCharacters( chars );

		clearAhead( clear );
		back( chars.length );
		flushConsole();
	}


	/**
	 * Redraw the rest of the buffer from the cursor onwards. This
	 * is necessary for inserting text into the buffer.
	 */
	private final void drawBuffer() throws IOException {
		drawBuffer( 0 );
	}


	/**
	 * Clear ahead the specified number of characters
	 * without moving the cursor.
	 */
	private final void clearAhead( final int num )
		throws IOException {
		if ( num == 0 ) {
			return;
		}

		// debug ("clearAhead: " + num);

		// print blank extra characters
		printCharacters( ' ', num );

		// we need to flush here so a "clever" console
		// doesn't just ignore the redundancy of a space followed by
		// a backspace.
		flushConsole();

		// reset the visual cursor
		back( num );

		flushConsole();
	}


	/**
	 * Move the visual cursor backwards without modifying the
	 * buffer cursor.
	 */
	private final void back( final int num )
		throws IOException {
		printCharacters( BACKSPACE, num );
		flushConsole();
	}


	/**
	 * Issue an audible keyboard bell, if
	 * {@link #getBellEnabled} return true.
	 */
	public final void beep()
		throws IOException {
		if ( !( getBellEnabled() ) ) {
			return;
		}

		printCharacter( KEYBOARD_BELL );
		// need to flush so the console actually beeps
		flushConsole();
	}


	/**
	 * Output the specified character to the output stream
	 * without manipulating the current buffer.
	 */
	private final void printCharacter( final int c )
		throws IOException {
		out.write( c );
	}


	/**
	 * Output the specified characters to the output stream
	 * without manipulating the current buffer.
	 */
	private final void printCharacters( final char[] c ) throws IOException {
		out.write( c );
	}


	private final void printCharacters( final char c, final int num )
		throws IOException {
		if ( num == 1 ) {
			printCharacter( c );
		} else {
			char[] chars = new char[ num ];
			Arrays.fill( chars, c );
			printCharacters( chars );
		}
	}


	/**
	 * Flush the console output stream. This is important for
	 * printout out single characters (like a backspace or keyboard)
	 * that we want the console to handle immedately.
	 */
	public final void flushConsole() throws IOException {
		out.flush();
	}


	private final int backspaceAll()
		throws IOException {
		return backspace( Integer.MAX_VALUE );
	}


	/**
	 * Issue <em>num</em> backspaces.
	 *
	 * @return the number of characters backed up
	 */
	private final int backspace( final int num ) throws IOException {
		if ( buf.cursor == 0 ) {
			return 0;
		} else {
			int count = 0;
			count = moveCursor( -1 * num ) * -1;
			// debug ("Deleting from " + buf.cursor + " for " + count);
			buf.buffer.delete( buf.cursor, buf.cursor + count );
			drawBuffer( count );
			return count;
		}
	}


	/**
	 * Issue a backspace.
	 *
	 * @return true if successful
	 */
	public final boolean backspace() throws IOException {
		return this.backspace( 1 ) == 1;
	}


	private final boolean moveToEnd()
		throws IOException {
		if ( moveCursor( 1 ) == 0 ) {
			return false;
		}

		while ( moveCursor( 1 ) != 0 ) {
			;
		}

		return true;
	}


	/**
	 * Delete the character at the current position and
	 * redraw the remainder of the buffer.
	 */
	private final boolean deleteCurrentCharacter()
		throws IOException {
		buf.buffer.deleteCharAt( buf.cursor );
		drawBuffer( 1 );
		return true;
	}


	private final boolean previousWord()
		throws IOException {
		while ( isDelimiter( buf.current() ) && moveCursor( -1 ) != 0 ) {
			;
		}
		while ( !isDelimiter( buf.current() ) && moveCursor( -1 ) != 0 ) {
			;
		}

		return true;
	}


	private final boolean nextWord()
		throws IOException {
		while ( isDelimiter( buf.current() ) && moveCursor( 1 ) != 0 ) {
			;
		}
		while ( !isDelimiter( buf.current() ) && moveCursor( 1 ) != 0 ) {
			;
		}

		return true;
	}


	private final boolean deletePreviousWord()
		throws IOException {
		while ( isDelimiter( buf.current() ) && backspace() ) {
			;
		}
		while ( !isDelimiter( buf.current() ) && backspace() ) {
			;
		}

		return true;
	}


	/**
	 * Move the cursor <i>where</i> characters.
	 *
	 * @param num if less than 0, move abs(<i>where</i>) to the left,
	 *            otherwise move <i>where</i> to the right.
	 * @return the number of spaces we moved
	 */
	private final int moveCursor( final int num ) throws IOException {
		int where = num;
		if ( buf.cursor == 0 && where < 0 ) {
			return 0;
		}

		if ( buf.cursor == buf.buffer.length() && where > 0 ) {
			return 0;
		}

		if ( buf.cursor + where < 0 ) {
			where = -buf.cursor;
		} else if ( buf.cursor + where > buf.buffer.length() ) {
			where = buf.buffer.length() - buf.cursor;
		}

		moveInternal( where );
		return where;
	}


	/**
	 * debug.
	 *
	 * @param str the message to issue.
	 */
	public static void debug( final String str ) {
		if ( debugger != null ) {
			debugger.println( str );
			debugger.flush();
		}
	}


	/**
	 * Move the cursor <i>where</i> characters, withough checking
	 * the current buffer.
	 *
	 * @param where the number of characters to move to the right or left.
	 */
	private final void moveInternal( final int where )
		throws IOException {
		// debug ("move cursor " + where + " ("
		// + buf.cursor + " => " + (buf.cursor + where) + ")");

		buf.cursor += where;

		char c;

		if ( where < 0 ) {
			c = BACKSPACE;
		} else if ( buf.cursor == 0 ) {
			return;
		} else {
			c = buf.buffer.charAt( buf.cursor - 1 ); // draw replacement
		}

		// null character mask: don't output anything
		if ( NULL_MASK.equals( mask ) ) {
			return;
		}

		printCharacters( c, Math.abs( where ) );
	}


	/**
	 * Read a character from the console.
	 *
	 * @return the character, or -1 if an EOF is received.
	 */
	public final int readCharacter()
		throws IOException {
		int c = terminal.readCharacter( in );

		if ( debugger != null ) {
			debug( "keystroke: " + c + "" );
		}

		// clear any echo characters
		clearEcho( c );

		return c;
	}


	public final int readCharacter( final char[] allowed ) throws IOException {

		// if we restrict to a limited set and the current character
		// is not in the set, then try again.
		char c;

		Arrays.sort( allowed ); // always need to sort before binarySearch
		while ( Arrays.binarySearch( allowed, c = (char)readCharacter() ) == -1 ) {
			//	Skip.
		}

		return c;
	}


	public void setHistory( final History history ) {
		this.history = history;
	}


	public History getHistory() {
		return this.history;
	}


	public void setCompletionHandler( final CompletionHandler completionHandler ) {
		this.completionHandler = completionHandler;
	}


	public CompletionHandler getCompletionHandler() {
		return this.completionHandler;
	}


	/**
	 * <p/>
	 * Set the echo character. For example, to have "*" entered
	 * when a password is typed:
	 * </p>
	 * <p/>
	 * <pre>
	 *    myConsoleReader.setEchoCharacter (new Character ('*'));
	 * 	</pre>
	 * <p/>
	 * <p/>
	 * Setting the character to <pre>null</pre> will restore normal
	 * character echoing. Setting the character to
	 * <pre>new Character (0)</pre> will cause nothing to be echoed.
	 * </p>
	 *
	 * @param echoCharacter the character to echo to the console in
	 *                      place of the typed character.
	 */
	public void setEchoCharacter( final Character echoCharacter ) {
		this.echoCharacter = echoCharacter;
	}


	/**
	 * Returns the echo character.
	 */
	public Character getEchoCharacter() {
		return this.echoCharacter;
	}


	/**
	 * No-op for exceptions we want to silently consume.
	 */
	private void consumeException( final Throwable e ) {
	}


	/**
	 * Checks to see if the specified character is a delimiter. We
	 * consider a character a delimiter if it is anything but a letter or
	 * digit.
	 *
	 * @param c the character to examples
	 * @return		true if it is a delimiter
	 */
	private boolean isDelimiter( char c ) {
		return !Character.isLetterOrDigit( c );
	}
}

