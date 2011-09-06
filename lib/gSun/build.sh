#!/bin/bash
#build a jar containing the Calculs class from gsun and its dependences
cd $(dirname $0)

if [ -d "gSun" ]; then
	cd gSun
	git pull
else
	git clone https://github.com/benjaminthorent/gSun.git
fi

cd src

javac com/ei3info/gsun/Calculs.java

jar cf ../../gSun.jar $(ls com/ei3info/gsun/*.class)
