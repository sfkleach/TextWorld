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

import java.util.*;

/**
 *  A Completor is the mechanism by which tab-completion candidates
 *  will be resolved.
 *  <p>
 *  <strong>TODO:</strong>
 *  <ul>
 *	<li>handle quotes and escaped quotes</li>
 *	<li>enable automatic escaping of whitespace</li>
 *  </ul>
 *
 *  @author  <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public interface Completor {
	/**
	 *  Populates <i>candidates</i> with a list of possible
	 *  completions for the <i>buffer</i>. The <i>candidates</i>
	 *  list will not be sorted before being displayed to the
	 *  user: thus, the complete method should sort the
	 *  {@link List} before returning.
	 *
	 *
	 *  @param  buffer		the buffer
	 *  @param  candidates	the {@link List} of candidates to populate
	 *  @return				the index of the <i>buffer</i> for which
	 *  					the completion will be relative
	 */
	int complete( String buffer, int cursor, List candidates );
}
