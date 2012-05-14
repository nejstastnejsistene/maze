package edu.wm.cs.pajohn.maze;

public abstract class Robot extends BasicRobot implements Runnable {

    private boolean manual;
    private int battery;

    private Thread thread;
    private boolean finished;

    public Robot(Maze maze, Coordinate pos, Direction dir, boolean manual) {
        super(maze, pos, dir);
        this.manual = manual;
    }

    public Robot(Maze maze, Coordinate pos, Direction dir) {
        this(maze, pos, dir, 2500);
    }

    public Robot(Maze maze, Coordinate pos, Direction dir, int battery) {
        super(maze, pos, dir);
        this.manual = false;
        this.battery = battery;
    }

    public void keyPress(Direction dir) {

    }

    public boolean isManual() {
        return manual;
    }

    public int getBattery() {
        return battery;
    }

    private boolean consumeBattery(int units) {
        if (manual) {
            return true;
        }
        if (battery < units) {
            battery = 0;
            return false;
        }
        battery -= units;
        return true;
    }

    private boolean isClear(Direction relDir) {
        if (consumeBattery(1)) {
            return !getMaze().hasWall(getPos(), relDir.toAbsolute(getDir()));
        }
        return true;
    }

    public boolean frontIsClear() {
        return isClear(Direction.FORWARD);
    }

    public boolean leftIsClear() {
        return isClear(Direction.LEFT);
    }

    @Override
    public void move(Direction dir) {
        if (consumeBattery(5)) {
            super.move(dir);
        }
    }

    @Override
    public void setDir(Direction dir) {
        if (consumeBattery(3)) {
            super.setDir(dir);
        }
    }

    public void makeMove(Direction relDir) {
        if (relDir == Direction.FORWARD) {
            move();
        } else if (relDir == Direction.BACKWARD) {
            move(getDir().opposite());
        } else if (relDir == Direction.LEFT) {
            rotateLeft();
            move();
        } else if (relDir == Direction.RIGHT) {
            rotateRight();
            move();
        }
    }

    public void solveMaze() throws MazeException {
        if (manual) {
            throw new MazeException("robot is set to solve maze manually");
        }
        thread = new Thread(this);
        thread.start();
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public void run() {
        finished = false;
        while (!finished) {
            nextAction();
            finished = !(getMaze().isInBounds(getPos()) && battery > 0);
        }
    }

    public abstract void nextAction();

}
