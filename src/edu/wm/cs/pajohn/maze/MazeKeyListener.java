package edu.wm.cs.pajohn.maze;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class MazeKeyListener {

    private ArrayList<Direction> keyStack;
    private Robot robot;

    public void RobotKeyListener(Robot robot) {
        keyStack = new ArrayList<Direction>();
        this.robot = robot;
    }

    public void toggleKey(int keyCode, boolean pressed) {
        Direction dir = getDirection(keyCode);
        if (dir != null) {
            keyStack.remove(dir);
            if (pressed) {
                keyStack.add(0, dir);
            }
            if (keyStack.size() > 0) {
                robot.keyPress(keyStack.get(0));
            }
        } else {
            // non directional keys here
        }
    }

    private Direction getDirection(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                return Direction.LEFT;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                return Direction.RIGHT;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                return Direction.FORWARD;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                return Direction.BACKWARD;
            default:
                return null;
        }
    }

}
