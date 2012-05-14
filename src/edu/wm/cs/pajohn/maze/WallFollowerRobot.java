package edu.wm.cs.pajohn.maze;

public class WallFollowerRobot extends Robot {

    public WallFollowerRobot(Maze maze, Coordinate pos, Direction dir) {
        super(maze, pos, dir, 50000);
    }

    @Override
    public void nextAction() {
        if (leftIsClear()) {
            makeMove(Direction.LEFT);
        } else if (frontIsClear()) {
            move();
        } else {
            rotateRight();
        }
    }

}
