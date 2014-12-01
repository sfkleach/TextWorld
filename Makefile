all: help

.PHONEY: jarfile
jarfile: 
	mkdir -p _build
	jar cfe  _build/textworld.jar com.steelypip.textworld.main.Main -C classes/ com -C classes jline

help:
	#	Targets are:
	#		
	#	jarfile: creates the jarfile _build/textfile.jar
