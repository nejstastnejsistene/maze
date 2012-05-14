package edu.wm.cs.pajohn.maze;

/**
 * <p>
 * A class to represent the cardinal directions. A {@link Direction} is
 * represented by vertical and horizontal components (dx and dy). As usual in
 * computer representations the y-axis is flipped so NORTH and SOUTH have
 * vertical components of -1 and 1 respectively. One assumption made with this
 * class is that the directions are ordered in increasing angular displacement
 * starting at 0 degrees (east), allowing quick calculations between directions
 * in {@link #rotate(int)}.
 * </p>
 * <p>
 * A tricky thing about this class is the distinction between absolute and
 * relative directions. Using a {@link Robot} for example, consider a robot
 * facing north, with a wall to its left. The relative direction of the wall is
 * left, and the absolute direction is west. You can convert between absolute
 * and relative directions by using the {@link #toAbsolute(Direction)} and
 * {@link #toRelative(Direction)} methods. Both forms of directions are
 * interchangable (the relative directional constants are aliases for the
 * absolute directions), so care should be taken not to confused them.
 * </p>
 */
public enum Direction {

	/** Absolute direction representing east. **/
	EAST(1, 0),
	/** Absolute direction representing north. **/
	NORTH(0, -1),
	/** Absolute direction representing west. **/
	WEST(-1, 0),
	/** Absolute direction representing south. **/
	SOUTH(0, 1), ;

	/** Relative alias for EAST. */
	public static final Direction RIGHT = EAST;
	/** Relative alias for NORTH. */
	public static final Direction FORWARD = NORTH;
	/** Relative alias for WEST. */
	public static final Direction LEFT = WEST;
	/** Relative alias for SOUTH. */
	public static final Direction BACKWARD = SOUTH;

	/** The horizontal component. */
	private int dx;
	/** The vertical component. */
	private int dy;

	/**
	 * Creates a new direction given horizontal and vertical components.
	 * 
	 * @param dx
	 *            Horizontal component.
	 * @param dy
	 *            Vertical component.
	 */
	private Direction(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	/**
	 * Returns <code>dx</code>.
	 * 
	 * @return The horizontal displacement.
	 */
	public int getDX() {
		return dx;
	}

	/**
	 * Returns <code>dy</code>.
	 * 
	 * @return The vertical displacement.
	 */
	public int getDY() {
		return dy;
	}

	/**
	 * Returns the angular displacement of this direction vector.
	 * 
	 * @return <code>(Math.PI / 2) * ordinal()</code>
	 */
	public double getAngle() {
		return (Math.PI / 2) * ordinal();
	}

	/**
	 * Returns whether this direction is horizontal, with the assumption that
	 * only the four cardinal directions are represented.
	 * 
	 * @return <code>getDX() != 0></code>
	 */
	public boolean isHorizontal() {
		return dx != 0;
	}

	/**
	 * Returns whether this direction is vertical, with the assumption that only
	 * the four cardinal directions are represented.
	 * 
	 * @return <code>getDY() != 0></code>
	 */
	public boolean isVertical() {
		return dy != 0;
	}

	/**
	 * Returns this direction rotated <code>90 * turns</code> degrees.
	 * 
	 * @param turns
	 *            The number of times to rotate this direction.
	 * @return This direction rotated <code>90 * turns</code> degrees.
	 */
	public Direction rotate(int turns) {
		while (turns < 0) {
			turns += 4;
		}
		return values()[(ordinal() + turns) % 4];
	}

	/**
	 * Returns this direction rotated counter-clockwise.
	 * 
	 * @return This direction rotated 90 degrees.
	 */
	public Direction rotateLeft() {
		return rotate(1);
	}

	/**
	 * Returns this direction rotated clockwise.
	 * 
	 * @return This direction rotated -90 degrees.
	 */
	public Direction rotateRight() {
		return rotate(3);
	}

	/**
	 * Returns the opposite of this direction.
	 * 
	 * @return This direction rotated 180 degrees.
	 */
	public Direction opposite() {
		return rotate(2);
	}

	/**
	 * Converts a relative direction to an absolute direction with respect to
	 * <code>refDir</code>.
	 * 
	 * @param refDir
	 *            The reference direction. This is usually the direction a
	 *            {@link MazeCrawler} is facing.
	 * 
	 * @return The absolute direction of this relative direction with respect to
	 *         <code>refDir</code>.
	 */
	public Direction toAbsolute(Direction refDir) {
		return rotate(refDir.ordinal() - 1);
	}

	/**
	 * Converts an absolute direction to a relative direction with respect to
	 * <code>refDir</code>.
	 * 
	 * @param refDir
	 *            The reference direction. This is usually the direction a
	 *            {@link MazeCrawler} is facing.
	 * @return The relative direction of this absolute direction with respect to
	 *         <code>refDir</code>.
	 */
	public Direction toRelative(Direction refDir) {
		return rotate(1 - refDir.ordinal());
	}

	/**
	 * Returns a random {@link Direction} value.
	 * 
	 * @return Randomly one of EAST, NORTH, WEST, or SOUTH.
	 */
	public static Direction random() {
		int index = (int) (Math.random() * 4);
		return values()[index];
	}

}
