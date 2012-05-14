package edu.wm.cs.pajohn.maze;

public class RecursiveBackTrackingAlgorithm implements MazeBuildingAlgorithm {

	private MazeBackTracker backTracker;

	@Override
	public void generate(Maze maze, Coordinate startPos, Direction startDir) {
		backTracker = new RecursiveBackTracker(maze, startPos, startDir);
		backTracker.iterate();
	}

	private class RecursiveBackTracker extends MazeBackTracker {

		public RecursiveBackTracker(Maze maze, Coordinate startPos,
				Direction startDir) {
			super(maze, startPos, startDir);
		}

		@Override
		protected void onVisit() {
			getMaze().removeWall(getPos(), getDir().opposite());
		}

		@Override
		protected void onBacktrack() {

		}

		@Override
		protected Direction nextDir() {
			return Direction.random();
		}

	}

}
