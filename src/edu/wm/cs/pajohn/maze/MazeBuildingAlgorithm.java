package edu.wm.cs.pajohn.maze;

public interface MazeBuildingAlgorithm {

	public abstract void generate(Maze maze, Coordinate startPos, Direction startDir);
	
}
