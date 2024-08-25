package com.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Demon extends Entity {
    public int myRow;
    public int myCol;

    private int hasNotEaten;

    private int sinceMult;
    private int turns;

    public Demon(int r, int c) {
        myRow = r;
        myCol = c;
        hasNotEaten = 0;
        sinceMult = 0;
        turns = 0;

    }

    public Demon(int r, int c, int passedTurns) {
        myRow = r;
        myCol = c;
        hasNotEaten = 0;
        sinceMult = 0;
        turns = passedTurns;
    }


    @Override
    public void move(Entity[][] board, int currentTurn) {
        if (turns >= currentTurn)
            return;
        // move towards the nearest orthogonal soldier, if any
        // and if we can (nothing blocking)
        int[] direction = findNearestOrthogonalSoldier(board);
        if (direction == null) {
            // we have to find the farthest edge
            direction = findFarthestEdge(board);
        }
        // now we have a direction we want to move and will move to that direction if possible
        int newRow = myRow + direction[0];
        int newCol = myCol + direction[1];
        sinceMult++;
        turns++;
        if (newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length) {
            // we are in bounds and will attempt to move
            // if demon then we are blocked
            if (board[newRow][newCol] instanceof Demon) {
                hasNotEaten++;
                return;
            }
            // if soldier then update coordinates while eating(deleting) soldier and making previous space null
            if (board[newRow][newCol] instanceof Soldier) {
                hasNotEaten = 0;

                board[newRow][newCol] = this;
                int oldRow = myRow;
                int oldCol = myCol;
                myCol = newCol;
                myRow = newRow;
                board[oldRow][oldCol] = null;
            }
            // if null then move to empty space while making previous space null
            if (board[newRow][newCol] == null) {
                hasNotEaten++;

                board[newRow][newCol] = this;
                int oldRow = myRow;
                int oldCol = myCol;
                myCol = newCol;
                myRow = newRow;
                board[oldRow][oldCol] = null;
            }

        } else {
            // else we were attempting to move out of bounds, so we will stay still and update hunger
            hasNotEaten++;
        }

    }

    private int[] findFarthestEdge(Entity[][] board) {
        // go in direction of n e s w then sort by distance similar to move()
        // and return first element
        List<int[]> farthestEdges = new ArrayList<>();
        // north
        int distance = myRow;
        farthestEdges.add(new int[] {-1, 0, distance});

        //east
        distance = board[0].length - 1 - myCol;
        farthestEdges.add(new int[] {0, 1, distance});

        //south
        distance = board.length - 1 - myRow;
        farthestEdges.add(new int[] {1, 0, distance});

        //west
        distance = myCol;
        farthestEdges.add(new int[] {0, -1, distance});

        farthestEdges.sort(Comparator.comparingInt((int[] x) -> x[2]).reversed());
        int[] arr = farthestEdges.get(0);
        return new int[] {arr[0], arr[1]};
    }

    private int[] findNearestOrthogonalSoldier(Entity[][] board) {
        int minOrthogonalDistance = Integer.MAX_VALUE;
        boolean orthogonalSoldierFound = false;

        // check horizontal direction for orthogonal soldier and min distance and max neighbors
        for (int col = 0; col < board[0].length; col++) {
            if (board[myRow][col] instanceof Soldier) {
                orthogonalSoldierFound = true;

                int distance = Math.abs(col - myCol);
                if (distance < minOrthogonalDistance)
                    minOrthogonalDistance = distance;
            }
        }

        // check vertical direction for orthogonal soldier and min distance and max neighbors
        for (int row = 0; row < board.length; row++) {
            if (board[row][myCol] instanceof Soldier) {
                orthogonalSoldierFound = true;

                int distance = Math.abs(row - myRow);
                if (distance < minOrthogonalDistance)
                    minOrthogonalDistance = distance;
            }
        }

        // if no orthogonal soldier found return false
        if (!orthogonalSoldierFound)
            return null;

        // count how many soldiers are in minimumDistance
        // adding them to a list in order of N E S W and keeping track of how many neighbors they have
        List<int[]> minDistSoldierList = new ArrayList<>();
        int[][] directions = {{-1, 0}, {0, 1},
                                {1, 0}, {0, -1}};
        for (int[] l:directions) {
            int newRow = myRow + l[0] * minOrthogonalDistance;
            int newCol = myCol + l[1] * minOrthogonalDistance;
            if (newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length
                    && board[newRow][newCol] instanceof Soldier) {
                int neighbors = ((Soldier) board[newRow][newCol]).numberOfNeighbors(board);
                minDistSoldierList.add(new int[]{l[0], l[1], neighbors});
            }
        }

        // if only one soldier in minimum distance return direction to move
        if (minDistSoldierList.size() == 1) {
            int[] arr = minDistSoldierList.get(0);
            return new int[] {arr[0], arr[1]};
        }

        // else sort by number of neighbors if all elements contain same number of neighbors
        // we can traverse in order because of stable sort, and they are in order of n e s w
        // and return the first element
        minDistSoldierList.sort(Comparator.comparingInt((int[] x) -> x[2]).reversed());
        int[] arr = minDistSoldierList.get(0);
        return new int[] {arr[0], arr[1]};


    }

    @Override
    public void mult(Entity[][] board) {
        // if it is time to breed then breed in order of clockwise
        // if no empty spaces no breeding
        if (sinceMult == 8) {

            int[][] directions = {{-1, 0}, {0, 1},
                    {1, 0}, {0, -1}};

            for (int[] l: directions) {
                int newRow = myRow + l[0];
                int newCol = myCol + l[1];
                if (newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length
                        && board[newRow][newCol] == null) {
                    Demon newDemon = new Demon(newRow, newCol, turns);
                    board[newRow][newCol] = newDemon;
                    sinceMult = 0;
                    return;
                }
            }

            sinceMult = 0;

        }
    }

    public void starve(Entity[][] board) {
        if (hasNotEaten == 5) {
            board[myRow][myCol] = null;
        }
    }
}
