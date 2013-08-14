#!/bin/sh
rm -R edu doc
./compile.sh
javadoc -d doc `find src -name '*.java'`
jar cvfe maze-pajohn.jar edu.wm.cs.pajohn.maze.Maze doc edu src
