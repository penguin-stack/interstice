import java.util.ArrayList;
import java.util.List;

public class Soldier extends Entity {
    public int myRow;
    public int myCol;

    private int turns;
    private int sinceMult;



    public Soldier(int r, int c) {
        myRow = r;
        myCol = c;
        turns = 0;
        sinceMult = 0;

    }

    public Soldier(int r, int c, int passedTurns) {
        myRow = r;
        myCol = c;
        turns = passedTurns;
        sinceMult = 0;
    }

    @Override
    public void move(Entity[][] board, int currentTurn) {
        if (turns >= currentTurn)
            return;


        turns++;
        sinceMult++;
        // move away from the nearest orthogonal demon, if any
        // and if we can (nothing blocking)
        int[] direction = findNearestOrthogonalDemon(board);
        if (direction == null) {
            // if no nearest orthogonal demon stand still
            return;
        }


        // now we have a direction we want to move and will move to that direction if possible
        int newRow = myRow + direction[0];
        int newCol = myCol + direction[1];
        if (newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length) {
            // we are in bounds
            // if demon or soldier then we are blocked
            if (board[newRow][newCol] instanceof Demon || board[newRow][newCol] instanceof Soldier) {
                return;
            }
            // if null then move
            if (board[newRow][newCol] == null) {

                board[newRow][newCol] = this;
                int oldRow = myRow;
                int oldCol = myCol;
                myCol = newCol;
                myRow = newRow;
                board[oldRow][oldCol] = null;
            }
        }
        // else we tried to move out of bounds

    }

    private int[] findNearestOrthogonalDemon(Entity[][] board) {
        int minOrthogonalDistance = Integer.MAX_VALUE;
        int maxOrthogonalDistance = Integer.MIN_VALUE;
        boolean orthogonalDemonFound = false;

        // check horizontal direction for orthogonal demon and min/max distance
        // east
        for (int col = myCol; col < board[0].length; col++) {
            if (board[myRow][col] instanceof Demon) {
                orthogonalDemonFound = true;

                int distance = Math.abs(col - myCol);
                minOrthogonalDistance = distance;
                maxOrthogonalDistance = distance;
                break;
            }
        }

        //west
        for (int col = myCol; col >= 0; col--) {
            if (board[myRow][col] instanceof Demon) {
                orthogonalDemonFound = true;

                int distance = Math.abs(col - myCol);
                if (distance < minOrthogonalDistance)
                    minOrthogonalDistance = distance;
                if (distance > maxOrthogonalDistance)
                    maxOrthogonalDistance = distance;
                break;
            }
        }


        // check vertical direction for orthogonal soldier and min/max distance
        // south
        for (int row = myRow; row < board.length; row++) {
            if (board[row][myCol] instanceof Demon) {
                orthogonalDemonFound = true;

                int distance = Math.abs(row - myRow);
                if (distance < minOrthogonalDistance)
                    minOrthogonalDistance = distance;
                if (distance > maxOrthogonalDistance)
                    maxOrthogonalDistance = distance;
                break;
            }
        }

        // north
        for (int row = myRow; row >= 0; row--) {
            if (board[row][myCol] instanceof Demon) {
                orthogonalDemonFound = true;

                int distance = Math.abs(row - myRow);
                if (distance < minOrthogonalDistance)
                    minOrthogonalDistance = distance;
                if (distance > maxOrthogonalDistance)
                    maxOrthogonalDistance = distance;
                break;
            }
        }

        // if no orthogonal demon found return false
        if (!orthogonalDemonFound)
            return null;


        // count how many demons are within minimum distance
        int[][] directions = {{-1, 0}, {0, 1},
                {1, 0}, {0, -1}};
        List<int[]> minDistDemons = new ArrayList<>();
        for (int[] l:directions) {
            int newRow = myRow + l[0] * minOrthogonalDistance;
            int newCol = myCol + l[1] * minOrthogonalDistance;
            if (newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length
                    && board[newRow][newCol] instanceof Demon) {
                minDistDemons.add(new int []{l[0], l[1]});
            }
        }

        // if only one demon within minimum distance then move away from it
        if (minDistDemons.size() == 1) {
            return awayFrom(minDistDemons.get(0));
        }
        // else we have multiple demons within minimum distance
        // check to see if we have a clear pathway add them to a list in direction of n e s w

        List<int[]> clearPathways = findClearPathways(board);
        if (clearPathways.size() > 0) {
            // if we have clear pathways take the first one since they are ordered already
            return clearPathways.get(0);
        }

        // else we have no clear pathways and demons surrounding us in every direction
        // we want to move toward the furthest demon, if there are multiple then in order by n e s w
        List<int[]> farthestDemons = findFurthestDemons(board, maxOrthogonalDistance);


        // return the first furthest demon since they are ordered already
        return farthestDemons.get(0);


    }

    private List<int[]> findFurthestDemons(Entity[][] board, int maxOrthogonalDistance) {
        List<int[]> res = new ArrayList<>();
        //north
        for (int row = myRow; row >= 0; row--) {
            if(board[row][myCol] instanceof Demon) {
                if (Math.abs(row - myRow) == maxOrthogonalDistance) {
                    res.add(new int[]{-1, 0});
                }
                break;
            }
        }
        // east
        for (int col = myCol; col < board[0].length; col++) {
            if(board[myRow][col] instanceof Demon) {
                if (Math.abs(col - myCol) == maxOrthogonalDistance) {
                    res.add(new int[]{0, 1});
                }
                break;
            }
        }
        // south
        for (int row = myRow; row < board.length; row++) {
            if(board[row][myCol] instanceof Demon) {
                if (Math.abs(row - myRow) == maxOrthogonalDistance) {
                    res.add(new int[]{1, 0});
                }
                break;
            }
        }
        //north
        for (int col = myCol; col >= 0; col--) {
            if(board[myRow][col] instanceof Demon) {
                if (Math.abs(col - myCol) == maxOrthogonalDistance) {
                    res.add(new int[]{0, -1});
                }
                break;
            }
        }


        return res;
    }

    private List<int[]> findClearPathways(Entity[][] board) {
        List<int[]> res = new ArrayList<>();
        // north
        boolean demonFound = false;
        for (int row = myRow; row >= 0; row--) {
            if (board[row][myCol] instanceof Demon) {
                demonFound = true;
                break;
            }
        }
        if (!demonFound) {
            res.add(new int[]{-1, 0});
        }

        // east
        demonFound = false;
        for (int col = myCol; col < board[0].length; col++) {
            if (board[myRow][col] instanceof Demon) {
                demonFound = true;
                break;
            }
        }
        if (!demonFound) {
            res.add(new int[]{0, 1});
        }

        // south
        demonFound = false;
        for (int row = myRow; row < board[0].length; row++) {
            if (board[row][myCol] instanceof Demon) {
                demonFound = true;
                break;
            }
        }
        if (!demonFound) {
            res.add(new int[]{1, 0});
        }

        // west
        demonFound = false;
        for (int col = myCol; col >= 0; col--) {
            if (board[myRow][col] instanceof Demon) {
                demonFound = true;
                break;
            }
        }
        if (!demonFound) {
            res.add(new int[]{0, -1});
        }

        return res;
    }

    private int[] awayFrom(int[] arr) {
        arr[0] = arr[0] == 0 ? 0 : -arr[0];
        arr[1] = arr[1] == 0 ? 0 : -arr[1];
        return arr;
    }

    @Override
    public void mult(Entity[][] board) {
        // if it is time to breed then breed in order of clockwise
        // if no empty spaces no breeding
        if (sinceMult == 3) {

            int[][] directions = {{-1, 0}, {0, 1},
                    {1, 0}, {0, -1}};

            for (int[] l: directions) {
                int newRow = myRow + l[0];
                int newCol = myCol + l[1];
                if (newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length
                        && board[newRow][newCol] == null) {
                    Soldier newSoldier = new Soldier(newRow, newCol, turns);
                    board[newRow][newCol] = newSoldier;
                    sinceMult = 0;
                    return;
                }
            }

            sinceMult = 0;

        }
    }

    public int numberOfNeighbors(Entity[][] board) {
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1},
                            {0, -1}, {0, 1},
                            {1, -1}, {1, 0}, {1, 1}};

        int count = 0;
        for (int[] l: directions) {
            int newRow = myRow + l[0];
            int newCol = myCol + l[1];
            if (newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length
                    && board[newRow][newCol] instanceof Soldier)
                count++;
        }
        return count;
    }
}
