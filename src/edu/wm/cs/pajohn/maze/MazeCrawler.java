package edu.wm.cs.pajohn.maze;

public class MazeCrawler {

	private Maze maze;
	private Coordinate pos;
	private Direction dir;

	public MazeCrawler(Maze maze, Coordinate pos, Direction dir) {
		this.maze = maze;
		this.pos = pos;
		this.dir = dir;
	}

	protected Maze getMaze() {
		return maze;
	}

	public Coordinate getPos() {
		return pos;
	}

	public void move() {
		move(getDir());
	}

	public void move(Direction dir) {
		pos = pos.translate(dir);
	}

	public Direction getDir() {
		return dir;
	}

	public void setDir(Direction dir) {
		this.dir = dir;
	}

	public void rotateLeft() {
		setDir(dir.rotateLeft());
	}

	public void rotateRight() {
		setDir(dir.rotateRight());
	}

}
