#!/usr/bin/env bash

if [ ! -d "classes" ]; then
	mkdir -p "classes"
fi

find src -name \*.java -print0 | xargs -0 javac -d classes

cp $2 classes
cp $3 classes
cp $4 classes

cd classes

echo "Starting peer $1"
java com.p2p.man.main.Start $1