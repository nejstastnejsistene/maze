package edu.wm.cs.pajohn.maze;

public class Coordinate {

	private final double x, y;

	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	public void assertIntValued() {
		if (!isIntValued()) {
			throw new AssertionError("not int valued");
		}
	}

	public boolean isIntValued() {
		return x % 1 == 0 && y % 1 == 0;
	}

	public Coordinate translate(Direction dir) {
		return new Coordinate(x + dir.getDX(), y + dir.getDY());
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Coordinate) {
			Coordinate c = (Coordinate) other;
			return x == c.x && y == c.y;
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

}
