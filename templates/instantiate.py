#!/usr/bin/env python3

import string
import sys

def main( template_file, jarfile ):
	with open( template_file, 'r' ) as template_source:
		template = string.Template( template_source.read() )
		instantiated = template.substitute( dict( filename = jarfile ) )
		sys.stdout.write( instantiated )

if __name__ == '__main__':
	main( sys.argv[1], sys.argv[2] )
