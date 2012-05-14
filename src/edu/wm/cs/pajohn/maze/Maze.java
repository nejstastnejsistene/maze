package edu.wm.cs.pajohn.maze;

import java.util.HashSet;

public class Maze {

	private int width;
	private int height;
	private short[][] cells;
	private int[][] distances;
	private boolean hasRooms = false;

	protected Maze(int width, int height) throws MazeException {
		this.width = width;
		this.height = height;
		cells = new short[height][width];
		addRoom(0, 0, width, height, true);
		//addRoom(2, 2, width - 2, height - 2);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isInBounds(Coordinate c) {
		double x = c.getX(), y = c.getY();
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	private boolean overLapsRoom(int x0, int y0, int x1, int y1) {
		for (int y = y0; y < y1; y++) {
			for (int x = x0; x < x1; x++) {
				if (getCell(new Coordinate(x, y)).isInRoom()) {
					return true;
				}
			}
		}
		return false;
	}

	public void addRoom(int x0, int y0, int x1, int y1) throws MazeException {
		addRoom(x0, y0, x1, y1, false);
	}

	private void addRoom(int x0, int y0, int x1, int y1, boolean init)
			throws MazeException {
		if (!init) {
			hasRooms = true;
		}
		Coordinate c;
		for (int y = y0; y < y1; y++) {
			for (int x = x0; x < x1; x++) {
				c = new Coordinate(x, y);
				if (isInRoom(c)) {
					throw new MazeException("rooms cannot overlap");
				}
				for (Direction dir : Direction.values()) {
					setWall(c, dir, init);
				}
				if (!init) {
					getCell(c).setInRoom(true);
				}
				if (x == x0) {
					if (init)
						addBoundary(c, Direction.WEST);
					addWall(c, Direction.WEST);
				} else if (x == x1 - 1) {
					addBoundary(c, Direction.EAST);
					addWall(c, Direction.EAST);
				}
				if (y == y0) {
					addBoundary(c, Direction.NORTH);
					addWall(c, Direction.NORTH);
				} else if (y == y1 - 1) {
					addBoundary(c, Direction.SOUTH);
					addWall(c, Direction.SOUTH);
				}
			}
		}
	}

	private Cell getCell(Coordinate c) {
		return new Cell(this, c);
	}

	public boolean isInRoom(Coordinate c) {
		return getCell(c).isInRoom();
	}

	public boolean hasBoundary(Coordinate c, Direction dir) {
		return getCell(c).hasBoundary(dir);
	}

	public void addBoundary(Coordinate c, Direction dir) {
		setWall(c, dir, true);
		getCell(c).setBoundary(dir, true);
		c = c.translate(dir);
		if (isInBounds(c)) {
			getCell(c).setBoundary(dir.opposite(), true);
		}
	}

	public boolean hasWall(Coordinate c, Direction dir) {
		return getCell(c).hasWall(dir);
	}

	private void setWall(Coordinate c, Direction dir, boolean value) {
		getCell(c).setWall(dir, value);
		c = c.translate(dir);
		if (isInBounds(c)) {
			getCell(c).setWall(dir.opposite(), value);
		}
	}

	public void addWall(Coordinate c, Direction dir) {
		setWall(c, dir, true);
	}

	public void removeWall(Coordinate c, Direction dir) {
		setWall(c, dir, false);
	}

	private Coordinate calcExit() throws MazeException {
		Coordinate c, exit = null;
		boolean multipleExits = false;
		for (int x = 0; x < width; x++) {
			c = new Coordinate(x, 0);
			if (!getCell(c).hasWall(Direction.NORTH)) {
				multipleExits = exit != null;
				exit = c;
			}
			c = new Coordinate(x, height - 1);
			if (!getCell(c).hasWall(Direction.SOUTH)) {
				multipleExits = exit != null;
				exit = c;
			}
		}
		for (int y = 0; y < height; y++) {
			c = new Coordinate(0, y);
			if (!getCell(c).hasWall(Direction.WEST)) {
				multipleExits = exit != null;
				exit = c;
			}
			c = new Coordinate(width - 1, y);
			if (!getCell(c).hasWall(Direction.EAST)) {
				multipleExits = exit != null;
				exit = c;
			}
		}
		if (multipleExits) {
			throw new MazeException("multiple exits to maze");
		}
		return exit;
	}

	public void calculateDistances() throws MazeException {
		distances = new int[height][width];
		DistanceCounter counter = new DistanceCounter(this, calcExit());
		if (hasRooms) {
			counter.dijkstra();
		} else {
			counter.naive();
		}
	}

	public int getDistance(Coordinate c) {
		c.assertIntValued();
		return distances[(int) c.getY()][(int) c.getX()];
	}

	private void setDistance(Coordinate c, int distance) {
		c.assertIntValued();
		distances[(int) c.getY()][(int) c.getX()] = distance;
	}

	public Coordinate calcStartPos() {
		int max = 0;
		Coordinate maxPos = null;
		int dist = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				dist = distances[y][x];
				if (dist > max) {
					max = dist;
					maxPos = new Coordinate(x, y);
				}
			}
		}
		return maxPos;
	}

	public int calcMaxDistance() {
		int max = 0;
		int dist = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				dist = distances[y][x];
				max = dist > max ? dist : max;
			}
		}
		return max;
	}

	@Override
	public String toString() {
		return new AsciiDrawer(this).toString();
	}

	private class Cell {

		private static final short IN_ROOM = 1 << 9;

		private Maze maze;
		private int x, y;

		public Cell(Maze maze, Coordinate c) {
			c.assertIntValued();
			this.maze = maze;
			this.x = (int) c.getX();
			this.y = (int) c.getY();
		}

		private boolean getMask(int mask) {
			return (maze.cells[y][x] & mask) != 0;
		}

		private void setMask(int mask, boolean value) {
			maze.cells[y][x] &= ~mask;
			if (value) {
				maze.cells[y][x] |= mask;
			}
		}

		public boolean hasWall(Direction dir) {
			return getMask(1 << dir.ordinal());
		}

		public void setWall(Direction dir, boolean value) {
			setMask(1 << dir.ordinal(), value);
		}

		public boolean hasBoundary(Direction dir) {
			return getMask(16 << dir.ordinal());
		}

		public void setBoundary(Direction dir, boolean value) {
			setMask(16 << dir.ordinal(), value);
		}

		public boolean isInRoom() {
			return getMask(IN_ROOM);
		}

		public void setInRoom(boolean value) {
			setMask(IN_ROOM, value);
		}

	}

	public static void main(String[] argv) {
		int width;
		int height;
		if (argv.length < 2) {
			System.out.println("Using default dimension (10x10)");
			width = 10;
			height = 10;
		} else {
			width = (Integer.valueOf(argv[1]) - 1) / 4;
			height = (Integer.valueOf(argv[0]) - 3) / 2;
		}
		System.out.println(width + "," + height);
		try {
			MazeBuilder builder = new MazeBuilder(
					new RecursiveBackTrackingAlgorithm());
			builder.buildMaze(width, height);
			Maze maze = builder.getMaze();
			Coordinate startPos = maze.calcStartPos();
			Direction startDir = null;
			for (Direction dir : Direction.values()) {
				if (!maze.hasWall(startPos, dir)) {
					startDir = dir;
					break;
				}
			}
			Robot robot = new WizardRobot(maze, startPos, startDir);
			robot.setSpeed(1);
			MazeDrawer drawer = new AsciiDrawer(maze, robot, true);
			drawer.resizeView(width, height);
			robot.solveMaze();
			while (!robot.isFinished()) {
				drawer.drawMaze();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println(robot.getBattery() > 0 ? "Gratulace!"
					: "Game Over!");
			drawer.drawMaze();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class DistanceCounter extends MazeBackTracker {

		private Coordinate startPos;
		private int infinity;

		private int lastDistance;

		public DistanceCounter(Maze maze, Coordinate startPos) {
			super(maze, startPos);
			this.startPos = startPos;
			infinity = width * height;
			setDistance(startPos, 1);
			lastDistance = 1;
		}

		private void naive() throws MazeException {
			iterate();
			if (!allVisited()) {
				throw new MazeException("not all cells were visited");
			}
		}

		@Override
		protected boolean canGoForward() {
			if (!super.canGoForward()) {
				return false;
			}
			return !hasWall(getPos(), getDir());
		}

		@Override
		protected void onVisit() {
			setDistance(getPos(), ++lastDistance);
		}

		@Override
		protected void onBacktrack() {
			lastDistance = getDistance(getPos());
		}

		private void dijkstra() throws MazeException {
			HashSet<Coordinate> Q = new HashSet<Coordinate>();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					Q.add(new Coordinate(x, y));
					setDistance(new Coordinate(x, y), infinity);
				}
			}
			setDistance(startPos, 1);
			while (!Q.isEmpty()) {
				Coordinate min = null;
				int minDist = infinity;
				for (Coordinate c : Q) {
					if (getDistance(c) < minDist) {
						min = c;
						minDist = getDistance(c);
					}
				}
				if (min == null) {
					throw new MazeException("not all cells were visited");
				}
				Q.remove(min);
				int dist = getDistance(min);
				for (Direction dir : Direction.values()) {
					if (isInBounds(min.translate(dir)) && !hasWall(min, dir)) {
						if (dist + 1 < getDistance(min.translate(dir))) {
							setDistance(min.translate(dir), dist + 1);
						}
					}
				}
			}
		}
	}

}
