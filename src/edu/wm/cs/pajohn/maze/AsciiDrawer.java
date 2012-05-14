package edu.wm.cs.pajohn.maze;

public class AsciiDrawer implements MazeDrawer {

	private Maze maze;
	private BasicRobot robot;
	private boolean drawSolution;
	private MazeSolution path;

	private boolean robotDrawn;
	private boolean xOffset, yOffset;
	private StringBuffer output;

	private int viewWidth = 10;
	private int viewHeight = 10;

	public AsciiDrawer(Maze maze) {
		this(maze, null);
	}

	public AsciiDrawer(Maze maze, BasicRobot robot) {
		this(maze, robot, false);
	}

	public AsciiDrawer(Maze maze, BasicRobot robot, boolean drawSolution) {
		this.maze = maze;
		this.robot = robot;
		this.drawSolution = robot == null ? false : drawSolution;
		if (this.drawSolution) {
			path = new MazeSolution(maze, robot.getPos());
		}
	}

	@Override
	public void resizeView(int viewWidth, int viewHeight) {
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
	}

	@Override
	public void drawMaze() {
		System.out.println(toString());
	}

	@Override
	public String toString() {
		// lock the robot so it doesn't move while we are trying to draw it
		synchronized (robot) {
			// update the path
			if (drawSolution) {
				path.update(robot.getOrigPos());
			}

			// calculate the bounds of the viewing window. if the maze size is
			// smaller than the viewing window, the window is shrunk. otherwise
			// it tries to center the window around the robot, but stops
			// scrolling when the robot approaches an edge
			int x0, x1, y0, y1;
			if (viewWidth > maze.getWidth()) {
				x0 = 0;
				x1 = maze.getWidth();
			} else {
				x0 = (int) (robot.getPos().getX() + 0.25) - viewWidth / 2;
				x1 = x0 + viewWidth;
				if (x0 < 0) {
					x0 = 0;
					x1 = viewWidth;
				} else if (x1 > maze.getWidth()) {
					x1 = maze.getWidth();
					x0 = x1 - viewWidth;
				}
			}
			if (viewHeight > maze.getHeight()) {
				y0 = 0;
				y1 = maze.getHeight();
			} else {
				y0 = (int) (robot.getPos().getY() + 0.25) - viewHeight / 2;
				y1 = y0 + viewHeight;
				if (y0 < 0) {
					y0 = 0;
					y1 = viewHeight;
				} else if (y1 > maze.getHeight()) {
					y1 = maze.getHeight();
					y0 = y1 - viewHeight;
				}
			}

			// calculate whether to offset the view screen for scrolling
			boolean offset = robot.isMoving() && robot.getProgress() > 0.25
					&& robot.getProgress() < 0.75;
			xOffset = offset && robot.getDir().isHorizontal() && x0 > 0
					&& x1 < maze.getWidth();
			yOffset = offset && robot.getDir().isVertical() && y0 > 0
					&& y1 < maze.getHeight();

			// draw the visible portion of the maze
			robotDrawn = false;
			output = new StringBuffer();
			for (int y = y0; y < y1; y++) {
				if (y == y0 && !yOffset) {
					writeOuterLine(y, x0, x1, Direction.NORTH);
				}
				writeInnerLine(y, x0, x1);
				writeOuterLine(y, x0, x1, Direction.SOUTH);
				if (y == y1 - 1 && yOffset) {
					writeInnerLine(y1, x0, x1);
				}
			}
		}
		return output.toString();
	}

	private String writeOuterLine(int y, int x0, int x1, Direction dir) {
		Coordinate c;
		// draw the leftmost corner
		if (!xOffset) {
			output.append(decideCorner(new Coordinate(x0, y), dir,
					Direction.WEST));
		}
		for (int x = x0; x < x1; x++) {
			c = new Coordinate(x, y);
			// draw the upper/lower part of the cell
			output.append(decideString(c, dir).substring(
					x == x0 && xOffset ? 1 : 0));
			// draw subsequent corners
			output.append(decideCorner(c, dir, Direction.EAST));
		}
		output.append("\n");
		return output.toString();
	}

	private void writeInnerLine(int y, int x0, int x1) {
		Coordinate c;
		for (int x = x0; x < x1; x++) {
			c = new Coordinate(x, y);
			// draw the only leftmost edge
			if (x == x0 && !xOffset) {
				output.append(decideString(c, Direction.WEST));
			}
			// draw the center of the cell
			output.append(decideString(c, null).substring(
					x == x0 && xOffset ? 1 : 0));
			// draw all east half of cell
			output.append(decideString(c, Direction.EAST));
		}
		output.append("\n");
	}

	private String decideCorner(Coordinate c, Direction vDir, Direction hDir) {
		// whether there are walls connected to this corner horizontally and
		// vertically
		boolean horizontalWalls = maze.hasWall(c, vDir)
				|| maze.isInBounds(c.translate(hDir))
				&& maze.hasWall(c.translate(hDir), vDir);
		boolean verticalWalls = maze.hasWall(c, hDir)
				|| maze.isInBounds(c.translate(vDir))
				&& maze.hasWall(c.translate(vDir), hDir);
		if (horizontalWalls && verticalWalls) {
			return "+";
		} else if (horizontalWalls) {
			return "-";
		} else if (verticalWalls) {
			return "|";
		} else {
			return " ";
		}
	}

	private String decideString(Coordinate c, Direction dir) {
		double xDiff = Math.abs(c.getX() - robot.getPos().getX());
		double yDiff = Math.abs(c.getY() - robot.getPos().getY());

		// draw the center of cells
		if (dir == null) {

			// draw the robot if it's near the center of the coordinate
			if (!robotDrawn && xDiff <= 0.25 && yDiff <= 0.25) {
				robotDrawn = true;
				return " " + chooseRobotChar() + " ";

				// draw the path if this cell is part of the solution
			} else if (drawSolution && path.contains(c)
					&& !c.equals(robot.getOrigPos())) {
				return " * ";

				// draw empty space
			} else {
				return "   ";
			}

			// draw a wall
		} else if (maze.hasWall(c, dir)) {
			return dir.isVertical() ? "---" : "|";

			// draw the robot if it's between coordinates vertically
		} else if (!robotDrawn && dir.isVertical() && xDiff == 0
				&& yDiff > 0.25 && yDiff < 0.75) {
			robotDrawn = true;
			return " " + chooseRobotChar() + " ";

			// draw the robot if it's between coordinates horizontally
		} else if (!robotDrawn && dir.isHorizontal() && yDiff == 0
				&& xDiff > 0.25 && xDiff < 0.75) {
			robotDrawn = true;
			return "" + chooseRobotChar();

			// this is a mess, but I don't want to touch it now that it works.
			// it draws the solution path, but not directly behind the robot so
			// that it looks like the robot is sort of eating the path like
			// pacman
		} else if (drawSolution
				&& path.contains(c)
				&& path.contains(c.translate(dir))
				&& !(dir == robot.getDir() && c.equals(robot.getOrigPos()) && robot
						.getProgress() >= 0.75)
				&& !(dir == robot.getDir().opposite()
						&& c.translate(dir).equals(robot.getOrigPos()) && robot
						.getProgress() >= 0.75)) {
			return dir.isVertical() ? " * " : "*";

			// otherwise draw empty space
		} else {
			return dir.isVertical() ? "   " : " ";
		}
	}

	private char chooseRobotChar() {
		double angle = robot.getAngle();

		// normalize the angle so it's positive
		while (angle < 0) {
			angle += 2 * Math.PI;
		}

		// box the compass to obtain an index
		int index = (int) (angle / (2 * Math.PI) * 8 + 0.5) % 8;
		return "\u2192\u2197\u2191\u2196\u2190\u2199\u2193\u2198".charAt(index);
	}

}
