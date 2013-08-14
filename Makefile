
compile %:
	javac -d . `find src -name '*.java'`

run %:
	java edu.wm.cs.pajohn.maze.Maze `stty size` $@

jar %:
	$(MAKE) compile
	javadoc -d doc `find src -name '*.java'`
	jar cvfe maze.jar edu.wm.cs.pajohn.maze.Maze doc edu src

.PHONY: compile run jar
