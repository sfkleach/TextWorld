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
 *	<p>
 *	A {@link CompletionHandler} that deals with multiple distinct completions
 *	by cycling through each one every time tab is pressed. This
 *	mimics the behavior of the
 *	<a href="http://packages.qa.debian.org/e/editline.html">editline</a>
 *	library.
 *	</p>
 *	<p><strong>This class is currently a stub; it does nothing</strong></p>
 *
 *  @author  <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class CandidateCycleCompletionHandler implements CompletionHandler {

	public boolean complete( final ConsoleReader reader, final List candidates, final int position ) throws IOException {
		throw new IllegalStateException( "CandidateCycleCompletionHandler unimplemented" );
	}
	
}

