package edu.wm.cs.pajohn.maze;

public abstract class MazeBackTracker extends MazeCrawler {

    private Coordinate startPos;
    private Direction origDir;
    private Direction[][] origDirs;
    private boolean[][] visited;
    private boolean finished;

    public MazeBackTracker(Maze maze, Coordinate startPos) {
        this(maze, startPos, Direction.random());
    }

    public MazeBackTracker(Maze maze, Coordinate startPos, Direction startDir) {
        super(maze, startPos, startDir);
        this.startPos = startPos;
        origDir = startDir;
        origDirs = new Direction[maze.getHeight()][maze.getWidth()];
        visited = new boolean[maze.getHeight()][maze.getWidth()];
        markVisited();
    }

    public void iterate() {
        finished = false;
        while(!finished) {
            finished = next();
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void interrupt() {
        finished = true;
    }

    public boolean next() {
        if (canGoForward()) {
            move();
            onVisit();
            markVisited();
            markOrigDir();
            setDir(nextDir());
            origDir = getDir();
        } else {
            rotateLeft();
            if (getDir() == origDir) {
                if (getPos().equals(startPos)) {
                    return true;
                }
                onBacktrack();
                move(getOrigDir().opposite());
                setDir(nextDir());
                origDir = getDir();
            }
        }
        return false;
    }

    private boolean isVisited(Coordinate c) {
    	c.assertIntValued();
        return visited[(int) c.getY()][(int) c.getX()];
    }

    private void markVisited() {
        Coordinate c = getPos();
    	c.assertIntValued();
        visited[(int) c.getY()][(int) c.getX()] = true;
    }

    private Direction getOrigDir() {
        Coordinate c = getPos();
    	c.assertIntValued();
        return origDirs[(int) c.getY()][(int) c.getX()];
    }

    private void markOrigDir() {
        Coordinate c = getPos();
    	c.assertIntValued();
        origDirs[(int) c.getY()][(int) c.getX()] = getDir();
    }
    
    protected boolean canGoForward() {
        if (getMaze().hasBoundary(getPos(), getDir())) {
            return false;
        }
        return !isVisited(getPos().translate(getDir()));
    }

    protected abstract void onVisit();
    protected abstract void onBacktrack();

    protected Direction nextDir() {
        return getDir().rotateLeft();
    }

    public boolean allVisited() {
        for (int j = 0; j < visited.length; j++) {
            for (int i = 0; i < visited[j].length; i++) {
                if (!visited[j][i]) {
                    return false;
                }
            }
        }
        return true;
    }

}
