package edu.wm.cs.pajohn.maze;

public class ManualRobot extends Robot {

    public ManualRobot(Maze maze, Coordinate pos, Direction dir) {
        super(maze, pos, dir, true);
    }

    @Override
    public void nextAction() {

    }

    @Override
    public void keyPress(Direction dir) {
        makeMove(dir);
    }

}
