package edu.wm.cs.pajohn.maze;

public class BasicRobot extends MazeCrawler {

	public static double DEFAULT_SPEED = 2;
	public static int DEFAULT_STEPS = 10;

	private boolean moving, turning;
	private double x, y, angle;
	private double speed;
	private int steps;
	private long sleepTime;
	private int currentStep;

	public BasicRobot(Maze maze, Coordinate pos, Direction dir) {
		super(maze, pos, dir);
		speed = DEFAULT_SPEED;
		steps = DEFAULT_STEPS;
		sleepTime = (long) (1000 / speed / steps);
	}

	public boolean isMoving() {
		return moving;
	}

	public double getProgress() {
		return (float) currentStep / steps;
	}

	public boolean isTurning() {
		return turning;
	}
	
	public Coordinate getOrigPos() {
		return super.getPos();
	}
	
	@Override
	public Coordinate getPos() {
		return moving ? new Coordinate(x, y) : super.getPos();
	}

	public double getAngle() {
		return turning ? angle : getDir().getAngle();
	}

	public void setSpeed(double speed) {
		this.speed = speed;
		sleepTime = (long) (1000 / speed / steps);
	}

	private void sleep() {
		sleep(sleepTime);
	}
	
	private void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void move(Direction dir) {
		if (getMaze().hasWall(getPos(), dir)) {
			sleep(steps * sleepTime);
		} else {
			getPos().assertIntValued();
			int origX = (int) getPos().getX();
			int origY = (int) getPos().getY();
			moving = true;
			for (currentStep = 0; currentStep < steps; currentStep++) {
				x = origX + dir.getDX() * getProgress();
				y = origY + dir.getDY() * getProgress();
				sleep();
			}
			super.move(dir);
			moving = false;
		}

	}

	private void rotate(int turns) {
		double origAngle = getDir().getAngle();
		turning = true;
		for (currentStep = 0; currentStep < steps; currentStep++) {
			angle = origAngle + turns * Math.PI / 2 * getProgress();
			sleep();
		}
		setDir(getDir().rotate(turns));
		turning = false;
	}

	@Override
	public void rotateLeft() {
		rotate(1);
	}

	@Override
	public void rotateRight() {
		rotate(-1);
	}

}
