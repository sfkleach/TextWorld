all: help

.PHONEY: jarfile
jarfile: 
	mkdir -p _build
	jar cfe  _build/textworld.jar com.steelypip.textworld.main.Main -C classes/ com -C classes jline

.PHONEY: publish
publish: VERSION:=$(shell cat src/com/steelypip/textworld/main/version.txt)
publish: DATE:=$(shell date "+%Y%m%d.%H%M%S")
publish: FILENAME:=textworld.$(VERSION)+$(DATE).jar
publish: jarfile
	cp _build/textworld.jar ~/Google\ Drive/TextWorld/Builds/$(FILENAME)
	python3 templates/instantiate.py templates/windows/textworld.cmd $(FILENAME) > ~/Google\ Drive/TextWorld/Adventures/textworld.cmd
	python3 templates/instantiate.py templates/unix/textworld $(FILENAME) > ~/Google\ Drive/TextWorld/Adventures/textworld
	chmod a+x ~/Google\ Drive/TextWorld/Adventures/textworld*

help:
	#	Targets are:
	#		
	#	jarfile: creates the jarfile _build/textworld.jar
