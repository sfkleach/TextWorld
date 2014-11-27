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
import java.net.*;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;


/**
 * A Completor implementation that completes java class names. By default,
 * it scans the java class path to locate all the classes.
 *
 * @author <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class ClassNameCompletor extends SimpleCompletor {

	/**
	 * Complete candidates using all the classes available in the
	 * java <em>CLASSPATH</em>.
	 */
	public ClassNameCompletor() throws IOException {
		this( null );
	}


	public ClassNameCompletor( final SimpleCompletorFilter filter ) throws IOException {
		super( getClassNames(), filter );
		setDelimiter( "." );
	}


	public static String[] getClassNames() throws IOException {
		Set urls = new HashSet();
		for ( ClassLoader loader = ClassNameCompletor.class.getClassLoader();
			loader != null; loader = loader.getParent() ) {
			if ( !( loader instanceof URLClassLoader ) ) {
				continue;
			}

			urls.addAll( Arrays.asList( ( (URLClassLoader)loader ).getURLs() ) );
		}

		// Now add the URL that holds java.lang.String. This is because
		// some JVMs do not report the core classes jar in the list of
		// class loaders.
		Class[] systemClasses = new Class[]{
			String.class,
			javax.swing.JFrame.class
		};
		for ( int i = 0; i < systemClasses.length; i++ ) {
			URL classURL = systemClasses[ i ].getResource( "/"
				+ systemClasses[ i ].getName().replace( '.', '/' ) + ".class" );
			if ( classURL != null ) {
				URLConnection uc = (URLConnection)classURL.openConnection();
				if ( uc instanceof JarURLConnection ) {
					urls.add( ( (JarURLConnection)uc ).getJarFileURL() );
				}
			}
		}


		Set classes = new HashSet();
		for ( Iterator i = urls.iterator(); i.hasNext(); ) {
			URL url = (URL)i.next();
			File file = new File( url.getFile() );
			if ( file.isDirectory() ) {
				Set files = getClassFiles( file.getAbsolutePath(),
					new HashSet(), file, new int[]{200} );
				classes.addAll( files );
				continue;
			}

			if ( file == null || !file.isFile() ) // TODO: handle directories
			{
				continue;
			}

			JarFile jf = new JarFile( file );
			for ( Enumeration entries = jf.entries();
				entries.hasMoreElements(); ) {
				JarEntry entry = (JarEntry)entries.nextElement();
				if ( entry == null ) {
					continue;
				}

				String name = entry.getName();
				if ( !name.endsWith( ".class" ) ) // only use class file
				{
					continue;
				}

				classes.add( name );
			}
		}

		// now filter classes by changing "/" to "." and trimming the
		// trailing ".class"
		Set classNames = new TreeSet();
		for ( Iterator i = classes.iterator(); i.hasNext(); ) {
			String name = (String)i.next();
			classNames.add( name.replace( '/', '.' ).substring( 0,
				name.length() - 6 ) );
		}

		return (String[])classNames.toArray( new String[ classNames.size() ] );
	}


	private static Set getClassFiles( String root, Set holder, File directory,
		int[] maxDirectories ) {
		// we have passed the maximum number of directories to scan
		if ( maxDirectories[ 0 ]-- < 0 ) {
			return holder;
		}

		File[] files = directory.listFiles();
		for ( int i = 0; files != null && i < files.length; i++ ) {
			String name = files[ i ].getAbsolutePath();
			if ( !( name.startsWith( root ) ) ) {
				continue;
			} else if ( files[ i ].isDirectory() ) {
				getClassFiles( root, holder, files[ i ], maxDirectories );
			} else if ( files[ i ].getName().endsWith( ".class" ) ) {
				holder.add( files[ i ].getAbsolutePath().substring( root.length() + 1 ) );
			}
		}

		return holder;
	}
}

