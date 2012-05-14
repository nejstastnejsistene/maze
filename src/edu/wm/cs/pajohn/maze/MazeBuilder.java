package edu.wm.cs.pajohn.maze;

public class MazeBuilder implements Runnable {

    private Thread thread;
    private MazeBuildingAlgorithm algorithm;
    private Maze maze;
    protected Coordinate startPos;
    protected Direction startDir;

    public MazeBuilder(MazeBuildingAlgorithm algorithm){
    	this.algorithm = algorithm;
    }
    
    public void buildMaze(int width, int height) throws MazeException {
        if (thread != null) {
            throw new MazeException("can only build one maze at a time");
        } else if (!(width > 0 && height > 0)) {
            throw new MazeException("invalid maze size");
        }
        chooseStart(width, height);
        maze = new Maze(width, height);
        thread = new Thread(this);
        thread.start();
    }

    protected void chooseStart(int width, int height) {
        boolean xAxis = Math.random() < 0.5;
        boolean lowSide = Math.random() < 0.5;
        int startX, startY;
        if (xAxis) {
            startX = (int) (Math.random() * width);
            if (lowSide) {
                startY = 0;
                startDir = Direction.SOUTH;
            } else {
                startY = height - 1;
                startDir = Direction.NORTH;
            }
        } else {
            startY = (int) (Math.random() * height);
            if (lowSide) {
                startX = 0;
                startDir = Direction.EAST;
            } else {
                startX = width - 1;
                startDir = Direction.WEST;
            }
        }
        startPos = new Coordinate(startX, startY);
    }

    public Maze getMaze() throws MazeException {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread = null;
        try {
            maze.removeWall(startPos, startDir.opposite());
            maze.calculateDistances();
        } catch (MazeException e) {
            throw e;
        }
        return maze;
    }

    @Override
    public void run() {
        algorithm.generate(maze, startPos, startDir);
    }

}
