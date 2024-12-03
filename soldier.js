import { Demon } from './demon.js';

export class Soldier {
    constructor(r, c) {
        this.r = r;
        this.c = c;
        this.sinceMult = 0;
        this.turns = 0;
    }

    move(board, turn) {
        if (this.turns >= turn) {
            return;
        }

        this.turns += 1;
        this.sinceMult += 1;

        let direction = this.findDirection(board);
        if (direction === -1) {
            return;
        }

        let newRow = this.r + direction[0];
        let newCol = this.c + direction[1];

        if (newRow >= 0 && newRow < 10 && newCol >= 0 && newCol < 10) {
            if (board[newRow][newCol] instanceof Demon || board[newRow][newCol] instanceof Soldier) {
                return;
            }

            if (board[newRow][newCol] === null) {
                board[newRow][newCol] = this;
                let oldRow = this.r;
                let oldCol = this.c;
                this.c = newCol;
                this.r = newRow;
                board[oldRow][oldCol] = null;
            }
        }
    }

    numberOfNeighbors(board) {
        let directions = [
            [-1, 0], [-1, 1], [0, 1], [1, 1],
            [1, 0], [1, -1], [0, -1], [-1, -1]
        ];
        let count = 0;

        directions.forEach(([dr, dc]) => {
            let neighbor_row = this.r + dr;
            let neighbor_col = this.c + dc;
            if (neighbor_row >= 0 && neighbor_row < 10 && neighbor_col >= 0 && neighbor_col < 10) {
                if (board[neighbor_row][neighbor_col] instanceof Soldier) {
                    count += 1;
                }
            }
        });
        
        return count;
    }

    findDirection(board) {
        const directions = [
            [-1, 0], // North
            [0, 1],  // East
            [1, 0],  // South
            [0, -1]  // West
        ];
    
        let nearestDemons = [];
        let furthestDemons = [];
        const clearPathways = [];
        let minDistance = Infinity;
        let maxDistance = -1;
    
        directions.forEach(([dr, dc]) => {
            let r = this.r;
            let c = this.c;
            let distance = 0;
            let foundDemon = false;
    
            // Move in the current direction until boundary or demon is found
            while (r >= 0 && r < 10 && c >= 0 && c < 10) {
                distance += 1;
                r += dr;
                c += dc;
    
                if (r >= 0 && r < 10 && c >= 0 && c < 10 && board[r][c] instanceof Demon) { // Assuming Demon is a class
                    if (distance <= minDistance) {
                        minDistance = distance;
                        nearestDemons.push({ dr, dc, distance });
                    }
    
                    if (distance >= maxDistance) {
                        maxDistance = distance;
                        furthestDemons.push({ dr, dc, distance });
                    }
    
                    foundDemon = true;
                    break; // Stop searching in this direction
                }
            }
    
            if (distance > 0 && !foundDemon) {
                clearPathways.push({ dr, dc });
            }
        });

        nearestDemons = nearestDemons.filter(tup => tup.distance === minDistance);
        furthestDemons = furthestDemons.filter(tup => tup.distance === maxDistance);
        
        if (nearestDemons.length === 0) {
            return -1;
        }

        if (nearestDemons.length === 1) {
            const nearestDemon = nearestDemons[0];
            return this.awayFrom([nearestDemon.dr, nearestDemon.dc]);
        }

        if (clearPathways.length > 0) {
            const firstClearPathway = clearPathways[0];
            return [firstClearPathway.dr, firstClearPathway.dc];
        }

        if (furthestDemons.length === 0) {
            throw new Error("expected furthestDemons to have elements");
        }

        const furthestDemon = furthestDemons[0];
        return [furthestDemon.dr, furthestDemon.dc];
    
        // return { nearestDemons, furthestDemons, clearPathways };
    }

    awayFrom(tup) {
        let dr = 3;
        let dc = 3;
        if (tup[0] === 0) {
            dr = 0;
        } else {
            dr = -tup[0];
        }
        if (tup[1] === 0) {
            dc = 0;
        } else {
            dc = -tup[1];
        }
        return [dr, dc];
    }

    mult(board) {
        if (this.sinceMult === 3) {
            const directions = [
                [-1, 0], // North
                [0, 1],  // East
                [1, 0],  // South
                [0, -1]  // West
            ];
    
            for (const [dr, dc] of directions) {
                const newRow = this.r + dr;
                const newCol = this.c + dc;
    
                if (newRow >= 0 && newRow < 10 && newCol >= 0 && newCol < 10 && !board[newRow][newCol]) {
                    const newSoldier = new Soldier(newRow, newCol);
                    newSoldier.turns = this.turns;
                    board[newRow][newCol] = newSoldier;
                    this.sinceMult = 0;
                    return;
                }
            }
    
            this.sinceMult = 0;
        }
    }


}