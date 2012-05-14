package edu.wm.cs.pajohn.maze;

public class WizardRobot extends Robot {

    public WizardRobot(Maze maze, Coordinate pos, Direction dir) {
        super(maze, pos, dir, 50000);
    }

    @Override
    public void nextAction() {
        Direction absDir = MazeSolution.nextDir(getMaze(), getPos());
        makeMove(absDir.toRelative(getDir()));
    }

}
