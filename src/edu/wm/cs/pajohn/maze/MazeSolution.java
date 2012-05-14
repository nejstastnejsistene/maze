package edu.wm.cs.pajohn.maze;

import java.util.Iterator;

public class MazeSolution implements Iterable<Coordinate> {

    public static Direction nextDir(Maze maze, Coordinate pos) {
    	pos.assertIntValued();
        int minDist = -1;
        Direction shortest = null;
        for (Direction dir : Direction.values()) {
            if (!maze.hasWall(pos, dir)) {
                if (!maze.isInBounds(pos.translate(dir))) {
                    return dir;
                }
                int dist = maze.getDistance(pos.translate(dir));
                if (minDist == -1 || dist < minDist) {
                    minDist = dist;
                    shortest = dir;
                }
            }
        }
        return shortest;
    }

    private Maze maze;
    private Coordinate start, end;
    private Coordinate[] path;

    public MazeSolution(Maze maze, Coordinate pos) {
    	pos.assertIntValued();
        this.maze = maze;
        start = pos;
        path = new Coordinate[maze.calcMaxDistance()];
        while (maze.isInBounds(pos)) {
            add(pos);
            pos = pos.translate(nextDir(maze, pos));
        }
        end = path[0];
    }

    public Coordinate getStart() {
        return start;
    }

    public Coordinate getEnd() {
        return end;
    }

    private int hash(Coordinate pos) {
        return maze.getDistance(pos) - 1;
    }

    public boolean contains(Coordinate pos) {
        if (!maze.isInBounds(pos)) {
            return false;
        }
        Coordinate c = path[hash(pos)];
        return c != null && c.equals(pos);
    }

    private void add(Coordinate pos) {
        path[hash(pos)] = pos;
    }

    private void clear() {
        clear(0);
    }

    private void clear(int start) {
        for (int i = start; i < path.length; i++) {
            path[i] = null;
        }
    }

    public void update(Coordinate pos) {
    	pos.assertIntValued();
        start = pos;
        if (!maze.isInBounds(pos)) {
            clear();
            start = null;
        } else if (contains(pos)) {
            clear(hash(pos) + 1);
        } else {
            while (maze.isInBounds(pos) &&
                    (path[hash(pos)] == null || !contains(pos))) {
                add(pos);
                pos = pos.translate(nextDir(maze, pos));
            }
        }
        end = path[0];
    }

    @Override
	public Iterator<Coordinate> iterator() {
        return new SolutionIterator(this);
    }

    private class SolutionIterator implements Iterator<Coordinate> {

        private MazeSolution solution;
        private int index;

        public SolutionIterator(MazeSolution solution) {
            this.solution = solution;
            index = solution.path.length;
            while (solution.path[--index] == null);
        }

        @Override
		public boolean hasNext() {
            return index >= 0;
        }

        @Override
		public Coordinate next() {
            return solution.path[index--];
        }

        @Override
		public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
